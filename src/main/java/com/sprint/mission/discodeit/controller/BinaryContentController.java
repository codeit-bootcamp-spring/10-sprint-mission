package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @GetMapping("/api/binaryContent/find")
    public ResponseEntity<BinaryContent> findById(@RequestParam UUID binaryContentId){
        return ResponseEntity.ok(binaryContentService.findEntity(binaryContentId));
    }

    @GetMapping("/api/binaryContents")
    public ResponseEntity<List<BinaryContent>> findAllById (@RequestParam List<UUID> ids){
        return ResponseEntity.ok(binaryContentService.findEntities(ids));
    }
}
