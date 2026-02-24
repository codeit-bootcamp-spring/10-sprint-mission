package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@ResponseBody
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  //GET /api/binaryContents/{binaryContentId}
  @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
  public ResponseEntity<BinaryContent> find(
      @PathVariable UUID binaryContentIds
  ) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentIds);
    return ResponseEntity.ok(binaryContent);
  }

  //GET /api/bianryContents?binaryContentIds=id1,id2,...
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<BinaryContent>> findAllByIdln(
      @RequestParam List<UUID> binaryContentIds
  ) {
    List<BinaryContent> contents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.ok(contents);
  }

//  private final BinaryContentService binaryContentService;
//
//  @RequestMapping(path = "find")
//  public ResponseEntity<BinaryContent> find(@RequestParam("binaryContentId") UUID binaryContentId) {
//    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
//    return ResponseEntity
//        .status(HttpStatus.OK)
//        .body(binaryContent);
//  }
//
//  @RequestMapping(path = "findAllByIdIn")
//  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
//      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
//    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
//    return ResponseEntity
//        .status(HttpStatus.OK)
//        .body(binaryContents);
//  }
}
