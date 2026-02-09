package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.authdto.AuthDTO;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStateRequestDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStateResponseDTO;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;
    private final AuthService authService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public UserResponseDTO postUser(@RequestBody UserCreateRequestDTO req){
        return userService.create(req);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<UserResponseDTO> getUsers(){
        return userService.findAll();
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String getUsersPage(Model model){
        model.addAttribute("users", userService.findAll());
        return "static/user-list";
    }


    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteUser(@PathVariable UUID userId){
        userService.delete(userId);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    @ResponseBody
    public UserResponseDTO updateUser(@RequestBody UserUpdateDTO req){
        return userService.update(req);
    }

    @RequestMapping(value = "/{userId}/status", method = RequestMethod.PATCH)
    @ResponseBody
    public UserStateResponseDTO updateUserOnline(@PathVariable UUID userId){
        return userStatusService.activateUserOnline(userId);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public UserResponseDTO userLogin(@RequestBody AuthDTO req){
        return authService.login(req); // 일단 Response DTO만 보내는걸로
        // 추후 로그인 기능을 서비스에서 구현?
    }

}
