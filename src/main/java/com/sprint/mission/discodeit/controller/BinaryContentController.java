package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @Autowired
    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    // 1. 바이너리 파일 단건 조회
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponseDto> find(@RequestParam UUID binaryContentId) {
        BinaryContentResponseDto bcDto = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(bcDto);
    }

    // 2. 바이너리 파일 다건 조회
    @RequestMapping(value = "/findAllByIdIn", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponseDto>> findAllByIdIn(@RequestParam List<UUID> ids) {
        List<BinaryContentResponseDto> bcDto = binaryContentService.findAllByIdIn(ids);
        return ResponseEntity.ok(bcDto);
    }
}
