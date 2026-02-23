package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  // GET /api/binaryContents/{binaryContentId}
  @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
  public ResponseEntity<BinaryContent> find(@PathVariable UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity.ok(binaryContent);
  }
  // GET /api/binaryContents?binaryContentIds=
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<BinaryContent>> findAllByIdIn(
          @RequestParam("binaryContentIds") List<UUID> binaryContentIds
  ) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
    return ResponseEntity.ok(binaryContents);
  }
}