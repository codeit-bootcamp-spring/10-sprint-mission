package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;import com.sprint.mission.discodeit.service.BinaryContentService;import lombok.RequiredArgsConstructor;import org.springframework.http.HttpStatus;import org.springframework.http.ResponseEntity;import org.springframework.web.bind.annotation.RequestMapping;import org.springframework.web.bind.annotation.RequestMethod;import org.springframework.web.bind.annotation.RequestParam;import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {

    private final BinaryContentService binaryContentService;

    //바이너리 파일을 1개 또는 여러 개 조회할 수 있다.
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<?> findById(@RequestParam UUID binaryContentId) {
        BinaryContentDto.Response response = binaryContentService.findById(binaryContentId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @RequestMapping(value = "/findAll", method = RequestMethod.GET)
    public ResponseEntity<?> findAll(@RequestParam List<UUID> contentsIds) {
        List<BinaryContentDto.Response> response = binaryContentService.findAllByIdIn(contentsIds);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
