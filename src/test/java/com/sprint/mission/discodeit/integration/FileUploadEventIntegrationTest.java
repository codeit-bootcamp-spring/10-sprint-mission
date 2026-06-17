package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.CreateUserRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class FileUploadEventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BinaryContentRepository binaryContentRepository;

    @Test
    @DisplayName("프로필 첨부 유저 생성 시 메타데이터는 즉시 커밋되고, 비동기로 바이너리 저장 후 status가 SUCCESS로 전이된다.")
    void createUserWithProfile_asyncStoresBinaryAndMarksSuccess() throws Exception {
        // given
        String fileName = "cp1-profile-" + System.nanoTime() + ".png";
        CreateUserRequestDto request =
                new CreateUserRequestDto("cp1User", "cp1@test.com", "password123", null);

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest", "request.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));
        MockMultipartFile profilePart = new MockMultipartFile(
                "profile", fileName, MediaType.IMAGE_PNG_VALUE, "fake-image-bytes".getBytes());

        // when: HTTP 응답은 비동기 저장을 기다리지 않고 즉시 반환된다(메타데이터 커밋)
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .file(profilePart)
                        .with(csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        // then 1: 메타데이터가 즉시 저장되어 있다
        BinaryContent created = binaryContentRepository.findAll().stream()
                .filter(bc -> fileName.equals(bc.getFileName()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("BinaryContent 메타데이터가 저장되지 않았습니다."));

        // then 2: 비동기 저장 완료 후 status가 SUCCESS로 전이된다 (sleep 3s 고려해 최대 15s 폴링)
        await().atMost(15, TimeUnit.SECONDS)
                .pollInterval(500, TimeUnit.MILLISECONDS)
                .untilAsserted(() -> {
                    BinaryContent latest = binaryContentRepository.findById(created.getId()).orElseThrow();
                    assertThat(latest.getStatus()).isEqualTo(BinaryContentStatus.SUCCESS);
                });
    }
}
