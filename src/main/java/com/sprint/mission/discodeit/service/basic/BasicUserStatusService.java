package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;

    @Override
    public UserStatus create(UserStatusCreateRequestDto userStatusCreateRequestDto) {
        if(!userRepository.existsById(userStatusCreateRequestDto.userId())) throw new AssertionError("User not found");
        if(userStatusRepository.findAll()
                .stream()
                .anyMatch(userStatus ->
                        userStatus.getUserID().equals(userStatusCreateRequestDto.userId())))
            throw new AssertionError("UserStatus already exists");

        return userStatusRepository.save(new UserStatus(userStatusCreateRequestDto.userId()));
    }

    @Override
    public UserStatus find(UUID Id) {
        return null;
    }

    @Override
    public UserStatus findAll(UUID Id) {
        return null;
    }

    @Override
    public void update(UserStatusUpdateRequestDto userStatusUpdateRequestDto) {

    }

    @Override
    public void delete(UUID userId) {

    }
}
