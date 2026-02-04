package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ReadStatusDto;
import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
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
    public UserStatusDto.Response create(UserStatusDto.CreateRequest request) {
        // 관련된 유저 존재 X
        userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 유저가 존재하지 않습니다."));

        // 같은 유저와 관련된 객체가 이미 존재
        if (userStatusRepository.existsByUserId(request.userId())) {
            throw new IllegalStateException("해당 ID의 상태가 이미 존재합니다.");
        }

        UserStatus userStatus = new UserStatus(
                request.userId()
        );

        return convertToResponse(userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusDto.Response find(UUID id) {
        return userStatusRepository.findById(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 상태가 존재하지 않습니다."));
    }

    @Override
    public List<UserStatusDto.Response> findAll() {
        return userStatusRepository.findAll().stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public UserStatusDto.Response update(UserStatusDto.UpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findById(request.id())
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 상태가 존재하지 않습니다."));

        userStatus.updateActiveTime();

        return convertToResponse(userStatusRepository.save(userStatus));
    }

    @Override
    public UserStatusDto.Response updateByUserId(UserStatusDto.UpdateRequest request) {
        UserStatus userStatus = userStatusRepository.findByUserId(request.userId())
                .orElseThrow(() -> new NoSuchElementException("해당 유저의 상태 정보가 존재하지 않습니다."));

        userStatus.updateActiveTime();

        return convertToResponse(userStatusRepository.save(userStatus));
    }

    @Override
    public void delete(UUID id) {
        if (!userStatusRepository.existsById(id)) {
            throw new NoSuchElementException("해당 ID의 상태가 존재하지 않습니다.");
        }
        userStatusRepository.deleteById(id);

    }

    private UserStatusDto.Response convertToResponse(UserStatus userStatus) {
        return new UserStatusDto.Response(
                userStatus.getId(),
                userStatus.getUserId(),
                userStatus.getUpdatedAt(),
                userStatus.isOnline()
        );
    }
}
