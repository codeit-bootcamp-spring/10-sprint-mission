package com.sprint.mission.discodeit.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import com.sprint.mission.discodeit.WithMockDiscodeitUser;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.Role;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;

@AutoConfigureMockMvc(addFilters = false)
public class UserIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("사용자 생성 성공")
    void create_user_success() throws Exception {

        UserCreateRequest request =
                new UserCreateRequest("달선", "dalsun@naver.com", ".ekftjs123");

        MockMultipartFile jsonPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/users")
                        .file(jsonPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("달선"));
    }

    @Test
    @DisplayName("사용자 생성 실패 - 이미 존재하는 username")
    void create_user_fail_already_exists_username() throws Exception {
        UserCreateRequest request =
                new UserCreateRequest("달선", "dalsun@naver.com", "ekftjs123");

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        mockMvc.perform(multipart("/api/users").file(requestPart));
        // 두 번째 요청
        mockMvc.perform(multipart("/api/users").file(requestPart))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("사용자 수정 성공 - 닉네임 변경")
    void user_update_success() throws Exception {
        // 먼저 사용자 생성
        UserCreateRequest createRequest = new UserCreateRequest("달선", "dalsun@naver.com",
                "ekftjs123");
        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(createRequest));

        String response = mockMvc.perform(multipart("/api/users").file(requestPart))
                .andReturn().getResponse().getContentAsString();
        UUID userId = UUID.fromString(JsonPath.read(response, "$.id"));

        setSecurityContext(userId);

        // 수정 요청
        UserUpdateRequest updateRequest = new UserUpdateRequest("new달선", "newDalsun@naver.com",
                "newnew123");
        MockMultipartFile updatePart = new MockMultipartFile(
                "userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateRequest));

        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(updatePart)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("new달선"));

        SecurityContextHolder.clearContext(); // 테스트 후 정리
    }

    @Test
    @WithMockDiscodeitUser(userId = "00000000-0000-0000-0000-000000000001")
    @DisplayName("사용자 수정 실패 - 존재하지 않는 사용자 ID")
    void user_update_fail_not_found() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UserUpdateRequest request = new UserUpdateRequest("new달선", "newDalsun@naver.com",
                "newnew123");
        MockMultipartFile updateDto =
                new MockMultipartFile("userUpdateRequest",
                        "",
                        "application/json",
                        objectMapper.writeValueAsBytes(request));

        mockMvc.perform(multipart("/api/users/{userId}", userId).file(updateDto)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        }))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"));
    }

    @Test
    @DisplayName("사용자 목록 조회 성공")
    void user_find_all_success() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자 삭제 실패 - 존재하지 않는 사용자")
    void user_delete_fail_not_found() throws Exception {
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        setSecurityContext(userId);
        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNotFound());

        SecurityContextHolder.clearContext();
    }

    private void setSecurityContext(UUID userId) {
        UserDto userDto = new UserDto(userId, "달선", "dalsun@naver.com", null, false, Role.USER);
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "ekftjs123");
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
