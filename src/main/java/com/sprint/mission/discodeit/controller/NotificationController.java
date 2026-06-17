package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.config.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.dto.data.NotificationDto;
import com.sprint.mission.discodeit.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationDto>> findAll(
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ) {
        UUID receiverId = userDetails.getUserDto().id();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(notificationService.findAllByReceiverId(receiverId));
    }

    @DeleteMapping("{notificationId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID notificationId,
            @AuthenticationPrincipal DiscodeitUserDetails userDetails
    ) {
        UUID requesterId = userDetails.getUserDto().id();

        notificationService.delete(notificationId, requesterId);

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}