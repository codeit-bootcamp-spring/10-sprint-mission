package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findAll(
            @RequestParam List<UUID> binaryContentIds
    ) {
        List<BinaryContentResponseDTO> binaryContents = binaryContentService.findAll();

        return ResponseEntity.ok(binaryContents);
    }

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity findById(
            @PathVariable UUID binaryContentId
    ) {
        BinaryContentResponseDTO binaryContent = binaryContentService.findById(binaryContentId);

        return ResponseEntity.ok(binaryContent);
    }
}
