package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {
    /*
    ### **바이너리 파일 다운로드**
    - [ ]  바이너리 파일을 1개 또는 여러 개 조회할 수 있다.
     */

    private final BinaryContentService binaryContentService;

    @RequestMapping(path = "/find",method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> findProfileImage(@RequestParam UUID binaryContentId){
        return ResponseEntity.status(HttpStatus.OK).body(binaryContentService.find(binaryContentId));
    }

    @RequestMapping(path = "/find-all",method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> findImages(@RequestParam List<UUID> binaryContentIds){
        return ResponseEntity.status(HttpStatus.OK).body(binaryContentService.findAllByIdIn(binaryContentIds));
    }
}
