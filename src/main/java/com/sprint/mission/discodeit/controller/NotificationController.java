package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> findAll(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        UUID receiverId = userDetails.getUserDto().id();
        return ResponseEntity.ok(notificationService.findAllByReceiverId(receiverId));
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal DiscodeitUserDetails userDetails) {
        UUID requesterId = userDetails.getUserDto().id();
        notificationService.delete(notificationId, requesterId);
        return ResponseEntity.noContent().build();
    }
}
