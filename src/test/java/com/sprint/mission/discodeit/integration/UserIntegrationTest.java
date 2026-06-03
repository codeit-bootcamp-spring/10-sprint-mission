package com.sprint.mission.discodeit.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.UserEntity;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.jwt.provider.JwtTokenProvider;
import com.sprint.mission.discodeit.security.jwt.registry.JwtRegistry;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class UserIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private JwtRegistry jwtRegistry;

    // 사용자 인증 정보 강제 주입
    private Authentication getCustomAuth(UserDto userDto) {
        DiscodeitUserDetails principal = new DiscodeitUserDetails(userDto, "dummyPassword123!");

        // 사용자 정보 및 권한을 담은 인증 토큰 반환
        return new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
    }

    /*
        사용자 생성
     */
    // [성공]
    @Test
    @DisplayName("사용자 생성 완료")
    void create_user_success() throws Exception {
        // given

        // 생성할 사용자
        UserCreateRequest createRequest = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );

        // JSON 파일화
        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(createRequest).getBytes()
        );

        // when
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isCreated());

        // then
        List<UserEntity> users = userRepository.findAll();
        assertEquals(createRequest.username(), users.get(1).getUsername());
    }

    // [실패] 이메일 형식 오류
    @Test
    @DisplayName("사용자 생성 실패: 이메일 형식이 잘못된 경우, 400 Bad Request 반환")
    void create_user_failure() throws Exception {
        // given

        // 생성할 사용자
        UserCreateRequest badRequest = new UserCreateRequest(
                "yushi",
                "yushi",
                "yushi1234"
        );

        // JSON 파일화
        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(badRequest).getBytes()
        );

        // when & then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart)
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentNotValidException"));
    }

    /*
        사용자 수정
     */
    // [성공]
    @Test
    @DisplayName("사용자 수정 완료")
    void update_user_success() throws Exception {
        // given

        // 기존 사용자 정보
        UserCreateRequest createRequest = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        UserDto savedUser = userService.create(createRequest, null);

        // 수정할 사용자
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "tokuno",
                "tokuno@wish.com",
                "tokuno1234"
        );

        // JSON 파일화
        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(updateRequest).getBytes()
        );

        // when
        mockMvc.perform(multipart("/api/users/{userId}", savedUser.id())
                        .file(requestPart)
                        .with(request -> {
                            request.setMethod("PATCH");
                            return request;
                        })
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(authentication(getCustomAuth(savedUser))))
                .andExpect(status().isOk());

        // then
        UserEntity updatedEntity = userRepository.findById(savedUser.id()).orElseThrow();
        assertEquals(updateRequest.newUsername(), updatedEntity.getUsername());
    }

    // [실패] 잘못된 사용자 ID
    @Test
    @DisplayName("사용자 수정 실패: 사용자 ID가 잘못된 경우, 400 Bad Request 반환")
    void update_user_failure() throws Exception {
        // given
        String userId = "userId";

        // 수정할 사용자
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "tokuno",
                "tokuno@wish.com",
                "tokuno1234"
        );

        // JSON 파일화
        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(updateRequest).getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(requestPart)
                        .with(req -> {
                            req.setMethod("PATCH");
                            return req;
                        })
                        .accept(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("MethodArgumentTypeMismatchException"));
    }

    /*
        사용자 삭제
     */
    // [성공]
    @Test
    @DisplayName("사용자 삭제 완료")
    void delete_user_success() throws Exception {
        // given

        // 삭제할 사용자
        UserCreateRequest createRequest = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        UserDto savedUser = userService.create(createRequest, null);

        // when
        mockMvc.perform(delete(("/api/users/{userId}"), savedUser.id())
                        .with(csrf())
                        .with(authentication(getCustomAuth(savedUser))))
                .andExpect(status().isNoContent());

        // then
        boolean exists = userRepository.existsById(savedUser.id());
        assertFalse(exists);
    }

    // [실패] HTTP 메서드 오류
    @Test
    @DisplayName("사용자 삭제 실패: HTTP 메서드가 잘못된 경우, 405 Method Not Allowed 반환\"")
    void delete_user_failure() throws Exception {
        // given
        UUID userId = UUID.randomUUID();

        // when & then
        mockMvc.perform(post("/api/users/{userId}", userId)
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.exceptionType").value("HttpRequestMethodNotSupportedException"));
    }

    /*
        전체 목록 조회
     */
    // [성공]
    @Test
    @DisplayName("사용자 생성 완료")
    void find_all_user_success() throws Exception {
        // given
        UserCreateRequest firstCreateRequest = new UserCreateRequest(
                "yushi",
                "yushi@wish.com",
                "yushi1234"
        );
        userService.create(firstCreateRequest, null);
        UserCreateRequest secondCreateRequest = new UserCreateRequest(
                "tokuno",
                "tokuno@wish.com",
                "tokuno1234"
        );
        userService.create(secondCreateRequest, null);

        mockMvc.perform(get("/api/users")
                        .with(csrf())
                        .with(user("yushi").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(2)));
    }
}
