package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.user.*;
import com.sprint.mission.discodeit.dto.user.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.request.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.user.response.UserResponse;
import com.sprint.mission.discodeit.dto.user.response.UserWithOnlineResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorResponse;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 사용자 관리 Controller
 */
@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    /**
     * 사용자 등록
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity createUser(@RequestBody @Valid UserCreateRequest request) {
        User user = userService.createUser(request);

        UserResponse result = new UserResponse(user.getId(), user.getEmail()
                , user.getUserName(), user.getNickName(), user.getBirthday(), user.getProfileId());

        return ResponseEntity.status(201).body(result);
    }

    // 사용자 삭제
    // 채널 완료한 후

    /**
     * 사용자 정보 수정
     */
//    @RequestMapping(value = "/me", method = RequestMethod.PATCH)
    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity updateUserInfo(@PathVariable UUID userId,
                                         @RequestBody @Valid UserUpdateRequest request) {
        UserUpdateInput input = new UserUpdateInput(userId, request.email(), request.password(),
                request.userName(), request.nickName(), request.birthday(), request.profileImage());
        User user = userService.updateUserInfo(input);

        UserResponse result = new UserResponse(user.getId(), user.getEmail()
                , user.getUserName(), user.getNickName(), user.getBirthday(), user.getProfileId());
        return ResponseEntity.status(200).body(result);
    }

    /**
     * 모든 사용자 조회
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ResponseEntity findAllUsers() {
        List<UserWithOnlineResponse> result = userService.findAllUsers();

        return ResponseEntity.status(200).body(result);
    }

    // 인증 추가 후, 본인이면? /me/online ???
    /**
     * 사용자의 온라인 상태 업데이트
     */
    @RequestMapping(value = "/{userId}/online", method = RequestMethod.PATCH)
    public ResponseEntity updateUserOnlineTime(@PathVariable UUID userId) {
        UserStatus userStatus = userStatusService.updateUserStatusByUserId(userId, Instant.now());

        UserStatusResponse result = new UserStatusResponse(userId, userStatus.getLastOnlineTime(), userStatus.isOnlineStatus());
        return ResponseEntity.status(200).body(result);
    }

    // exception
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity handleException(NoSuchElementException e) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.getReasonPhrase(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity handleException(MethodArgumentNotValidException e) {
        String ErrorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), ErrorMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity handleException(Exception e) {
//        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
//    }
}
