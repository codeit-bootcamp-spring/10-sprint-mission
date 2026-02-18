package com.sprint.mission.discodeit.controller;

import java.io.IOException;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sprint.mission.discodeit.dto.BinaryContentResponseDto;
import com.sprint.mission.discodeit.service.BinaryContentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/binaryContent")
@RequiredArgsConstructor
public class BinaryContentController {
	private final BinaryContentService binaryContentService;

	@RequestMapping(value = "/find", method = RequestMethod.GET)
	public ResponseEntity<BinaryContentResponseDto> getBinaryContent(@RequestParam("binaryContentId") UUID id) throws
		IOException {
		return ResponseEntity.status(HttpStatus.OK).body(binaryContentService.findById(id));
	}

}
