package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.UserStatusApi;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * 사용자 상태 관련 요청을 처리하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/userStatuses")
@RequiredArgsConstructor
public class UserStatusController implements UserStatusApi {

    private final UserStatusService userStatusService;

    @Override
    public ResponseEntity<UserStatusDto.Response> createUserStatus(UserStatusDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userStatusService.create(request));
    }

    @Override
    public ResponseEntity<UserStatusDto.Response> findUserStatus(UUID userStatusId) {
        return ResponseEntity.ok(userStatusService.find(userStatusId));
    }

    @Override
    public ResponseEntity<UserStatusDto.Response> findUserStatusByUserId(UUID userId) {
        return ResponseEntity.ok(userStatusService.findByUserId(userId));
    }

    @Override
    public ResponseEntity<List<UserStatusDto.Response>> findAllUserStatus() {
        return ResponseEntity.ok(userStatusService.findAll());
    }

    @Override
    public ResponseEntity<UserStatusDto.Response> updateUserStatus(UUID userStatusId, UserStatusDto.UpdateRequest request) {
        return ResponseEntity.ok(userStatusService.update(userStatusId, request));
    }

    @Override
    public ResponseEntity<Void> deleteUserStatus(UUID userStatusId) {
        userStatusService.delete(userStatusId);
        return ResponseEntity.noContent().build();
    }
}
