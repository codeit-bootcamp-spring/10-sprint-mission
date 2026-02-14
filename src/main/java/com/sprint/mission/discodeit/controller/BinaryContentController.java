package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> find(@RequestParam("binaryContentId") UUID binaryContentId) {
        BinaryContent content = binaryContentService.findEntityById(binaryContentId);
        return ResponseEntity.ok(content);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContentDto.Response> findAllIdIn(@RequestParam("ids") List<UUID> ids) {
        return binaryContentService.findAllIdIn(ids);
    }
}
