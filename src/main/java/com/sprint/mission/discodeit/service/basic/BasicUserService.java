package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.user.CreateUserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UpdateUserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.global.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.global.InvalidInputException;
import com.sprint.mission.discodeit.exception.global.UnchangedValueException;
import com.sprint.mission.discodeit.exception.status.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public UserDto createUser(CreateUserRequestDTO dto, CreateBinaryContentPayloadDTO profileImage) {
        if (userRepository.existsByUsername(dto.username())) {
            log.warn("[USER_CREATE_FAIL_BY_USERNAME] 이미 사용중인 이름으로 유저 생성 실패: username={}", dto.username());
            throw new DuplicateResourceException(
                    ErrorCode.USERNAME_ALREADY_EXISTS, Map.of("username", dto.username())
            );
        }

        if (userRepository.existsByEmail(dto.email())) {
            log.warn("[USER_CREATE_FAIL_BY_EMAIL] 이미 사용중인 이메일로 유저 생성 실패: email={}", dto.email());
            throw new DuplicateResourceException(
                    ErrorCode.EMAIL_ALREADY_EXISTS, Map.of("email", dto.email())
            );
        }

        // userId를 받아오기 위해 우선 객체 생성
        User user = new User(dto.username(), dto.email(), dto.password(), null);

        if (profileImage != null) {
            BinaryContent profile = binaryContentMapper.toEntity(profileImage);

            // 갱신하기
            user.updateProfile(profile);
        }

        UserStatus status = new UserStatus(user, Instant.now());
        user.updateStatus(status);

        User savedUser = userRepository.saveAndFlush(user);

        if (profileImage != null && savedUser.getProfile() != null) {
            binaryContentStorage.put(savedUser.getProfile().getId(), profileImage.bytes());
        }

        log.info("[USER_CREATE_SUCCESS] 유저 생성 성공: userId={}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();

        return userMapper.toDtoList(users);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByUserId(UUID userId) {
        return userMapper.toDto(findUserOrThrow(userId));
    }

    @Override
    public UserDto updateUserInfo(UUID userId, UpdateUserRequestDTO dto, CreateBinaryContentPayloadDTO profileImage) {
        User user = findUserOrThrow(userId);

        if (dto.newUsername() != null) {
            updateUserName(dto, user);
        }
        if (dto.newEmail() != null) {
            updateEmail(dto, user);
        }
        if (dto.newPassword() != null) {
            updatePassword(dto, user);
        }
        if (profileImage != null) {
            // binaryConent는 수정불가 -> 요구사항
            BinaryContent profile = binaryContentMapper.toEntity(profileImage);
            BinaryContent savedProfile = binaryContentRepository.save(profile);
            user.updateProfile(savedProfile);

            userRepository.saveAndFlush(user); // 여기서 cascade로 profile도 저장되고 id 생성

            binaryContentStorage.put(savedProfile.getId(), profileImage.bytes());
        }

        log.info("[USER_UPDATE_SUCCESS] 유저 정보 수정 성공: userId={}", user.getId());
        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateUserStatus(UUID userId, UpdateUserStatusRequestDTO dto) {
        User user = findUserOrThrow(userId);
        UserStatus status = userStatusRepository.findByUser_Id(userId)
                .orElseThrow(() -> new UserStatusNotFoundException(user.getUserStatus().getId()));

        // 갱신
        status.updateLastActiveAt(dto.newLastActiveAt());

        log.info("[USER_STATUS_UPDATE_SUCCESS] 유저 상태 수정 성공: userId={}", user.getId());
        return userMapper.toDto(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = findUserOrThrow(userId);

        BinaryContent profile = user.getProfile();
        if (profile != null && profile.getId() != null) {
            binaryContentRepository.deleteById(profile.getId());
        }

        log.info("[USER_DELETE_SUCCESS] 유저 삭제 성공: userId={}", user.getId());
        userRepository.deleteById(userId);
    }

    // === 여기부터 내부 메서드 ===

    private User findUserOrThrow(UUID userId) {
        if (userId == null) {
            throw new InvalidInputException(
                    ErrorCode.ID_CAN_NOT_BE_NULL, Map.of("userId", "userId is null")
            );
        }

        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("[USER_NOT_FOUND] 유저가 존재하지 않음: userId={}", userId);
                    return new UserNotFoundException(userId);
                });
    }

    private void updateUserName(UpdateUserRequestDTO dto, User user) {
        if (user.getUsername().equals(dto.newUsername())){
            log.warn("[USER_UPDATE_FAIL_BY_USERNAME] 동일한 이름으로 수정 시도로 유저 정보 수정 실패: userId={}, newUsername={}", user.getId(), dto.newUsername());
            throw new UnchangedValueException(
                    ErrorCode.USERNAME_UNCHANGED, Map.of("username", dto.newUsername())
            );
        }

        if (userRepository.existsByUsername(dto.newUsername())) {
            log.warn("[USER_UPDATE_FAIL_BY_USERNAME] 이미 사용중인 이름으로 수정 시도로 유저 정보 수정 실패: userId={}, newUsername={}", user.getId(), dto.newUsername());
            throw new DuplicateResourceException(
                    ErrorCode.USERNAME_ALREADY_EXISTS, Map.of("username", dto.newUsername())
            );
        }

        user.updateUsername(dto.newUsername());     // 객체를 수정하면 JPA가 트랜잭션 커밋되는 순간에 update를 실행해줌
        // 그래서 userRepository.save(user); 코드가 삭제된 것
    }

    private void updateEmail(UpdateUserRequestDTO dto, User user) {
        if (user.getEmail().equals(dto.newEmail())){
            log.warn("[USER_UPDATE_FAIL_BY_EMAIL] 동일한 이메일로 수정 시도로 유저 정보 수정 실패: userId={}, newEmail={}", user.getId(), dto.newEmail());
            throw new UnchangedValueException(
                    ErrorCode.EMAIL_UNCHANGED, Map.of("email", dto.newEmail())
            );
        }

        if (userRepository.existsByEmail(dto.newEmail())) {
            log.warn("[USER_UPDATE_FAIL_BY_EMAIL] 이미 사용중인 이메일로 수정 시도로 유저 정보 수정 실패: userId={}, newEmail={}", user.getId(), dto.newEmail());
            throw new DuplicateResourceException(
                    ErrorCode.EMAIL_ALREADY_EXISTS, Map.of("email", dto.newEmail())
            );
        }

        user.updateEmail(dto.newEmail());
    }

    private void updatePassword(UpdateUserRequestDTO dto, User user) {
        user.updatePassword(dto.newPassword());
    }
}
