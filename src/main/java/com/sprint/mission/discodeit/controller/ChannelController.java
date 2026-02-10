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

import com.sprint.mission.discodeit.dto.ChannelPatchDto;
import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDto;
import com.sprint.mission.discodeit.dto.PublicChannelPostDto;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/channels")
@RequiredArgsConstructor
public class ChannelController {
	private final ChannelService channelService;

	@RequestMapping(value = "/public", method = RequestMethod.POST)
	public ResponseEntity<ChannelResponseDto> createPublicChannel(
		@RequestBody PublicChannelPostDto publicChannelPostDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(channelService.createPublicChannel(publicChannelPostDto));
	}

	@RequestMapping(value = "/private", method = RequestMethod.POST)
	public ResponseEntity<ChannelResponseDto> createPrivateChannel(
		@RequestBody PrivateChannelPostDto privateChannelPostDto) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(channelService.createPrivateChannel(privateChannelPostDto));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<ChannelResponseDto> updatePublicChannel(@PathVariable UUID id,
		@RequestBody ChannelPatchDto channelPatchDto) {
		return ResponseEntity.status(HttpStatus.OK).body(channelService.update(id, channelPatchDto));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<ChannelResponseDto> updatePublicChannel(@PathVariable UUID id) {
		channelService.delete(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<ChannelResponseDto>> getChannelsByUserId(@RequestParam(value = "user") UUID userId) {
		return ResponseEntity.status(HttpStatus.OK).body(channelService.findAllByUserId(userId));
	}
}
