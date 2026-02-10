package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.POST, value = "/users", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UUID> uploadProfileFile(@RequestPart MultipartFile file) {
        if (file.getContentType() == null || !file.getContentType().startsWith("image/")) {
            throw new IllegalArgumentException("이미지만 업로드 가능합니다.");
        }
        BinaryContentDto.Response response = binaryContentService.create(multipartFileToCreateRequest(file));
        return ResponseEntity.status(HttpStatus.CREATED).body(response.id());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<UUID>> uploadMessageFiles(@RequestPart("files") List<MultipartFile> files) {
        List<UUID> ids = files.stream()
                .map(file -> binaryContentService.create(multipartFileToCreateRequest(file)).id())
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(ids);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/find")
    public ResponseEntity<BinaryContentDto.Response> findBinaryContent(@RequestParam("binary-content-id") UUID binaryContentId) {
        BinaryContentDto.Response response = binaryContentService.find(binaryContentId);
        return ResponseEntity.ok(response);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentDto.Response>> findAll(@RequestParam("binary-content-ids") List<UUID> binaryContentIds) {
        List<BinaryContentDto.Response> response = binaryContentService.findAllByIn(binaryContentIds);
        return ResponseEntity.ok(response);
    }

    private BinaryContentDto.CreateRequest multipartFileToCreateRequest(MultipartFile file) {
        try {
            return new BinaryContentDto.CreateRequest(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new UncheckedIOException("파일 읽기 오류", e);
        }
    }
}
