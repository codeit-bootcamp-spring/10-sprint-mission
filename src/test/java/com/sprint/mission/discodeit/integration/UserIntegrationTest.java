package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("User 생성 성공")
    void createUser_success() throws Exception {
        UserCreateRequest request = new UserCreateRequest("dustle", "dustle@test.com", "1234");
        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/users").file(requestPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("dustle"))
                .andExpect(jsonPath("$.email").value("dustle@test.com"));

        assertThat(userRepository.findByUsername("dustle")).isPresent();
    }

    @Test
    @DisplayName("User 전체 조회 성공")
    void findAllUsers_success() throws Exception {
        userRepository.save(new User("alpha", "1234", "alpha@test.com"));
        userRepository.save(new User("bravo", "1234", "bravo@test.com"));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("User 수정 성공")
    void updateUser_success() throws Exception {
        User user = userRepository.save(new User("before", "1234", "before@test.com"));
        UserUpdateRequest request = new UserUpdateRequest("after", "after@test.com", "5678");
        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/users/{userId}", user.getId())
                        .file(requestPart)
                        .with(httpRequest -> {
                            httpRequest.setMethod("PATCH");
                            return httpRequest;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("after"))
                .andExpect(jsonPath("$.email").value("after@test.com"));

        User updated = userRepository.findById(user.getId()).orElseThrow();
        assertThat(updated.getUsername()).isEqualTo("after");
    }

    @Test
    @DisplayName("User 삭제 성공")
    void deleteUser_success() throws Exception {
        User user = userRepository.save(new User("delete-user", "1234", "delete@test.com"));
        UUID userId = user.getId();

        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(userId)).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 User 조회 실패 - 404")
    void findUser_fail_notFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/users/{userId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.exceptionType").value("UserNotFoundException"));
    }

    @Test
    @DisplayName("User 생성 실패 - 유효성 검증 (빈 username)")
    void createUser_fail_validation() throws Exception {
        UserCreateRequest request = new UserCreateRequest("", "invalid", "1234");
        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/users").file(requestPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("존재하지 않는 User 삭제 실패 - 404")
    void deleteUser_fail_notFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/api/users/{userId}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }
}
