package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/messages")
public class MessageController {

  private final MessageService messageService;

  // 메시지 보내기
  @RequestMapping(method = RequestMethod.POST)
  public MessageResponseDto send(@RequestBody MessageCreateDto dto) {
    return messageService.create(dto);
  }

  // 메시지 수정
  @RequestMapping(value = "/{messageId}", method = RequestMethod.PATCH)
  public MessageResponseDto update(@PathVariable UUID messageId,
      @RequestBody MessageUpdateDto dto) {
    return messageService.update(messageId, dto);
  }

  // 메시지 삭제
  @RequestMapping(value = "/{messageId}", method = RequestMethod.DELETE)
  public void delete(@PathVariable UUID messageId) {
    messageService.delete(messageId);
  }

  // 조회
  @RequestMapping(method = RequestMethod.GET)
  public List<MessageResponseDto> getMessages(
      @RequestParam UUID channelId,
      @RequestParam String keyword,
      @RequestParam UUID userId) {

    // 특정 채널의 메시지 목록 조회
    if (channelId != null) {
      return messageService.findAllByChannelId(channelId);
    }

    // 키워드 검색
    if (keyword != null) {
      return messageService.searchMessage(channelId, keyword);
    }

    // 특정 사용자가 보낸 메시지 조회
    if (userId != null) {
      return messageService.getUserMessages(userId);
    }

    // 모든 조건 없으면 전체 조회
    return new ArrayList<>();
  }


}
