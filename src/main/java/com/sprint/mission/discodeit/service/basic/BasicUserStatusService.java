package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.CreateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.UpdateUserStatusRequestDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserStatusMapper;
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
    private final UserStatusMapper userStatusMapper;

    @Override
    public UserStatusDto create(CreateUserStatusRequestDto request){
        if (!userRepository.existsById(request.getUserId())){
            throw new NoSuchElementException("User not found with id " + request.getUserId());
        }
        if (userStatusRepository.findByUserId(request.getUserId()).isPresent()){
            throw new NoSuchElementException("UserStatus already exists for this id." + request.getUserId());
        }

        UserStatus userStatus = new UserStatus(request.getUserId());
        userStatusRepository.save(userStatus);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusDto find(UUID userStatusId){
        UserStatus userStatus = getUserStatusEntity(userStatusId);
        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public List<UserStatusDto> findAll(){
        return userStatusRepository.findAll()
                .stream()
                .map(userStatusMapper::toDto)
                .toList();
    }

    @Override
    public UserStatusDto update(UUID userStatusId, UpdateUserStatusRequestDto request){
        UserStatus userStatus = getUserStatusEntity(userStatusId);

        userStatus.updateLastActiveAt();
        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public UserStatusDto updateByUserId(UUID userId){
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with user id " + userId));

        userStatus.updateLastActiveAt();
        userStatusRepository.save(userStatus);

        return userStatusMapper.toDto(userStatus);
    }

    @Override
    public void delete(UUID userStatusId){
        getUserStatusEntity(userStatusId);
        userStatusRepository.deleteById(userStatusId);
    }

    // --------- 내부 메서드 ------------
    private UserStatus getUserStatusEntity(UUID userStatusId){
        return userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("UserStatus not found with id" + userStatusId));
    }
}

