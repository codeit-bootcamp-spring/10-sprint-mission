package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
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
    log.info("Received GET /api/binaryContents/{} request", binaryContentId); // 파일 조회 요청 로그

    BinaryContent content = binaryContentService.findById(binaryContentId);

    return ResponseEntity.ok(binaryContentMapper.toDto(content));
  }

  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
    log.info("Received GET /api/binaryContents/{}/download request",
        binaryContentId); // 파일 다운로드 요청 로그

    BinaryContent binaryContent = binaryContentService.findById(binaryContentId);
    BinaryContentDto dto = binaryContentMapper.toDto(binaryContent);
    return binaryContentStorage.download(dto);
  }

  @Override
  @GetMapping
  public ResponseEntity<List<BinaryContentDto>> findAllById(
      @RequestParam List<UUID> binaryContentIds) {
    log.info("Received GET /api/binaryContents request - ids count: {}",
        binaryContentIds != null ? binaryContentIds.size() : 0); // 여러 파일 조회 요청 로그

    List<BinaryContent> contents = binaryContentService.findAllByIdIn(binaryContentIds);
    List<BinaryContentDto> dtos = contents.stream()
        .map(binaryContentMapper::toDto)
        .toList();

    return ResponseEntity.ok(dtos);
  }
}
