package com.sprint.mission.discodeit.contorller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<UUID> createBinaryContent(
            @RequestParam UUID ownerId,
            @ModelAttribute BinaryContentRequest request
    ) {
        UUID id = binaryContentService.createBinaryContent(ownerId, request);

        return ResponseEntity.ok(id);
    }

    @RequestMapping(method = RequestMethod.GET, params = "binaryContentId")
    public ResponseEntity<BinaryContentResponse> findBinaryContent(
            @RequestParam UUID binaryContentId
    ) {
        BinaryContentResponse response =
                binaryContentService.findBinaryContent(binaryContentId);

        return ResponseEntity.ok()
                .body(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponse>> findAllBinaryContents(
            @RequestParam List<UUID> ids
    ) {
        List<BinaryContentResponse> responses =
                binaryContentService.findAllByIdIn(ids);

        return ResponseEntity.ok(responses);
    }

    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteBinaryContent(
            @PathVariable UUID binaryContentId
    ) {
        binaryContentService.deleteBinaryContent(binaryContentId);

        return ResponseEntity.noContent().build();
    }
}
