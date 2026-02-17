package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    // 기존 API 유지
    // GET /binaryContents?ids=...
    @RequestMapping(value = "/binarycontents", method = RequestMethod.GET)
    public List<BinaryContentResponse> getBinaryContents(
            @RequestParam List<UUID> ids
    ) {
        return binaryContentService.findAllByIdIn(ids);
    }

    // 심화 요구사항
    // GET /api/binaryContent/find?binaryContentId=...
    @RequestMapping(value = "/api/binarycontent/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> findBinaryContent(
            @RequestParam UUID binaryContentId
    ) {
        return ResponseEntity.ok(
                binaryContentService.findEntity(binaryContentId)
        );
    }
}
