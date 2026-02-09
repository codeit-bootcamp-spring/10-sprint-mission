package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByStatusIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByUserIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDTO;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/user-statuses")
@RequiredArgsConstructor
public class UserStatusController {

    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity updateUserStatusById(
            @RequestBody UpdateStatusByStatusIdRequestDTO dto
            ) {
        UserStatusResponseDTO updated = userStatusService.updateUserStatus(dto);

        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/by-user", method = RequestMethod.PATCH)
    public ResponseEntity updateUserStatusByUserId(
            @RequestBody UpdateStatusByUserIdRequestDTO dto
    ) {
        UserStatusResponseDTO updated = userStatusService.updateStatusByUserId(dto.userId(), dto);

        return ResponseEntity.ok(updated);
    }
}
