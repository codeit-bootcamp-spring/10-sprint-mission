package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
public class BinaryContentApiController {

    private final BinaryContentService binaryContentService;

    public BinaryContentApiController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(
            @RequestParam UUID binaryContentId
    ) {
        return ResponseEntity.ok(
                binaryContentService.findEntity(binaryContentId)
        );
    }
}
