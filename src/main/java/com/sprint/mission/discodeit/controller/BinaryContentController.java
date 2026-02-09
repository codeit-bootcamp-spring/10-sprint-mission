package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class BinaryContentController {
    private final BinaryContentService binaryContentService;
    private final ChannelService channelService;
    private final MessageService messageService;

    //이미지 단순 조회(프로필, 메시지 내 파일 단일 모두 가능)
    @RequestMapping(value = "/api/binaryContent/find", method = RequestMethod.GET)
    public ResponseEntity<BinaryContentResponseDto> getSimpleFiles(
            @RequestParam UUID binaryContentId
    ){
        return ResponseEntity.ok(binaryContentService.find(binaryContentId));
    }

    @RequestMapping(value = "/files", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponseDto>> getAllFiles(
            @RequestParam List<UUID> binaryContentIdList
    ){
        return ResponseEntity.ok(binaryContentIdList.stream()
                .map(binaryContentService::find)
                .toList());
    }

    //메시지 내부 이미지 전원 조회
    @RequestMapping(value = "/channels/{channelId}/messages/{messageId}/files", method = RequestMethod.GET)
    public ResponseEntity<List<BinaryContentResponseDto>> getMessagesFiles(
            @PathVariable UUID messageId
    ){
        return ResponseEntity.ok(messageService.find(messageId).attachments().stream()
                .map(binaryContentService::find)
                .toList());
    }
}
