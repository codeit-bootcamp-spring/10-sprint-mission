package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity findAll() {
        List<BinaryContentResponseDTO> binaryContents = binaryContentService.findAll();

        return ResponseEntity.ok(binaryContents);
    }

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity findById(
            @RequestParam UUID binaryContentId
    ) {
        BinaryContentResponseDTO binaryContent = binaryContentService.findById(binaryContentId);

        return ResponseEntity.ok(binaryContent);
    }
}
