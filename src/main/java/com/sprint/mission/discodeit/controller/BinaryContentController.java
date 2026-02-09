package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentCreateDto;
import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/binaryContent")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // 바이너리 파일 생성
    @RequestMapping(method = RequestMethod.POST, consumes = MediaType.ALL_VALUE)
    public BinaryContentResponseDto create(@RequestBody byte[] bytes,
                                           @RequestHeader("Content-Type") String contentType) {
        BinaryContentCreateDto newDto = new BinaryContentCreateDto(contentType, bytes);
        return binaryContentService.create(newDto);
    }

    // 바아너리 파일 1개 조회
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponseDto> findById(@RequestParam("binaryContentId") UUID id) {
        return new ResponseEntity<>(binaryContentService.findById(id), HttpStatus.OK);
    }

    // 바이너리 파일 여러개 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContentResponseDto> findAllByIdIn(@RequestParam("ids") List<UUID> idList) {
        return binaryContentService.findAllByIdIn(idList);
    }
}
