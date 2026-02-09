package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
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

    @RequestMapping(method = RequestMethod.GET, value = "/find")
    public ResponseEntity<BinaryContentDto.Response> findBinaryContent(@RequestParam("binary-content-id") UUID binaryContentId) {
        BinaryContentDto.Response response = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto.Response>> findAll(@RequestParam("binary-content-ids") List<UUID> binaryContentIds) {
        List<BinaryContentDto.Response> response = binaryContentService.findAllByIn(binaryContentIds);
        return ResponseEntity.ok(response);
    }
}
