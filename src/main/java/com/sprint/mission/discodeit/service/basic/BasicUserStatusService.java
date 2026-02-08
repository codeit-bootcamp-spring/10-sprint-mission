package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatus.CreateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UpdateUserStatusRequest;
import com.sprint.mission.discodeit.dto.userstatus.UserStatusResponse;
import com.sprint.mission.discodeit.entity.status.UserStatus;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.status.UserStatusRepository;
import com.sprint.mission.discodeit.service.status.UserStatusService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;
    private final UserRepository UserRepository;

    public BasicUserStatusService(UserStatusRepository userStatusRepository,
                                  UserRepository userRepository) {
        this.userStatusRepository = userStatusRepository;
        this.UserRepository = userRepository;
    }

    public UserStatusResponse create(CreateUserStatusRequest request) {
        //1. User 존재 여부 확인
        if (!userStatusRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException(
                    "User not found with id : " + request.getUserId()
            );

        }

        //2. 중복 UserStatus 확인
        if (userStatusRepository.existsByUserId(request.getUserId())) {
            throw new IllegalArgumentException(
                    "UserStatus already exists for userid : " + request.getUserId()
            );
        }

        // 3. UserStaus 생성 및 저장
        UserStatus userStatus = new UserStatus(
                request.getUserId(),
                request.getStatus(),
                request.getLastActiveAt(),
                Instant.now()
        );

        UserStatus saved = userStatusRepository.save(userStatus);
        return UserStatusResponse.from(saved);
    }

    @Override
    public UserStatusResponse find(UUID id){
        UserStatus userStatus = userStatusRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "유저의 활동이 없습니다. 검색한 아이디 :" + id
                ));

        return UserStatusResponse.from(userStatus);
    }

    @Override
    public List<UserStatusResponse> findAll() {
        return List.of();
    }


    @Override
    public UserStatusResponse update(UpdateUserStatusRequest request){
        // 1. UserStatus 존재 여부 확인
        UserStatus userStatus = userStatusRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException(
                        "유저의 활동이 없습니다. 검색한 아이디 :" + request.getId()
                ));
        // 2. 상태 업데이트
        if(request.getStatus() != null) {
            userStatus.updateStatus(request.getStatus());
        }

        if(request.getLastActiveAt() != null){
            userStatus.updateLastActiveAt(request.getLastActiveAt());
        }

        // 3. 저장 및 반환
        UserStatus updated = userStatusRepository.save(userStatus);
        return UserStatusResponse.from(updated);
    }

    @Override
    public UserStatusResponse updateByUserId(UUID userId, UpdateUserStatusRequest request) {
        // 1. userId로 UserStatus 조회
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "UserStatus not found for userId: " + userId
                ));

        // 2. 상태 업데이트
        if (request.getStatus() != null) {
            userStatus.updateStatus(request.getStatus());
        }

        if (request.getLastActiveAt() != null) {
            userStatus.updateLastActiveAt(request.getLastActiveAt());
        }

        // 3. 저장 및 반환
        UserStatus updated = userStatusRepository.save(userStatus);
        return UserStatusResponse.from(updated);
    }

    @Override
    public void delete(UUID id) {
        // 1. UserStatus 존재 여부 확인
        if (!userStatusRepository.findById(id).isPresent()) {
            throw new NoSuchElementException(
                    "UserStatus not found with id: " + id
            );
        }

        // 2. 삭제
        userStatusRepository.deleteById(id);
    }


}
