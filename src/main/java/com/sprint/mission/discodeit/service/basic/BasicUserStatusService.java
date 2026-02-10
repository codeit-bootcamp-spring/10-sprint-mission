package com.sprint.mission.discodeit.service.basic;

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
    public UserStatusDto.Response create(UserStatusDto.Create request) {

        //유저가 존재하지 않으면 예외
        userRepository.findById(request.userId())
                .orElseThrow(() -> new NoSuchElementException("유저가 존재하지 않습니다."));

        //유저 상태가 이미 존재하면 에외
        userStatusRepository.findByUserId(request.userId())
                .ifPresent(status -> {
                    throw new IllegalArgumentException("이미 유저 상태가 존재합니다.");
                });

        UserStatus status = new UserStatus(request.userId());
        userStatusRepository.save(status);

        return UserStatusDto.Response.of(status);
    }

    @Override
    public UserStatusDto.Response findById(UUID userStatusId) {
        UserStatus status = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("유저 상태가 존재하지 않습니다."));
        return UserStatusDto.Response.of(status);
    }

    @Override
    public List<UserStatusDto.Response> findAll() {
        return userStatusRepository.findAll().stream()
                .map(UserStatusDto.Response::of)
                .toList();
    }

    @Override
    public UserStatusDto.Response update(UserStatusDto.Update request) {
        UserStatus status = userStatusRepository.findByUserId(request.id())
                .orElseThrow(() -> new NoSuchElementException("유저 상태가 존재하지 않습니다."));
        status.updateOnline();
        userStatusRepository.save(status);
        return UserStatusDto.Response.of(status);
    }

    @Override
    public UserStatusDto.Response updateByUserId(UUID userId) {
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("유저 상태가 존재하지 않습니다."));
        status.updateOnline();
        userStatusRepository.save(status);
        return UserStatusDto.Response.of(status);
    }

    @Override
    public void delete(UUID userStatusId) {
        UserStatus status = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new NoSuchElementException("유저 상태가 존재하지 않습니다."));
        userStatusRepository.delete(status);
    }
}
