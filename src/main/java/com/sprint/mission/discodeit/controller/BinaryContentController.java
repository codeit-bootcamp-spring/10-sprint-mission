package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentResponse;
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

    // binary-Content 단건 조회
    @RequestMapping(value="/find/{content-id}", method= RequestMethod.GET)
    public ResponseEntity<BinaryContentResponse> getBinaryContent(@PathVariable("content-id") UUID contentId){
        return ResponseEntity.ok(binaryContentService.find(contentId));
    }

    // binary-content 다건 조회
    @RequestMapping(value="/findAll",method=RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponse>> getBinaryContents(@RequestParam List<UUID> binaryContentIds){
        return ResponseEntity.ok(binaryContentService.findAllByIdIn(binaryContentIds));
    }

}
