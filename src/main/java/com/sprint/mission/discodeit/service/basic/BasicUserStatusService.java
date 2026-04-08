package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.UpdateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
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
@Transactional(readOnly = true)
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;
    private final UserStatusMapper userStatusMapper;

    @Override
    @Transactional
    public UserStatusDto create(CreateUserStatusRequestDto request){
        // 객체 생성자에 맞게 User 엔티티를 우선 조회
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("User not found with id " + request.userId()));

        if (userStatusRepository.findByUserId(request.userId()).isPresent()){
            throw new NoSuchElementException("UserStatus already exists for this id." + request.userId());
        }

        UserStatus userStatus = new UserStatus(user);
        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusDto find(UUID userStatusId){
        return userStatusMapper.toDto(getUserStatusEntity(userStatusId));
    }

    @Override
    public List<UserStatusDto> findAll(){
        return userStatusRepository.findAll().stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserStatusDto update(UUID userStatusId, UpdateUserStatusRequestDto request){
        UserStatus userStatus = getUserStatusEntity(userStatusId);
        userStatus.updateLastActiveAt();
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    @Transactional
    public UserStatusDto updateByUserId(UUID userId, UpdateUserStatusRequestDto request){
        return processUpdate(userId, request);
    }

    @Override
    @Transactional
    public UserStatusDto updateByUserId(UUID userId){
        return processUpdate(userId, null);
    }

    @Override
    @Transactional
    public void delete(UUID userStatusId){
        UserStatus userStatus = getUserStatusEntity(userStatusId);
        userStatusRepository.delete(userStatus);
    }

    private UserStatus getUserStatusEntity(UUID userStatusId){
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id" + userStatusId));
    }

    private UserStatusDto processUpdate(UUID userId, UpdateUserStatusRequestDto request){
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with user id " + userId));

        if (request != null && request.newLastActiveAt() != null){
            userStatus.updateLastActiveAt(request.newLastActiveAt());
        } else {
            userStatus.updateLastActiveAt();
        }

        return userStatusMapper.toDto(userStatus);
    }
}