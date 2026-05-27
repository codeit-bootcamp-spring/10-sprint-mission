package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserDTO;
import com.sprint.mission.discodeit.dto.userStatus.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.response.UserStatusDTO;
import com.sprint.mission.discodeit.exception.user.AlreadyExistsEmailException;
import com.sprint.mission.discodeit.exception.user.AlreadyExistsNameException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    /*
     * MockMvc
     * - 실제 HTTP 서버를 띄우지 않고 Controller를 호출할 수 있는 테스트용 클라이언트.
     * - perform() 으로 요청을 만들고, andExpect() 로 응답을 검증한다.
     */
    @Autowired
    private MockMvc mockMvc;

    /*
     * ObjectMapper
     * - Java 객체 <-> JSON 문자열 변환을 담당한다.
     * - writeValueAsString(obj): 객체 → JSON 문자열 (요청 body를 만들 때 사용)
     * - writeValueAsBytes(obj): 객체 → JSON 바이트 배열 (multipart 파트에 넣을 때 사용)
     */
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserStatusService userStatusService;

    private UUID userId;
    private UserDTO userDTO;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        // profile=null, online=false 로 간단하게 설정
        userDTO = new UserDTO(userId, "승주", "jsj@naver.com", null, false);
    }

    // ===================== postUser =====================

    /*
     * 유저 생성 - 성공 케이스
     *
     * UserController.postUser는 multipart/form-data 방식으로 요청을 받는다.
     *   - "userCreateRequest" 파트: JSON 데이터 (이름, 이메일, 비밀번호)
     *   - "profile" 파트: 선택적 이미지 파일 (required=false)
     *
     * MockMultipartFile을 사용하는 이유:
     *   - 실제 파일 없이 multipart 요청을 흉내 내기 위해서다.
     *   - 첫 번째 인자: 파트 이름 (@RequestPart("userCreateRequest")와 반드시 일치해야 함)
     *   - 두 번째 인자: 원본 파일명 (JSON 파트는 파일이 아니므로 null)
     *   - 세 번째 인자: Content-Type (JSON 파트이므로 application/json)
     *   - 네 번째 인자: 실제 바이트 데이터 (ObjectMapper로 직렬화한 JSON)
     *
     * profile 파트를 넣지 않으면 @RequestPart(required=false)이므로 null로 처리된다.
     *
     * any()를 쓰는 이유:
     *   - MockMvc가 JSON을 역직렬화할 때 새로운 객체를 생성하므로,
     *     테스트에서 만든 request 객체와 서비스에 전달되는 객체의 참조가 다르다.
     *   - any()는 "어떤 인자든 상관없이 이 stub을 적용하라"는 뜻이다.
     */
    @Test
    @DisplayName("유저 생성 성공 - 201 Created + UserDTO 반환")
    void postUser_성공() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("승주", "jsj@naver.com", "12345678");
        given(userService.create(any(), any())).willReturn(userDTO);

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when, then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("승주"))
                .andExpect(jsonPath("$.email").value("jsj@naver.com"));
    }

    /*
     * 유저 생성 실패 - username이 빈 값인 경우
     *
     * UserCreateRequest.username에 @NotBlank가 붙어 있다.
     * 빈 문자열("")을 보내면 Spring Bean Validation이 요청을 거부하고
     * GlobalExceptionHandler가 400 Bad Request로 응답한다.
     *
     * then(userService).should(never()).create(...)를 쓰는 이유:
     *   - Validation 단계에서 실패했으므로 서비스 메서드가 절대 호출되면 안 된다.
     *   - 이를 검증해서 "Validation이 실제로 동작하고 있음"을 보장한다.
     */
    @Test
    @DisplayName("유저 생성 실패 - username이 빈 값이면 400 Bad Request")
    void postUser_실패_이름없음() throws Exception {
        // given: @NotBlank 위반
        UserCreateRequest request = new UserCreateRequest("", "jsj@naver.com", "12345678");

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when, then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.username").doesNotExist());

        then(userService).should(never()).create(any(), any());
    }

    @Test
    @DisplayName("유저 생성 실패 - email 형식이 올바르지 않으면 400 Bad Request")
    void postUser_실패_이메일형식오류() throws Exception {
        // given: @Email 위반 - 이메일 형식이 아닌 문자열
        UserCreateRequest request = new UserCreateRequest("승주", "올바르지않은이메일", "12345678");

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when, then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.email").exists());

        then(userService).should(never()).create(any(), any());
    }

    /*
     * 유저 생성 실패 - username 중복
     *
     * Validation은 통과하지만, 서비스 내부에서 동일한 username이 이미 존재하면
     * AlreadyExistsNameException을 던진다.
     * GlobalExceptionHandler가 이를 400 Bad Request로 처리한다.
     */
    @Test
    @DisplayName("유저 생성 실패 - username 중복이면 400 Bad Request")
    void postUser_실패_이름중복() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("승주", "jsj@naver.com", "12345678");
        given(userService.create(any(), any())).willThrow(new AlreadyExistsNameException("승주"));

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when, then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유저 생성 실패 - email 중복이면 400 Bad Request")
    void postUser_실패_이메일중복() throws Exception {
        // given
        UserCreateRequest request = new UserCreateRequest("승주", "jsj@naver.com", "12345678");
        given(userService.create(any(), any())).willThrow(new AlreadyExistsEmailException("jsj@naver.com"));

        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when, then
        mockMvc.perform(multipart("/api/users")
                        .file(requestPart))
                .andExpect(status().isBadRequest());
    }

    // ===================== updateUser =====================

    /*
     * PATCH 메서드로 multipart 요청을 보내는 방법
     *
     * MockMvcRequestBuilders.multipart()는 기본적으로 POST 방식이다.
     * PATCH로 변경하려면 .with(req -> { req.setMethod("PATCH"); return req; }) 를 사용한다.
     * - with()는 RequestPostProcessor 인터페이스를 받는다.
     * - 람다 안에서 HttpServletRequest의 메서드를 바꿔치기하는 방식이다.
     */
    @Test
    @DisplayName("유저 수정 성공 - 200 OK + 수정된 UserDTO 반환")
    void updateUser_성공() throws Exception {
        // given
        UserUpdateRequest request = new UserUpdateRequest("새이름", "new@naver.com", "newpass12");
        UserDTO updatedDTO = new UserDTO(userId, "새이름", "new@naver.com", null, false);

        // eq(userId): userId는 정확하게 일치하는 값을 사용, 나머지는 any()로 처리
        given(userService.update(eq(userId), any(), any())).willReturn(updatedDTO);

        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when, then
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(requestPart)
                        .with(req -> { req.setMethod("PATCH"); return req; }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("새이름"))
                .andExpect(jsonPath("$.email").value("new@naver.com"));
    }

    @Test
    @DisplayName("유저 수정 실패 - 존재하지 않는 userId면 404 Not Found")
    void updateUser_실패_없는유저() throws Exception {
        // given
        UserUpdateRequest request = new UserUpdateRequest("새이름", "new@naver.com", "newpass12");
        given(userService.update(eq(userId), any(), any())).willThrow(new UserNotFoundException(userId));

        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                null,
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(request)
        );

        // when, then
        mockMvc.perform(multipart("/api/users/{userId}", userId)
                        .file(requestPart)
                        .with(req -> { req.setMethod("PATCH"); return req; }))
                .andExpect(status().isNotFound());
    }

    // ===================== deleteUser =====================

    @Test
    @DisplayName("유저 삭제 성공 - 204 No Content")
    void deleteUser_성공() throws Exception {
        // given
        // void 메서드는 willDoNothing()으로 아무 일도 안 함을 명시한다.
        willDoNothing().given(userService).deleteUser(userId);

        // when, then
        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNoContent());

        // deleteUser가 정확히 1번 호출됐는지 검증
        then(userService).should().deleteUser(userId);
    }

    @Test
    @DisplayName("유저 삭제 실패 - 존재하지 않는 userId면 404 Not Found")
    void deleteUser_실패_없는유저() throws Exception {
        // given
        // void 메서드에 예외 설정: willThrow().given() 순서 사용
        // 이유: given(service.voidMethod()).willThrow()는 컴파일 불가 (void를 given()에 넘길 수 없음)
        willThrow(new UserNotFoundException(userId)).given(userService).deleteUser(userId);

        // when, then
        mockMvc.perform(delete("/api/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    // ===================== getUser =====================

    @Test
    @DisplayName("유저 단건 조회 성공 - 200 OK + UserDTO 반환")
    void getUser_성공() throws Exception {
        // given
        given(userService.find(userId)).willReturn(userDTO);

        // when, then
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("승주"));
    }

    @Test
    @DisplayName("유저 단건 조회 실패 - 존재하지 않는 userId면 404 Not Found")
    void getUser_실패_없는유저() throws Exception {
        // given
        given(userService.find(userId)).willThrow(new UserNotFoundException(userId));

        // when, then
        mockMvc.perform(get("/api/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    // ===================== getAllUsers =====================

    @Test
    @DisplayName("전체 유저 목록 조회 성공 - 200 OK + 유저 리스트 반환")
    void getAllUsers_성공() throws Exception {
        // given
        UserDTO userDTO2 = new UserDTO(UUID.randomUUID(), "홍길동", "hong@naver.com", null, true);
        given(userService.findAll()).willReturn(List.of(userDTO, userDTO2));

        // when, then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("승주"))
                .andExpect(jsonPath("$[1].username").value("홍길동"));
    }

    @Test
    @DisplayName("전체 유저 목록 조회 성공 - 유저가 없으면 빈 리스트 반환")
    void getAllUsers_빈리스트() throws Exception {
        // given
        given(userService.findAll()).willReturn(List.of());

        // when, then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ===================== updateStatus =====================

    /*
     * 유저 온라인 상태 업데이트
     * PATCH /api/users/{userId}/userStatus
     *
     * UserStatusUpdateRequest는 multipart가 아닌 일반 JSON body로 전송한다.
     * (Controller에서 @RequestBody를 사용하기 때문에 contentType(APPLICATION_JSON) 필요)
     *
     * multipart와의 차이:
     *   - multipart: 파일 + JSON을 동시에 보낼 때 사용, Content-Type: multipart/form-data
     *   - @RequestBody: JSON만 보낼 때 사용, Content-Type: application/json
     */
    @Test
    @DisplayName("유저 온라인 상태 업데이트 성공 - 200 OK + UserStatusDTO 반환")
    void updateStatus_성공() throws Exception {
        // given
        Instant now = Instant.now();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(now);
        UserStatusDTO statusDTO = new UserStatusDTO(UUID.randomUUID(), userId, now);
        given(userStatusService.updateByUserID(eq(userId), any())).willReturn(statusDTO);

        // when, then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    @DisplayName("유저 온라인 상태 업데이트 실패 - 존재하지 않는 userId면 404 Not Found")
    void updateStatus_실패_없는유저() throws Exception {
        // given
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(Instant.now());
        given(userStatusService.updateByUserID(eq(userId), any())).willThrow(new UserNotFoundException(userId));

        // when, then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("유저 온라인 상태 업데이트 실패 - newLastActiveAt이 null이면 400 Bad Request")
    void updateStatus_실패_시간없음() throws Exception {
        // given: @NotNull 위반 - newLastActiveAt을 null로 설정
        // JSON에서 null 필드를 명시적으로 보내면 {"newLastActiveAt": null} 처럼 표현한다.
        String requestBody = "{\"newLastActiveAt\": null}";

        // when, then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details.newLastActiveAt").exists());

        then(userStatusService).should(never()).updateByUserID(any(), any());
    }
}
