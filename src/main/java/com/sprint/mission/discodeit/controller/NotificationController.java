package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.config.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> findAll(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        UUID receiverId = userDetails.getUserDto().id();
        log.info("알림 목록 조회 요청: receiverId={}", receiverId);
        List<NotificationDto> notifications = notificationService.findAllByReceiverId(receiverId);
        log.debug("알림 목록 조회 응답: count={}", notifications.size());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notifications);
    }

    @DeleteMapping("{notificationId}")
    public ResponseEntity<Void> delete(@PathVariable("notificationId") UUID notificationId,
            @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        UUID requesterId = userDetails.getUserDto().id();
        log.info("알림 삭제 요청: id={}, requesterId={}", notificationId, requesterId);
        notificationService.delete(notificationId, requesterId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
