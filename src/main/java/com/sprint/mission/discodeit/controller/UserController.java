package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.dto.userdto.UserUpdateDTO;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public UserResponseDTO postUser(UserCreateRequestDTO req){
        return userService.create(req);
    }


    @RequestMapping(method = RequestMethod.PATCH)
    @ResponseBody
    public UserResponseDTO patchUser(UserUpdateDTO req){
        return userService.update(req);
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<UserResponseDTO> getUsers(){
        return userService.findAll();
    }
}
