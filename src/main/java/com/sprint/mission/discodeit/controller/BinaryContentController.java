package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.BinaryContentApi;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 바이너리 컨텐츠 관련 요청을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/binaryContents")
@RequiredArgsConstructor
public class BinaryContentController implements BinaryContentApi {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;

    @Override
    public ResponseEntity<BinaryContentDto.Response> findBinaryContent(UUID binaryContentId) {
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    @Override
    public ResponseEntity<List<BinaryContentDto.Response>> findAll(List<UUID> binaryContentIds) {
        return ResponseEntity.ok(binaryContentService.findAllByIn(binaryContentIds));
    }

    @Override
    public ResponseEntity<?> download(UUID binaryContentId) {
        BinaryContentDto.Response response = binaryContentService.find(binaryContentId);
        return binaryContentStorage.download(response);
    }
}
