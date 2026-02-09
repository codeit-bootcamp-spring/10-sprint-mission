package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 바이너리 파일 다운로드 Controller
 */
@RestController
@RequestMapping("/binary-contents")
@AllArgsConstructor
public class BinaryContentController {
    BinaryContentService binaryContentService;

    /**
     * 바이너리 파일 1개 조회
     */
    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity downloadFile(@PathVariable UUID binaryContentId) {
        BinaryContent binaryContent = binaryContentService.findBinaryContentById(binaryContentId);
        BinaryContentResponse result = createBinaryContentResponse(binaryContent);

        return ResponseEntity.status(200).body(result);
    }

    /**
     * 바이너리 파일 여러 개 조회
     */
    @RequestMapping(value = "/batch", method = RequestMethod.GET)
    public ResponseEntity downloadFiles(@RequestBody List<UUID> binaryContentIds) {
        List<BinaryContent> binaryContents = binaryContentService.findAllBinaryContentByIdIn(binaryContentIds);
        List<BinaryContentResponse> result = binaryContents.stream().map(b -> createBinaryContentResponse(b)).toList();

        return ResponseEntity.status(200).body(result);
    }

    private BinaryContentResponse createBinaryContentResponse(BinaryContent binaryContent) {
        return new BinaryContentResponse(
                binaryContent.getId(),
                binaryContent.getCreatedAt(),
                binaryContent.getContent());
    }
}
