package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentMapper binaryContentMapper;

    @GetMapping("/{binaryContentId}")
    public ResponseEntity<BinaryContentDto> find(@PathVariable UUID binaryContentId){
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        BinaryContentDto binaryContentDto = binaryContentMapper.toDto(binaryContent);
        return ResponseEntity.ok(binaryContentDto);
    }

    @GetMapping
    public ResponseEntity<List<BinaryContentDto>> findAll(@RequestParam List<UUID> binaryContentIds){
        List<BinaryContent> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);
        List<BinaryContentDto> binaryContentDtos =
                binaryContents.stream()
                        .map(binaryContentMapper::toDto)
                        .toList();

        return ResponseEntity.ok(binaryContentDtos);

    }

    @GetMapping("/{binaryContentId}/download")
    public ResponseEntity<?> download(@PathVariable UUID binaryContentId){
        BinaryContent binaryContent = binaryContentService.find(binaryContentId);
        BinaryContentDto binaryContentDto = binaryContentMapper.toDto(binaryContent);

        return binaryContentStorage.download(binaryContentDto);
    }
}
