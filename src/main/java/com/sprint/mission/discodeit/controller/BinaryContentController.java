package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // 생성(업로드)
    // filename, content, bytes
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<BinaryContentResponse> create(
            @RequestBody BinaryContentCreateRequest request) {
        BinaryContentResponse response = binaryContentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 1개 조회
    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponse> findById(
            @PathVariable UUID binaryContentId
    ) {
        BinaryContentResponse response =
                binaryContentService.findById(binaryContentId);
        return ResponseEntity.ok(response);
    }

    // 여러 개 조회
    @RequestMapping(value="/messages/{messageId}", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponse>> findAllByMessageId(
            @PathVariable UUID messageId
    ) {
        return ResponseEntity.ok(binaryContentService.findAll(messageId));
    }
}
