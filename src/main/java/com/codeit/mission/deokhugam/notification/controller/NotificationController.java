package com.codeit.mission.deokhugam.notification.controller;

import com.codeit.mission.deokhugam.notification.dto.request.NotificationRequestQuery;
import com.codeit.mission.deokhugam.notification.dto.request.NotificationUpdateRequest;
import com.codeit.mission.deokhugam.notification.dto.response.CursorPageResponseNotificationDto;
import com.codeit.mission.deokhugam.notification.dto.response.NotificationDto;
import com.codeit.mission.deokhugam.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "알림 관리", description = "알림 관련 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

  private final NotificationService notificationService;

  @Operation(
    summary = "알림 목록 조회",
    operationId = "find_all_4",
    description = "사용자의 알림 목록을 조회합니다."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공"),
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (정렬 방향 오류, 페이지네이션 파라미터 오류, 사용자 ID 누락)"),
    @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @GetMapping
  public ResponseEntity<CursorPageResponseNotificationDto> findByUserId(
    @RequestParam(name = "userId") UUID userId,
    @Valid @ParameterObject @ModelAttribute NotificationRequestQuery query) {

    return ResponseEntity.ok(notificationService.findByUserId(userId, query));
  }

  @Operation(
    summary = "알림 읽음 상태 업데이트",
    operationId = "update_4",
    description = "특정 알림의 읽음 상태를 업데이트합니다."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "200", description = "알림 상태 업데이트 성공"),
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (입력값 검증 실패, 요청자 ID 누락)"),
    @ApiResponse(responseCode = "403", description = "알림 수정 권한 없음"),
    @ApiResponse(responseCode = "404", description = "알림 정보 없음"),
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @PatchMapping("/{notificationId}")
  public ResponseEntity<NotificationDto> updateById(
    @PathVariable UUID notificationId,
    @RequestHeader("Deokhugam-Request-User-ID") UUID requestUserId,
    @Valid @RequestBody NotificationUpdateRequest requestDto) {

    log.info("[NOTIFICATION_UPDATE] single notification update request: requestUserId={}",
      requestUserId);

    return ResponseEntity.ok(
      notificationService.updateById(notificationId, requestUserId,
        requestDto));
  }

  @Operation(
    summary = "모든 알림 읽음 처리",
    operationId = "update_all_4",
    description = "사용자의 모든 알림을 읽음 상태로 처리합니다."
  )
  @ApiResponses({
    @ApiResponse(responseCode = "204", description = "알림 읽음 처리 성공"),
    @ApiResponse(responseCode = "400", description = "잘못된 요청 (사용자 ID 누락)"),
    @ApiResponse(responseCode = "404", description = "사용자 정보 없음"),
    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
  })
  @PatchMapping("/read-all")
  public ResponseEntity<Void> updateAllAsRead(
    @RequestHeader("Deokhugam-Request-User-ID") UUID userId) {

    log.info("[NOTIFICATION_UPDATE_ALL] all notification update request: userId={}", userId);

    notificationService.updateByUserId(userId);
    return ResponseEntity.noContent().build();
  }

}
