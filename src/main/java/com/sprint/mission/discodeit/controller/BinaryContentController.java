package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
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
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContentDto> findById(
      @PathVariable UUID binaryContentId) {
    BinaryContent content = binaryContentService.findById(binaryContentId);

    return ResponseEntity.ok(binaryContentMapper.toDto(content));
  }

  @Override
  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAllById(
      @RequestParam List<UUID> binaryContentIds) {
    List<BinaryContent> contents = binaryContentService.findAllByIdIn(binaryContentIds);
    List<BinaryContentDto> dtos = contents.stream()
        .map(binaryContentMapper::toDto)
        .toList();

    return ResponseEntity.ok(dtos);
  }
}
