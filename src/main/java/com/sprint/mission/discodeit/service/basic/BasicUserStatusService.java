package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserStatusDto;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import com.sprint.mission.discodeit.service.helper.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final EntityFinder entityFinder;

    @Override
    public UserStatusDto.UserStatusResponse create(UserStatusDto.UserStatusRequest request) {
        Objects.requireNonNull(request.userId(), "유저 ID가 유효하지 않습니다.");

        // 유저 존재 검사
        if (userRepository.findAll().stream().noneMatch(user -> user.getId().equals(request.userId())))
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다.");
        // 유저에 해당하는 Status 존재 검사
        if (userStatusRepository.findAll().stream().anyMatch(userStatus -> userStatus.getUserId().equals(request.userId())))
            throw new IllegalArgumentException("상태 객체가 이미 존재합니다.");

        UserStatus userStatus = new UserStatus(request.userId());
        userStatusRepository.save(userStatus);

        return UserStatusDto.UserStatusResponse.from(userStatus);
    }

    @Override
    public UserStatusDto.UserStatusResponse findById(UUID userStatusId){
        Objects.requireNonNull(userStatusId, "상태 ID가 유효하지 않습니다.");
        return UserStatusDto.UserStatusResponse.from(entityFinder.getUserStatus(userStatusId));
    }

    @Override
    public List<UserStatusDto.UserStatusResponse> findAll(){
        return userStatusRepository.findAll().stream().map(UserStatusDto.UserStatusResponse::from).toList();
    }

    @Override
    public UserStatusDto.UserStatusResponse update(UUID userStatusId, UserStatus.Status status){
        UserStatus userStatus = entityFinder.getUserStatus(userStatusId);
        Optional.ofNullable(status).ifPresent(userStatus::updateStatus);
        userStatusRepository.save(userStatus);
        return UserStatusDto.UserStatusResponse.from(userStatus);
    }

    @Override
    public void delete(UUID userStatusId){
        entityFinder.getUserStatus(userStatusId);
        userStatusRepository.delete(userStatusId);

    }

}
