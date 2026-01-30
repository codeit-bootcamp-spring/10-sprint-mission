package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDTO;
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
    public UserStatusDTO.Response create(UserStatusDTO.Create request) {

        //유저가 존재하지 않으면 예외
        userRepository.findById(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        //유저 상태가 이미 존재하면 에외
        userStatusRepository.findByUserId(request.userId())
                .ifPresent(status -> {
                    throw new IllegalArgumentException("이미 UserStatus가 존재합니다.");
                });

        UserStatus status = new UserStatus(request.userId());
        userStatusRepository.save(status);

        return UserStatusDTO.Response.of(status);
    }

    @Override
    public UserStatusDTO.Response findById(UUID userStatusId) {
        UserStatus status = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 UserStatus입니다."));
        return UserStatusDTO.Response.of(status);
    }

    @Override
    public List<UserStatusDTO.Response> findAll() {
        return userStatusRepository.findAll().stream()
                .map(UserStatusDTO.Response::of)
                .toList();
    }

    @Override
    public UserStatusDTO.Response update(UserStatusDTO.Update request) {
        UserStatus status = userStatusRepository.findByUserId(request.id())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 UserStatus입니다."));
        status.updateOnline();
        userStatusRepository.save(status);
        return UserStatusDTO.Response.of(status);
    }

    @Override
    public UserStatusDTO.Response updateByUserId(UserStatusDTO.Update request) {
        UserStatus status = userStatusRepository.findByUserId(request.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
        status.updateOnline();
        userStatusRepository.save(status);
        return UserStatusDTO.Response.of(status);
    }

    @Override
    public void delete(UUID userStatusId) {
        UserStatus status = userStatusRepository.findById(userStatusId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 UserStatus입니다."));
        userStatusRepository.delete(status);
    }
}
