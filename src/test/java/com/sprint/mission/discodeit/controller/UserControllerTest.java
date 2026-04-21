package com.sprint.mission.discodeit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    UserService userService;
    @MockitoBean
    UserStatusService userStatusService;

    @Test
    @DisplayName("유저 회원가입: 올바른 JSON과 프로필 이미지를 보내면 201 Created를 반환해야 한다")
    void postUser_success() throws Exception{
        // given
        // 프로파일 이미지를 담을 파일
        MockMultipartFile profilePart = new MockMultipartFile(
                "profile",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "mock byte".getBytes()
        );

        BinaryContentDto binaryContentDto = new BinaryContentDto(UUID.randomUUID(),
                profilePart.getSize(),
                profilePart.getName(),
                profilePart.getContentType());
        // 요청 dto
        UserCreateRequest requestDto = new UserCreateRequest("김현재", "fred@naver.com", "123123");
        // 기대 dto
        UserDto expectDto = new UserDto(UUID.randomUUID(), "김현재", "fred@naver.com",binaryContentDto, true);
        // json 요청을 담을 가자 파일들
        MockMultipartFile requestPart = new MockMultipartFile(
                "userCreateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(requestDto)
        );

        given(userService.create(any(), any())).willReturn(expectDto);

        // when
        // then
        mockMvc.perform(multipart("/api/users")
                    .file(requestPart)
                    .file(profilePart)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("김현재"))
                .andExpect(jsonPath("$.email").value("fred@naver.com"));
    }

    @Test
    @DisplayName("유저 전체 조회: 유저 목록을 요청하면 200 OK와 함께 유저 리스트를 반환해야 한다")
    void getAll_success() throws Exception {
        // given
        BinaryContentDto profileDto = new BinaryContentDto(UUID.randomUUID(),1L,"image.jpg", "image/jpeg");

        UserDto user1 = new UserDto(UUID.randomUUID(), "김현재", "fred@naver.com", profileDto, true);
        UserDto user2 = new UserDto(UUID.randomUUID(), "홍길동", "hong@naver.com", null, false); // 프로필 없는 유저

        given(userService.findAllUsers()).willReturn(List.of(user1, user2));

        // when & then
        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON)) // 응답은 json으로
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))

                // 첫 번째 유저([0]) 검증
                .andExpect(jsonPath("$[0].username").value("김현재"))
                .andExpect(jsonPath("$[0].profile").exists())
                .andExpect(jsonPath("$[0].profile.fileName").value("image.jpg"))
                // 두 번째 유저([1]) 검증
                .andExpect(jsonPath("$[1].username").value("홍길동"))
                .andExpect(jsonPath("$[1].profile").doesNotExist());
    }

    @Test
    @DisplayName("유저 정보 수정: 프로필과 정보를 수정하면 200 OK를 반환해야 한다")
    void updateUser_success() throws Exception{
        // given
        MockMultipartFile newProfile = new MockMultipartFile(
                "profile", // 컨트롤러 매개변수의 @RqeustPart("") 변수명
                "image2.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "newImage".getBytes()
        );
        BinaryContentDto binaryContentDto = new BinaryContentDto(
                UUID.randomUUID(),
                newProfile.getSize(),
                newProfile.getOriginalFilename(),
                newProfile.getContentType()
        );

        UUID targetId = UUID.randomUUID();
        User user = new User("김현재", "fred@naver.com","123123");
        UserUpdateRequest updateRequest = new UserUpdateRequest("현재",null,null);
        UserDto expectDto = new UserDto(targetId,
                updateRequest.getNewUsername(),
                user.getEmail(),
                binaryContentDto,
                true);
        MockMultipartFile requestPart = new MockMultipartFile(
                "userUpdateRequest",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsBytes(updateRequest)
        );

        given(userService.update(eq(targetId),any(), any())).willReturn(expectDto);

        // when
        // then
        mockMvc.perform(multipart(HttpMethod.PATCH, "/api/users/{userId}", targetId)
                    .file(requestPart)
                    .file(newProfile)
                    .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("현재"))
                .andExpect(jsonPath("$.profile").exists())
                .andExpect(jsonPath("$.profile.fileName").value("image2.jpg"));
    }

    @Test
    @DisplayName("유저 상태 업데이트: 올바른 상태 정보를 보내면 200 OK와 상태 DTO를 반환해야 한다")
    void updateUserStatusByUserId_success() throws Exception{
        // given
        UUID targetId = UUID.randomUUID();
        Instant targetTime = Instant.now();
        UserStatusUpdateRequest request = new UserStatusUpdateRequest(targetTime);
        UserStatusDto expectDto = new UserStatusDto(UUID.randomUUID(),targetId, request.getNewLastActiveAt());

        given(userStatusService.update(eq(targetId), any())).willReturn(expectDto);
        // when
        // then
        mockMvc.perform(patch("/api/users/{userId}/userStatus", targetId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))) // json형태의 문자열로 번역 본문에 넣음
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastActiveAt").value(targetTime.toString()));
    }

    @Test
    @DisplayName("유저 삭제: 유저 ID를 보내면 성공적으로 삭제하고 204 No Content를 반환해야 한다")
    void deleteUser_success() throws Exception{
        // given
        UUID targetId = UUID.randomUUID();

        // when
        // then
        mockMvc.perform(delete("/api/users/{userId}", targetId))
                .andExpect(status().isNoContent());
    }
}