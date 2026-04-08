package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusPatchDto;
import com.sprint.mission.discodeit.dto.UserStatusPostDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    private final UserStatusMapper userStatusMapper;

    public UserStatusDto create(UserStatusPostDto userStatusPostDto) {
        User user = userRepository.findById(userStatusPostDto.userId())
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.USER_NOT_FOUND, userStatusPostDto.userId())
            );

        try {
            return userStatusMapper.toDto(
                userStatusRepository.save(new UserStatus(user))
            );
        } catch (DataIntegrityViolationException e) {
            throw new BusinessLogicException(ExceptionCode.USER_STATUS_DUPLICATED);
        }

    }

    @Transactional(readOnly = true)
    public UserStatusDto findById(UUID id) {
        return userStatusMapper.toDto(
            userStatusRepository.findById(id)
                .orElseThrow(() ->
                    new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND))
        );
    }

    @Transactional(readOnly = true)
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream()
            .map(userStatusMapper::toDto)
            .collect(Collectors.toList());
    }

    public UserStatusDto updateByUserId(UUID userId,
        UserStatusPatchDto userStatusPatchDto) {
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.USER_STATUS_WITH_USER_ID_NOT_FOUND, userId)
            );

        userStatus.updateLastAccessedTime(userStatusPatchDto.newLastActiveAt());
        return userStatusMapper.toDto(userStatus);
    }

    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new BusinessLogicException(ExceptionCode.USER_STATUS_NOT_FOUND, id);
        }

        userStatusRepository.deleteById(id);
    }

}
