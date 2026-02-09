package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateDto dto) {
        User user = userRepository.findById(dto.userId())
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        if(userStatusRepository.existsByUserId(dto.userId())) {
            throw new BusinessLogicException(ExceptionCode.USER_STATUS_ALREADY_EXIST);
        }
        return userStatusRepository.save(new UserStatus(dto.userId()));
    }

    @Override
    public UserStatus update(UUID id, UserStatusUpdateDto dto) {
        UserStatus status = find(id);
        status.update();
        return userStatusRepository.save(status);
    }

    @Override
    public UserStatus updateByUserId(UUID userId, UserStatusUpdateDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_NOT_FOUND));
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
        status.update();
        return userStatusRepository.save(status);
    }

    @Override
    public UserStatus find(UUID id) {
        return userStatusRepository.find(id)
                .orElseThrow(()->new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND));
    }

    @Override
    public List<UserStatus> findAll() {
        return userStatusRepository.findAll();
    }

    @Override
    public void delete(UUID id) {
        if(! userStatusRepository.existsById(id)){
            throw new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND);
        }
        userStatusRepository.delete(id);
    }
}
