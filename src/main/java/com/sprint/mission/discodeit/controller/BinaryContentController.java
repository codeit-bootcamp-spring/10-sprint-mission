package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController implements BinaryContentApi {

  private final BinaryContentService binaryContentService;

  @GetMapping("{binaryContentId}")
  @Override
  public ResponseEntity<BinaryContentDto> find(@PathVariable("binaryContentId") UUID binaryContentId) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(binaryContentService.find(binaryContentId));
  }

  @GetMapping
  @Override
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
          @RequestParam("binaryContentIds") List<UUID> binaryContentIds
  ) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(binaryContentService.findAllByIdIn(binaryContentIds));
  }

  @GetMapping("{binaryContentId}/download")
  @Override
  public ResponseEntity<?> download(@PathVariable("binaryContentId") UUID binaryContentId) {
    return binaryContentService.download(binaryContentId);
  }
}