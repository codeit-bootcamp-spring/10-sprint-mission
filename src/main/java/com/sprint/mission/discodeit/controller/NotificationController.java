package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "알림 API")
public class NotificationController {

    private final NotificationService notificationService;

    @RequestMapping(method = RequestMethod.GET)
    @Operation(summary = "알림 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 User", content = @Content(examples = @ExampleObject(value = "Unauthorized")))
    })
    public ResponseEntity<List<NotificationDto>> findAll(
            @AuthenticationPrincipal DiscodeitUserDetails discodeitUserDetails
    ) {
        UUID receiverId = discodeitUserDetails.getUserDto().id();
        List<NotificationDto> notificationDtoList = notificationService.findAllByReceiverId(receiverId);

        return ResponseEntity.status(HttpStatus.OK).body(notificationDtoList);
    }

    @RequestMapping(value = "/{notificationId}", method = RequestMethod.DELETE)
    @Operation(summary = "알림 확인")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "알림 확인 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 요청", content = @Content(examples = @ExampleObject(value = "Unauthorized"))),
            @ApiResponse(responseCode = "403", description = "인가되지 않은 요청(본인 알림만 수행 가능)", content = @Content(examples = @ExampleObject("Forbidden"))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 알림", content = @Content(examples = @ExampleObject(value = "Notification Not Found")))
    })
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal DiscodeitUserDetails discodeitUserDetails,
            @PathVariable UUID notificationId
    ) {
        UUID receiverId = discodeitUserDetails.getUserDto().id();

        notificationService.deleteByReceiverId(receiverId, notificationId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
