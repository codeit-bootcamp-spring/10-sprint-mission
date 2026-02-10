package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.MessagePatchDto;
import com.sprint.mission.discodeit.dto.MessagePostDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
public class MessageController {
	private final MessageService messageService;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<MessageResponseDto> createMessage(@RequestBody MessagePostDto messagePostDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(messageService.create(messagePostDto));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<MessageResponseDto> updateMessage(@PathVariable UUID id,
		@RequestBody MessagePatchDto messagePatchDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(messageService.updateById(id, messagePatchDto));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<MessageResponseDto> deleteMessage(@PathVariable UUID id) {
		messageService.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<MessageResponseDto>> getMessage(@RequestParam(value = "channel") UUID channelId) {
		return ResponseEntity.status(HttpStatus.OK).body(messageService.findByChannelId(channelId));
	}
}
