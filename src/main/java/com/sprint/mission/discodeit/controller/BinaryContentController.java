package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binaryContent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.service.BinaryContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;

    // 단건 조회
    @RequestMapping(value = "/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> getBinaryContent(@RequestParam("binaryContentId") UUID id){
        BinaryContent response = binaryContentService.findId(id);
        return ResponseEntity.ok(response);
    }

    // 다수 조회
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UUID>> getAllBinaryContent(){
        List<UUID> responseList = binaryContentService.findAllIdIn();
        return ResponseEntity.ok(responseList);
    }

    // 프로필 조회
    @RequestMapping(value = "/user/{user-id}", method = RequestMethod.GET)
    public ResponseEntity<BinaryContent> getProfileImg(@PathVariable("user-id") UUID userId){
        BinaryContent response = binaryContentService.findBinaryContentByUserId(userId);
        return ResponseEntity.ok(response);
    }

    // 메시지 첨부파일 조회
    @RequestMapping(value = "/message/{message-id}", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContent>> getAllMessageBinaryContent(@PathVariable("message-id") UUID messageId){
        List<BinaryContent> responseList = binaryContentService.findAllByMessageId(messageId);
        return ResponseEntity.ok(responseList);
    }
}
