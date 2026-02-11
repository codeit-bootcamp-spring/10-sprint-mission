package com.sprint.mission.discodeit.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.sprint.mission.discodeit.dto.UserPatchDto;
import com.sprint.mission.discodeit.dto.UserPostDto;
import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserStatusResponseDTO;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
	private final UserService userService;
	private final UserStatusService userStatusService;

	// 사용자 등록
	@RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserResponseDto> createUser(@RequestPart("userPostDto") UserPostDto userPostDto,
		@RequestPart("profileImage") MultipartFile profileImage) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userPostDto, profileImage));
	}

	// 사용자 정보 수정
	@RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
	public ResponseEntity<UserResponseDto> updateUser(@PathVariable UUID id,
		@RequestBody UserPatchDto userPatchDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(userService.updateUser(id, userPatchDTO));
	}

	// 사용자 삭제
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
		userService.delete(id);
		return ResponseEntity.noContent().build();
	}

	// 모든 사용자 조회
	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public ResponseEntity<List<UserResponseDto>> getAllUser() {
		return ResponseEntity.ok(userService.findAll());
	}

	// 사용자 온라인 상태 업데이트
	@RequestMapping(value = "/{id}/user-status", method = RequestMethod.PATCH)
	public ResponseEntity<UserStatusResponseDTO> updateUserStatus(@PathVariable UUID id) {
		return ResponseEntity.ok(userStatusService.updateByUserId(id));
	}
}
