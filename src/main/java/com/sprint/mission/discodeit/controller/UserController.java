package com.sprint.mission.discodeit.controller;


import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateByUserIdRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UserResponse> create(@RequestBody UserCreateRequest request){
        // name, alias, email, password, binaryContent
        UserResponse userResponse = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH)
    public ResponseEntity<UserResponse> update(
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequest body
    ){
        UserUpdateRequest request = new UserUpdateRequest(
                userId,
                body.userName(),
                body.alias(),
                body.email(),
                body.password(),
                body.profileImage()
        );
        UserResponse response = userService.updateUser(request);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(value= "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID userId){
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    } //responseEntity.ok -> 바디값 반환. 삭제시에는 바디값 반환 필요 x

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserResponse>> findAll(){
        return ResponseEntity.ok(userService.getUserAll());
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
    public ResponseEntity<UserStatusResponse> updateOnlineStatus(@PathVariable UUID userId,
                                                                 @RequestBody UserStatusUpdateByUserIdRequest body){
        UserStatusUpdateByUserIdRequest request =
                new UserStatusUpdateByUserIdRequest(userId, body.refreshLogin());
        return ResponseEntity.ok(userStatusService.updateByUserId(request));
    }


}
