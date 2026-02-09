package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.BinaryContentResponseDTO;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/binarycontents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // 바이너리 파일 1개 조회
    @RequestMapping(value = "/{binarycontent-id}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<BinaryContentResponseDTO> find(@PathVariable("binarycontent-id") UUID binaryContentId) {
        BinaryContentResponseDTO response = binaryContentService.find(binaryContentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // 바이너리 파일 여러 개 조회
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<BinaryContentResponseDTO>> findAllByIdIn(@RequestParam List<UUID> ids) {
        List<BinaryContentResponseDTO> response = binaryContentService.findAllByIdIn(ids);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
