package com.sprint.mission.discodeit.controller;// FilesPageController.java

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FilesPageController {

  @GetMapping("/files/page")
  public String filesPage() {
    return "forward:/files.html";
  }
}
