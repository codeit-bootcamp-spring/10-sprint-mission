package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.UserStatusCreateRequestDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponseDto;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusUpdateRequestDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.userstatus.UserStatusResponseMapper;
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
    //
    private final UserStatusResponseMapper userStatusResponseMapper;

    @Override
    public UserStatusResponseDto create(UserStatusCreateRequestDto userStatusCreateRequestDto) {
        if(!userRepository.existsById(userStatusCreateRequestDto.userId())) throw new AssertionError("User not found");
        if(userStatusRepository.findAll()
                .stream()
                .anyMatch(userStatus ->
                        userStatus.getUserId().equals(userStatusCreateRequestDto.userId())))
            throw new AssertionError("UserStatus already exists");

        UserStatus userstatus = userStatusRepository.save(new UserStatus(userStatusCreateRequestDto.userId()));
        return userStatusResponseMapper.toDto(userstatus);
    }

    @Override
    public UserStatusResponseDto find(UUID id) {
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new AssertionError("UserStatus not found"));
        return userStatusResponseMapper.toDto(userStatus);
    }

    @Override
    public List<UserStatusResponseDto> findAll(UUID id) {
        return userStatusRepository.findAll().stream()
                .map(userStatusResponseMapper::toDto)
                .toList();
    }

    @Override
    public UserStatusResponseDto update(UserStatusUpdateRequestDto userStatusUpdateRequestDto) {
        //잘못 온 경우 올바르게 돌려주기
        if(userStatusUpdateRequestDto.isUserId()) return updateByUserId(userStatusUpdateRequestDto);

        UserStatus userStatus = userStatusRepository.findById(userStatusUpdateRequestDto.Id())
                .orElseThrow(() -> new AssertionError("UserStatus not found"));

        userStatus.setLastOnlineTime(userStatusUpdateRequestDto.lastOnlineTime());

        userStatusRepository.save(userStatus);
        return userStatusResponseMapper.toDto(userStatus);
    }

    public UserStatusResponseDto updateByUserId(UserStatusUpdateRequestDto userStatusUpdateRequestDto){
        //잘못 온 경우 올바르게 돌려주기
        if (!userStatusUpdateRequestDto.isUserId()) return update(userStatusUpdateRequestDto);

        UserStatus userStatus = userStatusRepository.findByUserId(userStatusUpdateRequestDto.Id())
                        .orElseThrow(() -> new AssertionError("UserStatus not found"));

        userStatus.setLastOnlineTime(userStatusUpdateRequestDto.lastOnlineTime());

        userStatusRepository.save(userStatus);

        return userStatusResponseMapper.toDto(userStatus);
    }

    @Override
    public void delete(UUID id) {
        userStatusRepository.deleteById(id);
    }
}
