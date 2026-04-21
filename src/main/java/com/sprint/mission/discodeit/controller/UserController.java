package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User", description = "User API")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    // 유저 생성
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> postUser(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                            @Valid @RequestPart("userCreateRequest") UserCreateRequest request,
                                            @RequestPart(value = "profile", required = false)MultipartFile profile){
        log.debug("유저 회원가입 요청: 유저 이름 = {}, 유저 이메일 = {}, 프로필 여부 = {}",
                request.getUsername(), request.getEmail(), profile != null);
        UserDto response = userService.create(request , profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 유저 전체 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> getAll(){
        List<UserDto> userList = userService.findAllUsers();
        return ResponseEntity.ok(userList);
    }

    // 유저 업데이트
    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> updateUser(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                              @PathVariable("userId") UUID id,
                                              @Valid @RequestPart("userUpdateRequest") UserUpdateRequest request,
                                              @RequestPart(value="profile", required = false) MultipartFile profile){
        log.debug("유저 정보 수정 요청: 수정할 유저 id = {}, 프로필 수정 여부 = {}",id, profile != null);
        UserDto response = userService.update(id,request, profile);
        return ResponseEntity.ok(response);
    }

    // 유저 온라인상태 업데이트
    @RequestMapping(value = "/{userId}/userStatus" , method = RequestMethod.PATCH)
    public ResponseEntity<UserStatusDto> updateUserStatusByUserId(@PathVariable("userId") UUID id,
                                                                  @Valid @RequestBody(required = true) UserStatusUpdateRequest request){
        UserStatusDto response = userStatusService.update(id,request);
        return ResponseEntity.ok(response);
    }

    // 유저 삭제
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteUser(@PathVariable("userId") UUID id){
        log.debug("유저 삭제 요청: 삭제할 유저 id = {}",id);
        userService.delete(id);
        return ResponseEntity.noContent().build(); // noContent는 객체를 만든다는 뜻, build를 붙혀서 객체 만듬

    }

}
