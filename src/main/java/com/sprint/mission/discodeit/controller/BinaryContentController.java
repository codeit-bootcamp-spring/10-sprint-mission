package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
@Slf4j
public class BinaryContentController {

    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity findAll(
            @RequestParam List<UUID> binaryContentIds
    ) {
        List<BinaryContentDto> binaryContents = binaryContentService.findAllByIdIn(binaryContentIds);

        return ResponseEntity.ok(binaryContents);
    }

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity findById(
            @PathVariable UUID binaryContentId
    ) {
        BinaryContentDto binaryContent = binaryContentService.findById(binaryContentId);

        return ResponseEntity.ok(binaryContent);
    }

    @RequestMapping(value = "/{binaryContentId}/download", method = RequestMethod.GET)
    public ResponseEntity download(
            @PathVariable UUID binaryContentId
    ) {
        log.debug("[BINARYCONTENT_DOWNLOAD_REQUEST] 파일 다운로드 요청: binaryContentId={}", binaryContentId);
        BinaryContentDto dto = binaryContentService.findById(binaryContentId);

        // storage에서 Return 값으로 ResponseEntity 던져줌
        return binaryContentStorage.download(dto);
    }
}
