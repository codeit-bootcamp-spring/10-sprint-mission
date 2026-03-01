package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;

  @Override
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContent> findById(
      @PathVariable UUID binaryContentId) {
    return ResponseEntity.ok(binaryContentService.findEntity(binaryContentId));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<BinaryContent>> findAllById(
      @RequestParam List<UUID> binaryContentIds) {
    return ResponseEntity.ok(binaryContentService.findEntities(binaryContentIds));
  }
}
