package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentIdsRequest;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponse;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
// TODO: URL에서는 소문자 사용이 관례
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @Autowired
    public BinaryContentController(BinaryContentService binaryContentService) {
        this.binaryContentService = binaryContentService;
    }

    // 바이너리 파일을 1개 또는 여러 개 조회할 수 있다.
    // 단건 조회 (다운로드 등)
    @RequestMapping(value = "/{binaryContentId}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponse> findById(@PathVariable UUID binaryContentId) {
        BinaryContentResponse response = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(response);
    }

    // 다건 조회 (특정 메시지의 첨부파일 목록 등)
    // 사용자가 메시지를 보낼 때 사진 3장을 한 번에 올렸다고 가정해보면
    // 프론트에서 사진 3장의 상세 정보를 가져오기 위해 다건 조회
    @RequestMapping(method = RequestMethod.POST)
    // GET은 Body를 권장하지 않음 -> POST 사용
    // RequestParam -> RequestBody / DTO로 받아서 처리(URL 길이 제한 문제 및 확장성 확보)
    public ResponseEntity<List<BinaryContentResponse>> findAllByIds(@RequestBody BinaryContentIdsRequest request) {
        List<BinaryContentResponse> response = binaryContentService.findAllByIdIn(request.ids());
        return ResponseEntity.ok(response);
    }

//    @RequestMapping(value = "/find",method = RequestMethod.GET)
//    public ResponseEntity<BinaryContent> findBinaryContent(
//            @RequestParam UUID binaryContentId
//    ) {
//        BinaryContentResponse response = binaryContentService.find(binaryContentId);
//
//    }
}
