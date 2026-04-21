package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserStatusRepository userStatusRepository;

    @MockitoBean
    BinaryContentStorage binaryContentStorage;


    @Test
    @DisplayName("회원가입 API 진행시 , 실제 DB에 유저가 1명 저장되어야 한다")
    void signUp_integration_success() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("김현재", "fred@naver.com","123123");

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "프로필 이미지".getBytes()
        );

        // when
        mockMvc.perform(multipart("/api/users")
                    .file(requestPart)
                    .file(profilePart)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("김현재"));

        // then
        List<User> userList = userRepository.findAll();
        User savedUser = userList.get(0);
        assertThat(savedUser.getUsername()).isEqualTo("김현재");
        assertThat(savedUser.getEmail()).isEqualTo("fred@naver.com");
    }

    @Test
    @DisplayName("시나리오 B: 유저 정보 수정 API를 찌르면, 실제 DB의 유저 이름이 변경되어야 한다")
    void updateUser_integration_success() throws Exception {
        // given
        User targetUser = userRepository.save(new User("김현재", "fred@naver.com", "123123")) ;
        UUID targetId = targetUser.getId();

        UserUpdateRequest request = new UserUpdateRequest("현재", null ,null);
        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "image data".getBytes()
        );

        userStatusRepository.save(new UserStatus(targetUser));

        // when
        mockMvc.perform(multipart(HttpMethod.PATCH,"/api/users/{userId}", targetId)
                    .file(requestPart)
                    .file(profilePart)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("현재"))
                .andExpect(jsonPath("$.profile").exists());
        // then
        User updatedUser = userRepository.findById(targetId).orElseThrow();
        assertThat(updatedUser.getUsername()).isEqualTo("현재");
    }

    @Test
    @DisplayName("시나리오 C: 유저 삭제 API를 찌르면, 실제 DB에서 유저가 완벽히 사라져야 한다")
    void deleteUser_integration_success() throws Exception{
        // given
        User targetUser = userRepository.save(new User("김현재", "fred@naver.com", "123123")) ;
        UUID targetId = targetUser.getId();

        // when
        mockMvc.perform(delete("/api/users/{userId}", targetId))
                .andExpect(status().isNoContent());

        // then
        Optional<User> deletedUser = userRepository.findById(targetId);
        assertThat(deletedUser).isEmpty();
    }
}
