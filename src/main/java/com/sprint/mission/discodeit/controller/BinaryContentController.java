package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  private final BinaryContentMapper binaryContentMapper;

  private final BinaryContentStorage  binaryContentStorage;

  // GET /api/binaryContents/{binaryContentId}
  @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
  public ResponseEntity<BinaryContentDto> find(@PathVariable UUID binaryContentId) {
    log.debug("HTTP 요청 - 파일 조회 binaryContentId={}", binaryContentId);

    BinaryContent binaryContent = binaryContentService.find(binaryContentId);
    return ResponseEntity.ok(binaryContentMapper.toDto(binaryContent));
  }
  // GET /api/binaryContents?binaryContentIds=
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<BinaryContentDto>> findAllByIdIn(
          @RequestParam("binaryContentIds") List<UUID> binaryContentIds
  ) {
    List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);

    List<BinaryContentDto> dtos = binaryContents.stream()
            .map(binaryContentMapper::toDto)
            .toList();

    return ResponseEntity.ok(dtos);
  }

  @GetMapping("/{binaryContentId}/download")
  public ResponseEntity<?> download(@PathVariable UUID binaryContentId) {
    log.info("HTTP 요청 - 파일 다운로드 binaryContentId={}", binaryContentId);
    BinaryContent meta = binaryContentService.find(binaryContentId);
    BinaryContentDto dto = binaryContentMapper.toDto(meta);
    log.info("HTTP 응답 - 파일 다운로드 준비 완료 binaryContentId={}", binaryContentId);
    return binaryContentStorage.download(dto);
  }
}
