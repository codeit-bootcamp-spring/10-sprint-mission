package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/binaryContent")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    @RequestMapping(method = RequestMethod.GET,value = "/find")
    public ResponseEntity<BinaryContent> find(@RequestParam("binaryContentId") UUID contentId){
        BinaryContent content = binaryContentService.findEntity(contentId);
        return ResponseEntity.ok(content);
    }

    // 단건 조회(다운로드)
    @RequestMapping(method = RequestMethod.GET, value = "/{contentId}")
    public ResponseEntity<?> download(@PathVariable UUID contentId){
        // 단건 조회
        BinaryContentDto binaryContentDto = binaryContentService.find(contentId);
        // 한글 깨짐 방지
        String encodedFileName = new String(binaryContentDto.getFileName()
                .getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1);
        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        // 파일 종류
        headers.setContentType(MediaType.parseMediaType(binaryContentDto.getContentType()));
        // 파일 크기
        headers.setContentLength(binaryContentDto.getSize());
        // Content-Disposition: 다운로드 시 저장될 파일 이름 설정
        headers.setContentDispositionFormData("attachment", encodedFileName);

        return new ResponseEntity<>(binaryContentDto.getContents(), headers, HttpStatus.OK);

    }

    // 여러 개 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<BinaryContentDto> findAll(@RequestParam List<UUID> contentIds){
        return binaryContentService.findAllByIdIn(contentIds);
    }

}
