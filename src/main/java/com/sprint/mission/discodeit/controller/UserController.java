package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public User createUser(@RequestBody UserCreateRequest createRequest) {

        return userService.create(createRequest);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<UserDto>> findAllUser(Model model) {
        return ResponseEntity.ok(userService.findAll());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteUser(@PathVariable UUID id) {
        userService.delete(id);
    }

    @RequestMapping(value = "/{id}",method = RequestMethod.PATCH)
    @ResponseBody
    public User updateUser(@PathVariable UUID id,
                           @RequestBody UserUpdateRequest userUpdateRequest) {

        return userService.update(id,userUpdateRequest);
    }

    @RequestMapping(value = "/{id}/status",method = RequestMethod.PATCH)
    @ResponseBody
    public UserDto updateOnline(@PathVariable UUID id,
                                @RequestBody UserStatusUpdateRequest userStatusUpdateRequest){

        userStatusService.update(id, userStatusUpdateRequest);
        return userService.find(id);

    }


}
