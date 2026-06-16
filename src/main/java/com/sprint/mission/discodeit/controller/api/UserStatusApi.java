package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "UserStatus", description = "사용자 상태(온라인/활동) 관리 API")
public interface UserStatusApi {

    @Operation(summary = "상태 정보 생성")
    @PostMapping
    ResponseEntity<UserStatusDto.Response> createUserStatus(@RequestBody @Valid UserStatusDto.CreateRequest request);

    @Operation(summary = "상태 정보 단건 조회")
    @GetMapping("/{userStatusId}")
    ResponseEntity<UserStatusDto.Response> findUserStatus(
            @Parameter(description = "상태 정보 ID") @PathVariable("userStatusId") UUID userStatusId
    );

    @Operation(summary = "사용자 ID로 상태 조회")
    @GetMapping("/user/{userId}")
    ResponseEntity<UserStatusDto.Response> findUserStatusByUserId(
            @Parameter(description = "사용자 ID") @PathVariable("userId") UUID userId
    );

    @Operation(summary = "전체 상태 목록 조회")
    @GetMapping
    ResponseEntity<List<UserStatusDto.Response>> findAllUserStatus();

    @Operation(summary = "상태 정보 수정")
    @PatchMapping("/{userStatusId}")
    ResponseEntity<UserStatusDto.Response> updateUserStatus(
            @Parameter(description = "상태 정보 ID") @PathVariable("userStatusId") UUID userStatusId,
            @RequestBody @Valid UserStatusDto.UpdateRequest request
    );

    @Operation(summary = "상태 정보 삭제")
    @DeleteMapping("/{userStatusId}")
    ResponseEntity<Void> deleteUserStatus(
            @Parameter(description = "상태 정보 ID") @PathVariable("userStatusId") UUID userStatusId
    );
}
