package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.user.CreateUserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.global.DuplicateResourceException;
import com.sprint.mission.discodeit.exception.global.InvalidInputException;
import com.sprint.mission.discodeit.exception.global.UnchangedValueException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.UserOnlineStatusChecker;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;

    private final UserMapper userMapper;
    private final BinaryContentMapper binaryContentMapper;

    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;
    private final UserOnlineStatusChecker checker;

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

        String encodedPassword = passwordEncoder.encode(dto.password());

        // userId를 받아오기 위해 우선 객체 생성
        User user = new User(dto.username(), dto.email(), encodedPassword, null);

        if (profileImage != null) {
            BinaryContent profile = binaryContentMapper.toEntity(profileImage);

            // 갱신하기
            user.updateProfile(profile);
        }

        User savedUser = userRepository.saveAndFlush(user);

        if (profileImage != null && savedUser.getProfile() != null) {
            binaryContentStorage.put(savedUser.getProfile().getId(), profileImage.bytes());
        }

        log.info("[USER_CREATE_SUCCESS] 유저 생성 성공: userId={}", savedUser.getId());
        return userMapper.toDto(savedUser, checker.isOnline(savedUser.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();

        return userMapper.toDtoList(users, checker::isOnline);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByUserId(UUID userId) {
        return userMapper.toDto(findUserOrThrow(userId), checker.isOnline(userId));
    }

    @PreAuthorize("#userId == authentication.principal.id")
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
        return userMapper.toDto(user, checker.isOnline(user.getId()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserDto updateRole(UserRoleUpdateRequest dto) {
        User user = findUserOrThrow(dto.userId());

        user.updateRole(dto.newRole());
        // 권한 업데이트 성공 후 로그인 세션 만료시키기
        expiredUserSessions(user.getId());

        log.info("[USER_ROLE_UPDATE_SUCCESS] 유저 역할 수정 성공: userId={}, role={}", user.getId(), user.getRole());
        return userMapper.toDto(user, checker.isOnline(user.getId()));
    }

    @PreAuthorize("#userId == authentication.principal.id")
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

    // === DiscodeitUserDetailsService에서 사용할 메서드 ===
    @Override
    public User findByUsername(String username) {
        if (username.isBlank()) {
            throw new InvalidInputException(
                    ErrorCode.USERNAME_CAN_NOT_BE_BLANK, Map.of("username", "username is blank")
            );
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("[USER_NOT_FOUND] 유저가 존재하지 않음: username={}", username);
                    return new UserNotFoundException(username);
                });
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
        String encodedPassword = passwordEncoder.encode(dto.newPassword());
        user.updatePassword(encodedPassword);
    }

    private void expiredUserSessions(UUID userId) {
        sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof DiscodeitUserDetails)
                .map(principal -> (DiscodeitUserDetails) principal)
                .filter(userDetails -> userDetails.getUserDto().id().equals(userId))
                .forEach(userDetails ->
                        sessionRegistry.getAllSessions(userDetails, false)
                                .forEach(SessionInformation::expireNow));
    }
}
