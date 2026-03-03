package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.exception.code.ErrorCode;
import com.sprint.mission.discodeit.common.exception.custom.APIException;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusServiceDTO.UserStatusResponse;
import com.sprint.mission.discodeit.dto.userstatus.command.UserStatusUpdateCommand;
import com.sprint.mission.discodeit.dto.userstatus.request.UserStatusCreateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService extends BasicDomainService<UserStatus> implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatusResponse create(UserStatusCreateRequest request) {
        User user = findUser(request.userId());
        if (userStatusRepository.existsByUserId(user.getId())) {
            throw new APIException(ErrorCode.USERSTATUS_ALREADY_EXIST, request.userId());
        }
        UserStatus status = new UserStatus(user.getId());
        userStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public UserStatusResponse find(UUID id) {
        return findById(id).toResponse();
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return userStatusRepository.streamAll(stream -> stream.map(UserStatus::toResponse))
                .toList();
    }

    @Override
    public UserStatusResponse update(UserStatusUpdateCommand command) {
        UserStatus status = findByUserId(command.userId());
        status.update(command.datetime());
        userStatusRepository.save(status);
        return status.toResponse();
    }

    @Override
    public void delete(UUID id) {
        deleteIfExist(id, userStatusRepository,
                () -> new APIException(ErrorCode.USERSTATUSID_NOT_FOUND, id));
    }

    @Override
    protected UserStatus findById(UUID id) {
        return findEntityById(id, userStatusRepository,
                () -> new APIException(ErrorCode.USERSTATUSID_NOT_FOUND, id));
    }

    private UserStatus findByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new APIException(ErrorCode.USERSTATUS_NOT_FOUND_BY_USERID, userId));
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new APIException(ErrorCode.USERID_NOT_FOUND, userId));
    }
}
