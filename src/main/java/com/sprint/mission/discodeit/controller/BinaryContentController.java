package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
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
  private final BinaryContentStorage binaryContentStorage;
  private final BinaryContentMapper binaryContentMapper;

  @Override
  @GetMapping("/{binaryContentId}")
  public ResponseEntity<BinaryContentDto> findById(
      @PathVariable UUID binaryContentId) {
    BinaryContent content = binaryContentService.findById(binaryContentId);

    return ResponseEntity.ok(binaryContentMapper.toDto(content));
  }

  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
    BinaryContent binaryContent = binaryContentService.findById(binaryContentId);
    BinaryContentDto dto = binaryContentMapper.toDto(binaryContent);
    return binaryContentStorage.download(dto);
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
