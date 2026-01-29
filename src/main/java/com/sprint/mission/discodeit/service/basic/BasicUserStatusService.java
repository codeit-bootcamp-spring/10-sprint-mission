package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
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
    public UserStatus find(UUID id) {
        return userStatusRepository.findById(id)
                .orElseThrow(() -> new AssertionError("UserStatus not found"));
    }

    @Override
    public List<UserStatus> findAll(UUID id) {
        return userStatusRepository.findAll();
    }

    @Override
    public UserStatus update(UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        //잘못 온 경우 올바르게 돌려주기
        if(userStatusUpdateRequestDto.isUserId()) return updateByUserId(userStatusUpdateRequestDto);

        UserStatus userStatus = userStatusRepository.findById(userStatusUpdateRequestDto.Id())
                .orElseThrow(() -> new AssertionError("UserStatus not found"));

        userStatus.setLastOnlineTime(userStatusUpdateRequestDto.lastOnlineTime());

        return userStatusRepository.save(userStatus);
    }

    public UserStatus updateByUserId(UserStatusUpdateRequestDto userStatusUpdateRequestDto){
        //잘못 온 경우 올바르게 돌려주기
        if (!userStatusUpdateRequestDto.isUserId()) return update(userStatusUpdateRequestDto);

        UserStatus userStatus = userStatusRepository.findByUserId(userStatusUpdateRequestDto.Id())
                        .orElseThrow(() -> new AssertionError("UserStatus not found"));

        userStatus.setLastOnlineTime(userStatusUpdateRequestDto.lastOnlineTime());

        return userStatusRepository.save(userStatus);
    }

    @Override
    public void delete(UUID userId) {

    }
}
