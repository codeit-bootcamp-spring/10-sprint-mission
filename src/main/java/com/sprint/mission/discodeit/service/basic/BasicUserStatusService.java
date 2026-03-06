package com.sprint.mission.discodeit.service.basic;

//import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusRequestDTO;

import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusDto;
import com.sprint.mission.discodeit.dto.userstatusdto.UserStatusUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.entity.mapper.UserStatusDTOMapper;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserStatusService implements UserStatusService {

    private final UserStatusRepository userStatusRepository;

// UserStatus의 생성은 User 생성과 동시에 일어남. 그리고 UserStatusController의 부재로 인하여 생성하는 로직은 주석처리 되었음.

//    @Override
//    @Transactional
//    public UserStatusDto create(UserStatusDto req) {
//        Objects.requireNonNull(req, "유효하지 않은 요청입니다.");
//        Objects.requireNonNull(req.userId(), "유효하지 않은 유저 ID 입니다.");
//
//        User user = userRepository.findById(req.userId())
//            .orElseThrow(() -> new NoSuchElementException("유저를 찾을 수 없습니다."));
//        if (userStatusRepository.existsByUserId(req.userId())) {
//            throw new IllegalStateException("이미 UserStatus가 존재합니다.");
//        }
//
//        try {
//            UserStatus userStatus = new UserStatus(user);
//            UserStatus saved = userStatusRepository.save(userStatus);
//            return UserStatusDTOMapper.userStatusToResponse(saved);
//        } catch (DataIntegrityViolationException e) {
//            throw new IllegalStateException("중복된 UserStatus 생성이 감지되었습니다!");
//        }
//
//
//    }

    @Override
    public UserStatus find(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID입니다!");
        return userStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 UserStatus는 존재하지 않습니다!"));
    }

    @Transactional
    @Override
    public List<UserStatusDto> findAll() {
        return userStatusRepository.findAll()
            .stream()
            .map(UserStatusDTOMapper::userStatusToResponse
            ).toList();
    }

//    @Transactional
//    @Override
//    public UserStatusDto update(UserStatusUpdateRequestDTO req) {
//        Objects.requireNonNull(req, "유효하지 않은 요청입니다.");
//        Objects.requireNonNull(req.newLastActiveAt(), "유효하지 않은 시간입니다.");
//
//        userRepository.findById(req.userId())
//            .orElseThrow(() -> new IllegalStateException("해당 유저가 존재하지 않습니다!"));
//        UserStatus userStatus = userStatusRepository.findById(req.userId())
//            .orElseThrow(() -> new NoSuchElementException("해당 User Status는 존재하지 않습니다!"));
//        userStatus.update(Instant.now());
//        UserStatus saved = userStatusRepository.save(userStatus);
//
//        return UserStatusDTOMapper.userStatusToResponse(saved);
//
//    }
//
//    @Transactional
//    @Override
//    public UserStatusDto updateByUserId(UUID userId) {
//        Objects.requireNonNull(userId, "유효하지 않은 ID 입니다!");
//
//        if (userRepository.findById(userId).stream().noneMatch(u -> userId.equals(u.getId()))) {
//            throw new IllegalStateException("존재하지 않는 유저 ID 입니다!");
//        }
//
//        UserStatus userStatus = userStatusRepository.findAll()
//            .stream()
//            .filter(us -> userId.equals(us.getUser().getId()))
//            .findFirst()
//            .orElseThrow(() -> new NoSuchElementException("유저의 Read Status가 존재하지 않습니다!"));
//
//        userStatus.update(Instant.now());
//        UserStatus saved = userStatusRepository.save(userStatus);
//
//        return UserStatusDTOMapper.userStatusToResponse(saved);
//    }

    @Override
    @Transactional
    public UserStatusDto activateUserOnline(UUID userId, UserStatusUpdateRequestDTO req) {
        Objects.requireNonNull(userId, "유효하지 않은 userStatus 식별자입니다!");
        Objects.requireNonNull(req, "유효하지 않은 요청입니다!");

        // 유저 ID를 통해 해당 유저의 UserStatus가 있는지 검증
        Optional<UserStatus> optUserStatus = userStatusRepository.findByUserId(userId);
        if (optUserStatus.isEmpty()) {
            throw new NoSuchElementException("해당 UserStatus 찾을 수 없음!");
        }
        UserStatus userStatus = optUserStatus.get();

        // 엔티티에 직접 접근해서 업데이트 후 영속성 컨텍스트에서 변경 감지 -> DirtyChecking으로 update 쿼리문이 나감
        userStatus.update(req.newLastActiveAt());

        return UserStatusDTOMapper.userStatusToResponse(userStatus);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Objects.requireNonNull(id, "유효하지 않은 ID 입니다!");
        userStatusRepository.findById(id)
            .orElseThrow(() -> new NoSuchElementException("해당 UserStatus를 찾을 수 없음."));
        userStatusRepository.deleteById(id);
    }

    @Override
    public boolean isUserOnline(UUID userId) {
        Objects.requireNonNull(userId, "유효하지 않은 ID 입니다!");
        UserStatus userStatus = userStatusRepository.findByUserId(userId)
            .orElseThrow(() -> new NoSuchElementException("UserStatus를 찾을 수 없음!"));
        return userStatus.isOnline();
    }
}
