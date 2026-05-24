package com.sprint.mission.discodeit.binarycontent.controller;

import com.sprint.mission.discodeit.binarycontent.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.binarycontent.service.BinaryContentService;
import com.sprint.mission.discodeit.global.infra.storage.BinaryContentStorage;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/binaryContents")
@RequiredArgsConstructor
class BinaryContentController {

  private final BinaryContentService binaryContentService;
  private final BinaryContentStorage binaryContentStorage;

  @GetMapping(value = "/{binaryContentId}")
  public ResponseEntity<BinaryContentDto> find(@PathVariable UUID binaryContentId) {
    return ResponseEntity.status(HttpStatus.OK).body(binaryContentService.find(binaryContentId));
  }

  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findMany(
      @RequestParam List<UUID> binaryContentIds) {
    return ResponseEntity.status(HttpStatus.OK)
        .body(binaryContentService.findAllByIdIn(binaryContentIds));
  }

  @GetMapping(value = "/{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
    BinaryContentDto dto = binaryContentService.find(binaryContentId);
    return binaryContentStorage.download(dto);
  }
}
