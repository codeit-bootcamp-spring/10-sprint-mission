package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.response.NotificationDto;
import com.sprint.mission.discodeit.security.auth.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notification", description = "Notification API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "Notification 조회", operationId = "find_1")
    @GetMapping
    public ResponseEntity<List<NotificationDto>> findAll(@AuthenticationPrincipal DiscodeitUserDetails discodeitUserDetails) {
        List<NotificationDto> response = notificationService.findAll(discodeitUserDetails.getUserDto().id());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Notification 삭제", operationId = "delete_3")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Void> deleteNotification(@PathVariable UUID notificationId,
                                                   @AuthenticationPrincipal DiscodeitUserDetails discodeitUserDetails) {
        notificationService.delete(notificationId, discodeitUserDetails.getUserDto().id());

        return ResponseEntity.noContent().build();
    }
}
