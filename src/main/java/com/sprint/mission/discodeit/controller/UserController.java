package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
@Slf4j
public class UserController {

    private final UserService userService;
    private final BinaryContentService binaryContentService;

    // 사용자 등록
    @Operation(summary = "사용자 등록", description = "새로운 사용자를 시스템에 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "등록 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class)))
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> join(
            @RequestPart("userCreateRequest") @Valid UserCreateRequest dto,
            @RequestPart(value = "profile", required = false) MultipartFile profile)
            throws IOException {

        log.info("사용자 등록 요청: username={}, email={}", dto.username(), dto.email());
        UserDto result = userService.create(dto, profile);
        log.info("사용자 등록 완료: username={}, email={}", dto.username(), dto.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // 사용자 정보 수정
    @Operation(summary = "사용자 정보 수정", description = "이름, 프로필 이미지 등 사용자의 기본 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @RequestMapping(value = "/{userId}", method = RequestMethod.PATCH, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> update(
            @PathVariable UUID userId,
            @RequestPart("userUpdateRequest") @Valid UserUpdateRequest dto,
            @RequestPart(value = "profile", required = false) MultipartFile profile)
            throws IOException {

        log.info("사용자 수정 요청: userId={}", userId);
        UserDto result = userService.update(userId, dto, profile);
        log.info("사용자 수정 완료: userId={}", userId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    // 사용자 삭제
    @Operation(summary = "사용자 삭제", description = "ID에 해당하는 사용자를 영구 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "삭제 성공")
    })
    @RequestMapping(value = "/{userId}", method = RequestMethod.DELETE)
    public ResponseEntity<Void> delete(@PathVariable UUID userId) {
        log.info("사용자 삭제 요청: userId={}", userId);
        userService.delete(userId);
        log.info("사용자 삭제 완료: userId={}", userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 모든 사용자 조회
    @Operation(summary = "모든 사용자 조회", description = "시스템에 등록된 전체 사용자 목록을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = UserDto.class))
                    ))
    })
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<UserDto>> findAll() {
        return new ResponseEntity<>(userService.findAll(), HttpStatus.OK);
    }

}
