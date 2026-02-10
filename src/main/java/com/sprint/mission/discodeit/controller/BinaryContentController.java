package com.sprint.mission.discodeit.controller;

import java.util.List;
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
@RequestMapping("/binary-contents")
@RequiredArgsConstructor
public class BinaryContentController {
	private final BinaryContentService binaryContentService;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<BinaryContentResponseDto>> getBinaryContent(@RequestParam("id") List<UUID> ids) {
		return ResponseEntity.status(HttpStatus.OK).body(binaryContentService.findAllByIdIn(ids));
	}

}
