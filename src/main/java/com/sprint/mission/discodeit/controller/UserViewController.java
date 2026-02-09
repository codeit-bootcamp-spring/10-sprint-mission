package com.sprint.mission.discodeit.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@AllArgsConstructor
@Controller
@RequestMapping("/users")
public class UserViewController {

    @RequestMapping(method = RequestMethod.GET)
    public String viewUserList(){
        return "forward:/user-list.html";
    }

}
