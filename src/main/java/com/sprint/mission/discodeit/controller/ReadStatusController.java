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

import com.sprint.mission.discodeit.dto.ReadStatusPostDto;
import com.sprint.mission.discodeit.dto.ReadStatusResponseDto;
import com.sprint.mission.discodeit.service.ReadStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/readstatuses")
@RequiredArgsConstructor
public class ReadStatusController {
	public final ReadStatusService readStatusService;

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<ReadStatusResponseDto> createReadStatus(
		@RequestBody ReadStatusPostDto readStatusPostDto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(readStatusService.create(readStatusPostDto));
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<ReadStatusResponseDto> updateReadStatus(@PathVariable UUID id) {
		return ResponseEntity.status(HttpStatus.OK).body(readStatusService.update(id));
	}

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<List<ReadStatusResponseDto>> getReadStatusByUserId(
		@RequestParam(value = "user") UUID userId) {
		return ResponseEntity.status(HttpStatus.OK).body(readStatusService.findAllByUserId(userId));
	}
}
