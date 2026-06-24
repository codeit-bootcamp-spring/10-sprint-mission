package com.sprint.mission.discodeit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/main")
public class ViewController {
    @RequestMapping(method = {RequestMethod.GET})
    public String index(){
        return "index";
    }
}
