package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // BinaryContent 생성
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<BinaryContentDto.response> createBinaryContent(@RequestPart("file") MultipartFile attachment) {

        // TODO attachment

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(binaryContentService.create(
                        new BinaryContentDto.createRequest(BinaryContentType.FILE, "tmp", "tmp")));
    }

    // BinaryContent 조회
    @RequestMapping(params = "binaryContentId", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentDto.response> findById(@RequestParam UUID binaryContentId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(binaryContentService.findById(binaryContentId));
    }

    // BinaryContent 다건 조회
    @RequestMapping(value = "findAll", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto.response>> findAllByIdIn(@RequestBody List<UUID> uuids) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(binaryContentService.findAllByIdIn(uuids));
    }

    // BinaryContent 삭제
    @RequestMapping(value = "{binary-content-id}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteById(@PathVariable("binary-content-id") UUID binaryContentId) {
        binaryContentService.deleteById(binaryContentId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
