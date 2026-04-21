package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userStatus.UserStatusCreateDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusDto;
import com.sprint.mission.discodeit.dto.userStatus.UserStatusUpdateRequest;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusExistException;
import com.sprint.mission.discodeit.exception.userStatus.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusDto create(UserStatusCreateDto dto) {
        UUID userId = dto.getUserId();
        // 관련된 사용자가 없다면 예외처리
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        // 해당 사용자가 이미 userStatus를 가지고 있다면 예외처리
        if(userStatusRepository.existsByUserId(userId)){
            throw new UserStatusExistException(userId);
        }

        // 만약 예외가 없다면 객체 생성
        UserStatus userStatus = new UserStatus(user);
        // 저장
        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public UserStatusDto findUserStatus(UUID id) {
        UserStatus userStatus = getUserStatus(id);

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll().stream()
                            .map(userStatusMapper::toDto)
                            .toList();
    }

    @Override
    @Transactional
    public UserStatusDto update(UUID userId, UserStatusUpdateRequest request) {
        // 수정할 객체 불러옴
        UserStatus userStatus = getUserStatus(userId);
        // 최신 접속 시간 업데이트
        userStatus.updateAccessTime(request.getNewLastActiveAt());

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }

    private UserStatus getUserStatus(UUID id){
        return userStatusRepository.findByUserId(id).orElseThrow(() -> new UserStatusNotFoundException(id));
    }
}
