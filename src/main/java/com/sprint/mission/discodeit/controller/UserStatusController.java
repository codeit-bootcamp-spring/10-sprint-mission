package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByStatusIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UpdateStatusByUserIdRequestDTO;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusDto;
import com.sprint.mission.discodeit.service.UserStatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/userStatus")
@RequiredArgsConstructor
@Slf4j
public class UserStatusController {

    private final UserStatusService userStatusService;

    @RequestMapping(method = RequestMethod.PATCH)
    public ResponseEntity updateUserStatusById(
            @RequestBody @Valid UpdateStatusByStatusIdRequestDTO dto
            ) {
        log.debug("[USERSTATUS_UPDATE_REQUEST] 유저 상태 수정 요청: userStatusId={}", dto.userStatusId());
        UserStatusDto updated = userStatusService.updateUserStatus(dto);

        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/by-user", method = RequestMethod.PATCH)
    public ResponseEntity updateUserStatusByUserId(
            @RequestBody @Valid UpdateStatusByUserIdRequestDTO dto
    ) {
        log.debug("[USERSTATUS_UPDATE_REQUEST] 유저 상태 수정 요청: userId={}", dto.userId());
        UserStatusDto updated = userStatusService.updateStatusByUserId(dto.userId(), dto);

        return ResponseEntity.ok(updated);
    }
}
