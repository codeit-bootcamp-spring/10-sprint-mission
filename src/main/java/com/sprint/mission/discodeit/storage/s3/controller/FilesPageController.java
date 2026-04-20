package com.sprint.mission.discodeit.storage.s3.controller;// FilesPageController.java

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class FilesPageController {

  /**
   * 정적 페이지로 forward - /resources/static/files.html - 브라우저에서 /files/page 로 접속
   */
  @GetMapping("/files/page")
  public String filesPage() {
    return "forward:/files.html";
  }
}