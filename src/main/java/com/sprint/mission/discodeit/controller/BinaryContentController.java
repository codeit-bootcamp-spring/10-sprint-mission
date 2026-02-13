package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContents")
public class BinaryContentController {

  private final BinaryContentService binaryContentService;

  // 바이너리 파일 생성
  @RequestMapping(method = RequestMethod.POST)
  public BinaryContentResponseDto create(@RequestParam("file") MultipartFile file)
      throws IOException {
    String fileName = file.getOriginalFilename();
    Path savePath = Paths.get("./uploads/" + fileName);
    Files.createDirectories(savePath.getParent());
    file.transferTo(savePath);

    BinaryContentCreateDto newDto = new BinaryContentCreateDto(file.getContentType(),
        file.getBytes());
    return binaryContentService.create(newDto);
  }

  // 바아너리 파일 1개 조회
  @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
  public ResponseEntity<BinaryContentResponseDto> findById(
      @PathVariable("binaryContentId") UUID id) {
    return new ResponseEntity<>(binaryContentService.findById(id), HttpStatus.OK);
  }

  // 바이너리 파일 여러개 조회
  @RequestMapping(method = RequestMethod.GET)
  public List<BinaryContentResponseDto> findAllByIdIn(@RequestParam("ids") List<UUID> idList) {
    return binaryContentService.findAllByIdIn(idList);
  }
}
