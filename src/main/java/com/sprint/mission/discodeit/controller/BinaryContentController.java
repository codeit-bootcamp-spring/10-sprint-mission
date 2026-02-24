package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  public BinaryContentController(BinaryContentService binaryContentService) {
    this.binaryContentService = binaryContentService;
  }

  @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<BinaryContentResponse> find(@PathVariable UUID binaryContentId) {
    return ResponseEntity.ok(binaryContentService.find(binaryContentId));
  }

  @RequestMapping(method = RequestMethod.GET, produces = "application/json")
  public ResponseEntity<List<BinaryContentResponse>> findAllByIdIn(
      @RequestParam("binaryContentIds") List<UUID> binaryContentIds) {
    return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
  }
}