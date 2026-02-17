package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.userstatusdto.UserStateRequestDTO;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStateResponseDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.mapper.UserStatusDTOMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {
    private final UserStatusDTOMapper userStatusDTOMapper;
    private final UserStatusRepository userStatusRepository;
    private final UserRepository userRepository;


    @Override
    public UserStateResponseDTO create(UserStateRequestDTO req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다.");

        if(userRepository.findById(req.userID()).isEmpty()){
            throw new IllegalStateException("해당 유저는 존재하지 않습니다.");
        }

        if((userRepository.findById(req.userID()).isPresent()) && (userStatusRepository.findById(req.id()).isPresent())){
            throw new IllegalStateException("해당 유저의 ReadStatus 객체가 이미 존재합니다.");
        }
        UserStatus userStatus = userStatusDTOMapper.userStatusRequestToUS(req);
        UserStatus saved = userStatusRepository.save(userStatus);

        return userStatusDTOMapper.userStatusToResponse(saved);
    }

    @Override
    public UserStatus find(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID입니다!");
        return userStatusRepository.findById(id).orElseThrow(() -> new NoSuchElementException("해당 User Status는 존재하지 않습니다!"));
    }

    @Override
    public List<UserStateResponseDTO> findAll() {

        return userStatusRepository.findAll()
                .stream()
                .map(userStatusDTOMapper::userStatusToResponse
                ).toList();
    }

    @Override
    public UserStateResponseDTO update(UserStateRequestDTO req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다.");
        userRepository.findById(req.userID()).orElseThrow(()->new IllegalStateException("해당 유저가 존재하지 않습니다!"));
        UserStatus userStatus = userStatusRepository.findById(req.id()).orElseThrow(() -> new NoSuchElementException("해당 User Status는 존재하지 않습니다!"));
        userStatus.update(Instant.now());
        UserStatus saved = userStatusRepository.save(userStatus);

        return userStatusDTOMapper.userStatusToResponse(saved);

    }

    @Override
    public UserStateResponseDTO updateByUserId(UUID userId) {
        Objects.requireNonNull(userId, "유효하지 않은 ID 입니다!");

        if (userRepository.findById(userId).stream().noneMatch(u -> userId.equals(u.getId()))) {
            throw new IllegalStateException("존재하지 않는 유저 ID 입니다!");
        }

        UserStatus userStatus = userStatusRepository.findAll()
                .stream()
                .filter(us -> userId.equals(us.getUserID()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("유저의 Read Status가 존재하지 않습니다!"));

        userStatus.update(Instant.now());
        UserStatus saved = userStatusRepository.save(userStatus);

        return userStatusDTOMapper.userStatusToResponse(saved);
    }

    @Override
    public UserStateResponseDTO activateUserOnline(UUID userId) {
        Objects.requireNonNull(userId, "유효하지 않은 User ID 입니다!");

        UserStatus userStatus = find(userId);
        userStatus.setLastActiveAt();
        UserStatus saved = userStatusRepository.save(userStatus);

        return userStatusDTOMapper.userStatusToResponse(saved);
    }

    @Override
    public void delete(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID 입니다!");
        userStatusRepository.deleteById(id);
    }
}
