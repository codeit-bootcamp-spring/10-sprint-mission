package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getNotification(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
            ){
        List<NotificationDto> response = notificationService.findNotifications(userDetails.getUserDto().getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> readNotification(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails,
            @PathVariable UUID notificationId
    ){
        notificationService.readNotifications(notificationId, userDetails.getUserDto().getId());
        return ResponseEntity.noContent().build();
    }


}
