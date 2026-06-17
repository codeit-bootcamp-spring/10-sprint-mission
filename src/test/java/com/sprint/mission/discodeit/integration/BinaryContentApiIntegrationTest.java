package com.sprint.mission.discodeit.integration;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.dto.data.MessageDto;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class BinaryContentApiIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private BinaryContentService binaryContentService;

  @Autowired
  private UserService userService;

  @Autowired
  private ChannelService channelService;

  @Autowired
  private MessageService messageService;

  @Autowired
  private BinaryContentStorage binaryContentStorage;

  private RequestPostProcessor asUser(UUID userId, UserRole role) {
    UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, false, role);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "password");
    Authentication auth = new UsernamePasswordAuthenticationToken(
        userDetails, null, userDetails.getAuthorities());

    return authentication(auth);
  }

  private void setCurrentUser(UUID userId, UserRole role) {
    UserDto userDto = new UserDto(userId, "testuser", "test@example.com", null, false, role);
    DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "password");
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()));

  }

  @Test
  @WithMockUser(roles = "CHANNEL_MANAGER")
  @DisplayName("바이너리 컨텐츠 조회 API 통합 테스트")
  void findBinaryContent_Success() throws Exception {

    UserCreateRequest userRequest = new UserCreateRequest(
        "contentuser",
        "content@example.com",
        "Password1!"
    );
    UserDto user = userService.create(userRequest, Optional.empty());

    PublicChannelCreateRequest channelRequest = new PublicChannelCreateRequest(
        "테스트 채널",
        "테스트 채널 설명입니다."
    );
    var channel = channelService.create(channelRequest);

    MessageCreateRequest messageRequest = new MessageCreateRequest(
        "첨부파일이 있는 메시지입니다.",
        channel.id(),
        user.id()
    );

    byte[] fileContent = "테스트 파일 내용입니다.".getBytes();
    BinaryContentCreateRequest attachmentRequest = new BinaryContentCreateRequest(
        "test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        fileContent
    );

    setCurrentUser(user.id(), UserRole.USER);
    MessageDto message = messageService.create(messageRequest, List.of(attachmentRequest));
    UUID binaryContentId = message.attachments().get(0).id();

    mockMvc.perform(get("/api/binaryContents/{binaryContentId}", binaryContentId)
            .with(asUser(UUID.randomUUID(), UserRole.USER)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(binaryContentId.toString())))
        .andExpect(jsonPath("$.fileName", is("test.txt")))
        .andExpect(jsonPath("$.contentType", is(MediaType.TEXT_PLAIN_VALUE)))
        .andExpect(jsonPath("$.size", is(fileContent.length)));
  }

  @Test
  @DisplayName("존재하지 않는 바이너리 컨텐츠 조회 API 통합 테스트")
  void findBinaryContent_Failure_NotFound() throws Exception {

    UUID nonExistentBinaryContentId = UUID.randomUUID();

    mockMvc.perform(get("/api/binaryContents/{binaryContentId}", nonExistentBinaryContentId)
            .with(asUser(UUID.randomUUID(), UserRole.USER)))
        .andExpect(status().isNotFound());
  }

  @Test
  @WithMockUser(roles = "CHANNEL_MANAGER")
  @DisplayName("여러 바이너리 컨텐츠 조회 API 통합 테스트")
  void findAllBinaryContentsByIds_Success() throws Exception {

    UserCreateRequest userRequest = new UserCreateRequest(
        "contentuser2",
        "content2@example.com",
        "Password1!"
    );
    UserDto user = userService.create(userRequest, Optional.empty());

    PublicChannelCreateRequest channelRequest = new PublicChannelCreateRequest(
        "테스트 채널2",
        "테스트 채널 설명입니다."
    );
    var channel = channelService.create(channelRequest);

    MessageCreateRequest messageRequest = new MessageCreateRequest(
        "첨부파일이 있는 메시지입니다.",
        channel.id(),
        user.id()
    );

    BinaryContentCreateRequest attachmentRequest1 = new BinaryContentCreateRequest(
        "test1.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "첫 번째 테스트 파일 내용입니다.".getBytes()
    );

    BinaryContentCreateRequest attachmentRequest2 = new BinaryContentCreateRequest(
        "test2.txt",
        MediaType.TEXT_PLAIN_VALUE,
        "두 번째 테스트 파일 내용입니다.".getBytes()
    );

    setCurrentUser(user.id(), UserRole.USER);
    MessageDto message = messageService.create(
        messageRequest,
        List.of(attachmentRequest1, attachmentRequest2)
    );

    List<UUID> binaryContentIds = message.attachments().stream()
        .map(BinaryContentDto::id)
        .toList();

    mockMvc.perform(get("/api/binaryContents")
            .param("binaryContentIds", binaryContentIds.get(0).toString())
            .param("binaryContentIds", binaryContentIds.get(1).toString())
            .with(asUser(UUID.randomUUID(), UserRole.USER)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))
        .andExpect(jsonPath("$[*].fileName", hasItems("test1.txt", "test2.txt")));
  }

  @Test
  @DisplayName("바이너리 컨텐츠 다운로드 API 통합 테스트")
  void downloadBinaryContent_Success() throws Exception {

    String fileContent = "다운로드 테스트 파일 내용입니다.";
    BinaryContentCreateRequest createRequest = new BinaryContentCreateRequest(
        "download-test.txt",
        MediaType.TEXT_PLAIN_VALUE,
        fileContent.getBytes()
    );

    BinaryContentDto binaryContent = binaryContentService.create(createRequest);
    UUID binaryContentId = binaryContent.id();
    // create()는 파일 저장을 AFTER_COMMIT 비동기 이벤트로 처리하므로, @Transactional 테스트에서는
    // 커밋이 일어나지 않아 실제 파일이 기록되지 않는다. 다운로드 검증을 위해 스토리지에 직접 저장한다.
    binaryContentStorage.put(binaryContentId, fileContent.getBytes());

    mockMvc.perform(get("/api/binaryContents/{binaryContentId}/download", binaryContentId)
            .with(asUser(UUID.randomUUID(), UserRole.USER)))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Disposition",
            "attachment; filename=\"download-test.txt\""))
        .andExpect(content().contentType(MediaType.TEXT_PLAIN_VALUE))
        .andExpect(content().bytes(fileContent.getBytes()));
  }

  @Test
  @DisplayName("존재하지 않는 바이너리 컨텐츠 다운로드 API 통합 테스트")
  void downloadBinaryContent_Failure_NotFound() throws Exception {

    UUID nonExistentBinaryContentId = UUID.randomUUID();

    mockMvc.perform(
            get("/api/binaryContents/{binaryContentId}/download", nonExistentBinaryContentId)
                .with(asUser(UUID.randomUUID(), UserRole.USER)))
        .andExpect(status().isNotFound());
  }
}
