package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    // 바이너리 파일 1개 조회
    @RequestMapping(method = RequestMethod.GET, path = "/{binaryContentId}")
    public ResponseEntity<BinaryContentDto.BinaryContentResponse> findById(
            @PathVariable UUID binaryContentId
    ) {
        BinaryContentDto.BinaryContentResponse data = binaryContentService.findById(binaryContentId);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    // 2) 바이너리 파일 여러 개 조회
    @RequestMapping(method = RequestMethod.GET, path = "")
    public ResponseEntity<List<BinaryContentDto.BinaryContentResponse>> findAllByIds(
            @RequestParam List<UUID> ids
    ) {
        List<BinaryContentDto.BinaryContentResponse> list =
                binaryContentService.findAllByIdIn(ids);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
