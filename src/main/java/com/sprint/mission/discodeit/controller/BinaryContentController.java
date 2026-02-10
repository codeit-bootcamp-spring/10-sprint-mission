package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.helper.EntityFinder;
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
    private final EntityFinder entityFinder;

    // 바이너리 파일 1개 조회
    @RequestMapping(method = RequestMethod.GET, path = "/api/binaryContent/find")
    public ResponseEntity<BinaryContent> findById(
            @RequestParam UUID binaryContentId
    ) {
        BinaryContent data = entityFinder.getBinaryContent(binaryContentId);
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
