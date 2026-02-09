package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/messages")
public class MessageController {
    private final MessageService messageService;

    // 메시지 보내기
    @RequestMapping(method = RequestMethod.POST)
    public MessageResponseDto send(@RequestBody MessageCreateDto dto) {
        return messageService.create(dto);
    }

    // 메시지 수정
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public MessageResponseDto update(@PathVariable UUID id, @RequestBody MessageUpdateDto dto) {
        return messageService.update(id, dto);
    }

    // 메시지 삭제
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID id) {
        messageService.delete(id);
    }

    // 특정 채널의 메시지 목록 조회
    @RequestMapping(value = "/channel/{channel-id}", method = RequestMethod.GET)
    public List<MessageResponseDto> findAllByChannelId(@PathVariable("channel-id") UUID channelId) {
        return messageService.findAllByChannelId(channelId);
    }

    // 메시지 검색
    @RequestMapping(value = "/{channel-id}/search", method = RequestMethod.GET)
    public List<MessageResponseDto> srearchMessage(@PathVariable("channel-id") UUID channelId,
                                                   @RequestParam String keyword) {
        return messageService.searchMessage(channelId, keyword);
    }

    // 특정 사용자가 보낸 메시지 목록 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<MessageResponseDto> getUserMessages(@RequestParam UUID userId){
        return messageService.getUserMessages(userId);
    }

}
