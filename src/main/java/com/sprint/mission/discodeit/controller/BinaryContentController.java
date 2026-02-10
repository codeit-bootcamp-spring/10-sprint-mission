package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binary-content")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // 첨부 파일 단건 조회
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponseDTO> findById(@PathVariable UUID id) {
        BinaryContentResponseDTO binaryContent = binaryContentService.findById(id);

        return ResponseEntity.ok(binaryContent);
    }

    // 첨부 파일 전체 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponseDTO>> findAllByIds(@RequestParam List<UUID> ids) {
        List<BinaryContentResponseDTO> binaryContents = binaryContentService.findAllByIds(ids);

        return ResponseEntity.ok(binaryContents);
    }
}
