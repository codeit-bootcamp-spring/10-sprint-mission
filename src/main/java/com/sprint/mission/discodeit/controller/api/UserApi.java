package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Tag(name = "User", description = "사용자 관리 API")
public interface UserApi {

    @Operation(summary = "사용자 등록", description = "신규 사용자를 등록합니다. 프로필 이미지를 첨부할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "중복된 이메일 또는 사용자 이름")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = @Encoding(name = "userCreateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
            )
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<UserDto.Response> createUser(
            @Parameter(description = "사용자 생성 정보") @RequestPart("userCreateRequest") @Valid UserDto.CreateRequest request,
            @Parameter(description = "프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile
    );

    @Operation(summary = "사용자 정보 수정", description = "사용자의 기본 정보 및 프로필 이미지를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(
                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                    encoding = @Encoding(name = "userUpdateRequest", contentType = MediaType.APPLICATION_JSON_VALUE)
            )
    )
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<UserDto.Response> updateUser(
            @Parameter(description = "수정할 사용자 ID") @PathVariable("userId") UUID userId,
            @Parameter(description = "수정 정보") @RequestPart("userUpdateRequest") @Valid UserDto.UpdateRequest request,
            @Parameter(description = "새로운 프로필 이미지") @RequestPart(value = "profile", required = false) MultipartFile profile
    );

    @Operation(summary = "사용자 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{userId}")
    ResponseEntity<Void> deleteUser(
            @Parameter(description = "삭제할 사용자 ID") @PathVariable("userId") UUID userId
    );

    @Operation(summary = "사용자 단건 조회")
    @GetMapping("/{userId}")
    ResponseEntity<UserDto.Response> findUser(
            @Parameter(description = "조회할 사용자 ID") @PathVariable("userId") UUID userId
    );

    @Operation(summary = "전체 사용자 목록 조회")
    @GetMapping
    ResponseEntity<List<UserDto.Response>> findAllUser();

    @Operation(summary = "사용자 온라인 상태 업데이트", description = "사용자의 마지막 활동 시간을 수동으로 업데이트합니다.")
    @PatchMapping("/{userId}/userStatus")
    ResponseEntity<UserStatusDto.Response> patchUserStatus(
            @Parameter(description = "사용자 ID") @PathVariable("userId") UUID userId,
            @RequestBody @Valid UserStatusDto.UpdateRequest request
    );

    @Operation(summary = "사용자 권한 변경", description = "특정 사용자의 권한을 변경합니다. (관리자 전용)")
    @PutMapping("/{userId}/role")
    ResponseEntity<UserDto.Response> updateUserRole(
            @Parameter(description = "대상 사용자 ID") @PathVariable("userId") UUID userId,
            @RequestBody @Valid UserRoleUpdateRequest request
    );
}
