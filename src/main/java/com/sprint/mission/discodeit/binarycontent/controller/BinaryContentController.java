package com.sprint.mission.discodeit.binarycontent.controller;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentInfo;
import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentsRequest;
import com.sprint.mission.discodeit.binarycontent.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/binarycontents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(value = "/{contentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentInfo> getBinaryContent(@PathVariable UUID contentId) {
        return ResponseEntity.ok(binaryContentService.findBinaryContent(contentId));
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentInfo>> getBinaryContents(
            @RequestBody BinaryContentsRequest request
    ) {
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(request));
    }
}
