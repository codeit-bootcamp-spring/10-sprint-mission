package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStatusService userStatusService;

    // 유저 등록
    @RequestMapping(method = RequestMethod.POST)
    public UserDto.Response create(@RequestBody UserDto.CreateRequest request) {
        return userService.create(request);
    }

    // 유저 정보 수정
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public UserDto.Response update(@PathVariable UUID id,
                                   @RequestBody UserDto.UpdateRequest request) {
        if (!id.equals(request.id())) throw new BusinessException(ErrorCode.PATH_ID_MISMATCH);

        return userService.update(request);
    }

    // 유저 삭제
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }

    // 모든 유저 조회
    @RequestMapping(method = RequestMethod.GET)
    public List<UserDto.Response> findAll() {
        return userService.findAll();
    }

    // 유저 온라인 상태 업데이트
    @RequestMapping(value = "/{id}/status", method = RequestMethod.PATCH)
    public UserStatusDto.Response updateStatus(@PathVariable UUID id,
                             @RequestBody UserStatusDto.UpdateRequest request) {
        if (!id.equals(request.id())) throw new BusinessException(ErrorCode.PATH_ID_MISMATCH);

        return userStatusService.update(request);
    }
}
