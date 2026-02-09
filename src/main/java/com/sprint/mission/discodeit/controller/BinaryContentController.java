package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponse> findById(@RequestParam UUID binaryContentId){
        return ResponseEntity.ok(binaryContentService.findById(binaryContentId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponse>> findAllById (@RequestParam List<UUID> ids){
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(ids));
    }
}
