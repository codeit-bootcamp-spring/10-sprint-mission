package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.user.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.user.UserUpdateRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserResponseDTO;
import com.sprint.mission.discodeit.dto.response.UserStatusResponseDTO;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    // 사용자 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponseDTO> creat (@RequestBody UserCreateRequestDTO userCreateRequestDTO){
        UserResponseDTO newUser = userService.create(userCreateRequestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // 사용자 전체 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserResponseDTO>> findAll(){
        List<UserResponseDTO> users = userService.findAll();

        return ResponseEntity.ok(users);
    }

    // 사용자 정보 수정
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public ResponseEntity<UserResponseDTO> update(@PathVariable UUID id,
                                                  @RequestBody UserUpdateRequestDTO userUpdateRequestDTO){
        userUpdateRequestDTO.setId(id);
        UserResponseDTO updateUser = userService.update(userUpdateRequestDTO);

        return ResponseEntity.ok(updateUser);
    }

    // 사용자 삭제
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<UserResponseDTO> delete (@PathVariable UUID id){
        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    // 특정 사용자 온라인 상태 변경
    @RequestMapping(value = "/{id}/status", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatusResponseDTO> updateByUserId (@PathVariable UUID id){
        UserStatusResponseDTO updatedUserStatus = userStatusService.updateByUserId(id);

        return ResponseEntity.ok(updatedUserStatus);
    }
}