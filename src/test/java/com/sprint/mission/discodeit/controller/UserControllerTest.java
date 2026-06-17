package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.GlobalExceptionHandler;
import com.sprint.mission.discodeit.exception.user.DuplicatedEmailException;
import com.sprint.mission.discodeit.exception.user.DuplicatedUsernameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.security.filter.jwt.JwtAuthenticationFilter;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper om;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // `@EnableJpaAuditing`이 애플리케이션 시작 클래스에 설정되어 있어서 JPA Auditing가 활성화됨
    // `jpaAuditingHandler`는 `jpaMappingContext`를 필요로 함
    // 근데 API 슬라이스 테스트에는 Entity 메타 모델이 없어서 오류가 남
    @MockitoBean
    private JpaMetamodelMappingContext  jpaMetamodelMappingContext;

    Instant now;
    Instant nowMinus5;
    Instant nowMinus10;
    Instant nowMinus15;
    Instant nowMinus20;

    @BeforeEach
    void setUp() {
        now = Instant.now();
        nowMinus5 = now.minus(5, ChronoUnit.MINUTES);
        nowMinus10 = now.minus(10, ChronoUnit.MINUTES);
        nowMinus15 = now.minus(15, ChronoUnit.MINUTES);
        nowMinus20 = now.minus(20, ChronoUnit.MINUTES);
    }

    private UserDto createUserDto(UUID id, String email, String username, BinaryContentDto profile, boolean isOnline) {
        UUID userId = id == null ? UUID.randomUUID() : id;
        return new UserDto(userId, username, email, profile, isOnline, Role.USER);
    }

    private BinaryContentDto createBinaryContentDto(String fileName, String contentType, Long size) {
        UUID binaryContentId = UUID.randomUUID();
        return new BinaryContentDto(binaryContentId, fileName, size, contentType, BinaryContentStatus.SUCCESS);
    }

    @Nested
    @DisplayName("사용자 생성 API 테스트")
    class createUser {

        @Test
        @DisplayName("사용자를 생성하면 201 상태 코드와 사용자 정보를 반환한다.")
        void success_create_user() throws Exception {
            // given(준비)
            BinaryContentDto profileDto = createBinaryContentDto("profileFile", "ProfileFileContentType", 1L);
            UserCreateRequest request = new UserCreateRequest("test@gmail.com", "test", "1234");

            UserDto expectedUserDto = createUserDto(null, request.email(), request.username(), profileDto, false);

            MockMultipartFile requestPart = new MockMultipartFile("userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, om.writeValueAsBytes(request));
            MockMultipartFile profile = new MockMultipartFile("profile", "image.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());

            given(userService.create(request, profile)).willReturn(expectedUserDto);

            // when(실행), then(검증)
            mockMvc.perform(multipart("/api/users")
                            .file(requestPart)
                            .file(profile)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(expectedUserDto.id().toString()))
                    .andExpect(jsonPath("$.email").value(expectedUserDto.email()))
                    .andExpect(jsonPath("$.username").value(expectedUserDto.username()))
                    .andExpect(jsonPath("$.profile.id").value(expectedUserDto.profile().id().toString()));
        }

        @Test
        @DisplayName("다른 사용자의 email과 중복되면 400 상태 코드와 DuplicatedEmailException 예외 응답을 반환한다.")
        void fail_create_user_by_email_when_email_duplicate() throws Exception {
            // given(준비)
            String existingEmail = "test@gmail.com";
            UserCreateRequest request = new UserCreateRequest(existingEmail, "test", "1234");

            MockMultipartFile requestPart = new MockMultipartFile("userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, om.writeValueAsBytes(request));
            MockMultipartFile profile = new MockMultipartFile("profile", "image.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());

            given(userService.create(request, profile)).willThrow(new DuplicatedEmailException(request.email()));

            // when(실행), then(검증)
            mockMvc.perform(multipart("/api/users")
                            .file(requestPart)
                            .file(profile)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATED_EMAIL.toString()))
                    .andExpect(jsonPath("$.status").value("400"))
                    .andExpect(jsonPath("$.exceptionType").value(DuplicatedEmailException.class.getSimpleName()))
                    .andExpect(jsonPath("$.details.email").value(existingEmail));
        }

        @Test
        @DisplayName("다른 사용자의 username과 중복되면 400 상태 코드와 DuplicatedUsernameException 예외 응답을 반환한다.")
        void fail_create_user_by_username_when_username_duplicate() throws Exception {
            // given(준비)
            String existingUsername = "test";
            UserCreateRequest request = new UserCreateRequest("test@gmail.com", existingUsername, "1234");

            MockMultipartFile requestPart = new MockMultipartFile("userCreateRequest", "", MediaType.APPLICATION_JSON_VALUE, om.writeValueAsBytes(request));
            MockMultipartFile profile = new MockMultipartFile("profile", "image.png", MediaType.IMAGE_PNG_VALUE, "image".getBytes());

            given(userService.create(request, profile)).willThrow(new DuplicatedUsernameException(request.username()));

            // when(실행), then(검증)
            mockMvc.perform(multipart("/api/users")
                            .file(requestPart)
                            .file(profile)
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATED_USERNAME.toString()))
                    .andExpect(jsonPath("$.status").value("400"))
                    .andExpect(jsonPath("$.exceptionType").value(DuplicatedUsernameException.class.getSimpleName()))
                    .andExpect(jsonPath("$.details.username").value(existingUsername));
        }
    }


    @Nested
    @DisplayName("사용자 목록 조회 API 테스트")
    class findAllUserList {

        @Test
        @DisplayName("사용자 목록을 조회하면 200 상태 코드와 사용자 목록이 반환된다.")
        void success_find_all_user_list() throws Exception {
            // given(준비)
            BinaryContentDto profile = createBinaryContentDto("testFileName", "image/png", 5L);
            UserDto userDto1 = createUserDto(null, "test1@gmail.com", "test1", null, true);
            UserDto userDto2 = createUserDto(null, "test2@gmail.com", "test2", profile, true);
            List<UserDto> userDtoList = List.of(userDto1, userDto2);

            given(userService.findAll()).willReturn(userDtoList);

            // when(실행), then(검증)
            mockMvc.perform(get("/api/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(userDtoList.size())))
                    .andExpect(jsonPath("$.[0].id").value(userDto1.id().toString()))
                    .andExpect(jsonPath("$.[1].id").value(userDto2.id().toString()))
                    .andExpect(jsonPath("$.[1].profile.id").value(userDto2.profile().id().toString()));
        }

        @Test
        @DisplayName("사용자 목록 조회 시 사용자가 없을 경우 200 상태 코드와 빈 사용자 목록이 반환된다.")
        void success_find_empty_all_user_list() throws Exception {
            // given(준비)
            given(userService.findAll()).willReturn(List.of());

            // when(실행), then(검증)
            mockMvc.perform(get("/api/users")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("사용자 정보 수정 API 테스트")
    class updateUser {

        @Test
        @DisplayName("특정 사용자 정보를 수정하면 200 상태 코드와 수정된 사용자 정보를 반환한다.")
        void success_update_user_by_id() throws Exception {
            // given(준비)
            BinaryContentDto existingProfile = createBinaryContentDto("testFileName", "image/png", 5L);
            UserDto existingUserDto = createUserDto(null, "test@gmail.com", "test", existingProfile, true);

            UUID requestUserId = existingUserDto.id();
            UserUpdateRequest request = new UserUpdateRequest("updateTestEmail@gmail.com", "12345", "updateTest");

            BinaryContentDto expectedProfile = createBinaryContentDto("testFileName", "image/png", 5L);
            UserDto expectedUserDto = createUserDto(existingUserDto.id(), request.newEmail(), request.newUsername(), expectedProfile, true);

            MockMultipartFile requestPart = new MockMultipartFile("userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, om.writeValueAsBytes(request));
            MockMultipartFile profile = new MockMultipartFile("profile", "updateImage.png", MediaType.IMAGE_PNG_VALUE, "updateImage".getBytes());

            given(userService.update(requestUserId, request, profile)).willReturn(expectedUserDto);

            // when(실행), then(검증)
            mockMvc.perform(multipart("/api/users/{userId}", requestUserId)
                            .file(requestPart)
                            .file(profile)
                            .with(r -> {
                                r.setMethod("PATCH");
                                return r;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(expectedUserDto.id().toString()))
                    .andExpect(jsonPath("$.email").value(expectedUserDto.email()))
                    .andExpect(jsonPath("$.username").value(expectedUserDto.username()))
                    .andExpect(jsonPath("$.profile.id").value(expectedUserDto.profile().id().toString()));
        }

        @Test
        @DisplayName("입력된 email을 사용하는 사용자가 있다면 400 상태 코드와 예외 응답을 반환한다.")
        void fail_update_user_by_id_when_email_duplicate() throws Exception {
            // given(준비)
            UserDto existingUserDto = createUserDto(null, "test@gmail.com", "test", null, true);

            UUID requestUserId = existingUserDto.id();
            UserUpdateRequest request = new UserUpdateRequest("existingEmail@gmail.com", null, null);

            MockMultipartFile requestPart = new MockMultipartFile("userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, om.writeValueAsBytes(request));

            given(userService.update(eq(requestUserId), eq(request), isNull())).willThrow(new DuplicatedEmailException(requestUserId, request.newEmail()));

            // when(실행), then(검증)
            mockMvc.perform(multipart("/api/users/{userId}", requestUserId)
                            .file(requestPart)
                            .with(r -> {
                                r.setMethod("PATCH");
                                return r;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATED_EMAIL.toString()))
                    .andExpect(jsonPath("$.status").value("400"))
                    .andExpect(jsonPath("$.exceptionType").value(DuplicatedEmailException.class.getSimpleName()))
                    .andExpect(jsonPath("$.details.userId").value(requestUserId.toString()))
                    .andExpect(jsonPath("$.details.email").value(request.newEmail()));
        }

        @Test
        @DisplayName("입력된 username을 사용하는 사용자가 있다면 400 상태 코드와 예외 응답을 반환한다.")
        void fail_update_user_by_id_when_username_duplicate() throws Exception {
            // given(준비)
            UserDto existingUserDto = createUserDto(null, "test@gmail.com", "test", null, true);

            UUID requestUserId = existingUserDto.id();
            UserUpdateRequest request = new UserUpdateRequest(null, null, "existingUsername");

            MockMultipartFile requestPart = new MockMultipartFile("userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, om.writeValueAsBytes(request));

            given(userService.update(eq(requestUserId), eq(request), isNull())).willThrow(new DuplicatedUsernameException(requestUserId, request.newUsername()));

            // when(실행), then(검증)
            mockMvc.perform(multipart("/api/users/{userId}", requestUserId)
                            .file(requestPart)
                            .with(r -> {
                                r.setMethod("PATCH");
                                return r;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(ErrorCode.DUPLICATED_USERNAME.toString()))
                    .andExpect(jsonPath("$.status").value("400"))
                    .andExpect(jsonPath("$.exceptionType").value(DuplicatedUsernameException.class.getSimpleName()))
                    .andExpect(jsonPath("$.details.userId").value(requestUserId.toString()))
                    .andExpect(jsonPath("$.details.username").value(request.newUsername()));
        }

        @Test
        @DisplayName("사용자를 찾을 수 없다면 404 상태 코드와 예외 응답을 반환한다.")
        void fail_update_user_by_id_when_user_not_found() throws Exception {
            // given(준비)
            UUID requestUserId = UUID.randomUUID();
            UserUpdateRequest request = new UserUpdateRequest(null, null, "existingUsername");

            MockMultipartFile requestPart = new MockMultipartFile("userUpdateRequest", "", MediaType.APPLICATION_JSON_VALUE, om.writeValueAsBytes(request));

            given(userService.update(eq(requestUserId), eq(request), isNull())).willThrow(new UserNotFoundException("userId", requestUserId));

            // when(실행), then(검증)
            mockMvc.perform(multipart("/api/users/{userId}", requestUserId)
                            .file(requestPart)
                            .with(r -> {
                                r.setMethod("PATCH");
                                return r;
                            })
                            .contentType(MediaType.MULTIPART_FORM_DATA))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.toString()))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.exceptionType").value(UserNotFoundException.class.getSimpleName()))
                    .andExpect(jsonPath("$.details.userId").value(requestUserId.toString()));
        }
    }

    @Nested
    @DisplayName("사용자 삭제 API 테스트")
    class deleteUser {

        @Test
        @DisplayName("특정 사용자를 삭제하면 204 상태 코드를 반환한다.")
        void success_delete_user_by_id() throws Exception {
            // given(준비)
            UUID requestUserId = UUID.randomUUID();

            willDoNothing().given(userService).delete(requestUserId);

            // when(실행), then(검증)
            mockMvc.perform(delete("/api/users/{userId}", requestUserId))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("사용자를 찾을 수 없다면 404 상태 코드와 예외 응답을 반환한다.")
        void fail_delete_user_by_id_when_user_not_found() throws Exception {
            // given(준비)
            UUID requestUserId = UUID.randomUUID();

            willThrow(new UserNotFoundException("userId", requestUserId)).given(userService).delete(requestUserId);

            // when(실행), then(검증)
            mockMvc.perform(delete("/api/users/{userId}", requestUserId))
                    .andExpect(status().isNotFound())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.code").value(ErrorCode.USER_NOT_FOUND.toString()))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.exceptionType").value(UserNotFoundException.class.getSimpleName()))
                    .andExpect(jsonPath("$.details.userId").value(requestUserId.toString()));
        }
    }
}