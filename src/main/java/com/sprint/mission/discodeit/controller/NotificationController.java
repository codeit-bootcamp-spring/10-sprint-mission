package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notification")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    @GetMapping
    public ResponseEntity<List<NotificationDto>> findMine(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        UUID receiverId = userDetails.getUserDto().id();
        return ResponseEntity.ok(notificationService.findAllByReceiverId(receiverId));
    }

    @Operation(summary = "알림 확인(삭제)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "알림 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청"),
            @ApiResponse(responseCode = "403", description = "본인의 알림이 아님"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음")
    })
    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "삭제할 알림 ID") @PathVariable UUID notificationId,
            @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        notificationService.delete(notificationId, userDetails.getUserDto().id());
    }
}
