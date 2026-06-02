package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.etc.DatabaseConflictException;
import com.sprint.mission.discodeit.exception.etc.InternalServerException;
import com.sprint.mission.discodeit.exception.user.DuplicationUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ChannelRepository channelRepository;
    private final AuthService authService;

    @Override
    @Transactional
    public UserDto.Response create(UserDto.CreateRequest request, UUID profileId) {
        User user = createUser(
            request.username(),
            request.email(),
            request.password(),
            profileId,
            Role.USER
        );
        return toDto(user);
    }

    @Override
    @Transactional
    public void createAdmin(String username, String email, String rawPassword) {
        createUser(
            username,
            email,
            rawPassword,
    null,
            Role.ADMIN
        );
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserDto.Response updateRole(UUID userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        user.updateRole(newRole);

        authService.expireUserSessions(userId);

        return toDto(user);
    }

    @Override
    public UserDto.Response find(UUID userId) {
        UserDto.Response response = userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        log.debug("[User Found] ID: {}, Username: {}", userId, response.username());

        return response;
    }

    @Override
    public List<UserDto.Response> findAll() {
        List<UserDto.Response> users = userRepository.findAll().stream()
                .map(this::toDto)
                .toList();

        log.debug("[Users Fetched] Total Count: {}", users.size());

        return users;
    }

    @PreAuthorize("hasRole('ADMIN') or principal.userDto.id == #userId")
    @Override
    @Transactional
    public UserDto.Response update(UUID userId, UserDto.UpdateRequest request, UUID newProfileId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        validateUser(user, request.newUsername(), request.newEmail());

        String oldUsername = user.getUsername();
        String oldEmail = user.getEmail();


        String encodedPassword = (request.newPassword() != null) // null일 때 암호화해서 null이 아니게 되는 것을 방지
                ? passwordEncoder.encode(request.newPassword())
                : null;

        BinaryContent newProfile = (newProfileId == null) ? null :
                binaryContentRepository.findById(newProfileId)
                        .orElseThrow(() -> BinaryContentNotFoundException.withId(newProfileId));

        // password 업데이트는 엔티티 내 메서드를 따로 만들어서 책임 분리로 개선할 여지가 있음
        user.update(request.newUsername(), request.newEmail(), encodedPassword, newProfile);

        try { // 레이스 컨디션
            User updatedUser = userRepository.saveAndFlush(user);
            log.info("[User Updated] ID: {}, Old Username: {} -> New Username: {}, Old Email: {} -> New Email: {}",
                    userId, oldUsername, updatedUser.getUsername(), oldEmail, updatedUser.getEmail());
            return toDto(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw DatabaseConflictException.withUser(request.newUsername(), request.newEmail(), e);
        }
    }

    @PreAuthorize("hasRole('ADMIN') or principal.userDto.id == #userId")
    @Override
    @Transactional
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        List<UUID> myChannelIds = readStatusRepository.findChannelIdsByUserId(userId);

        userRepository.delete(user);

        if (!myChannelIds.isEmpty()) {
            channelRepository.deleteEmptyOrLonelyChannels(myChannelIds);
        }

        log.info("[User Deleted] ID: {}, Username: {}",
                userId, user.getUsername());
    }

    // validation
    private void validateUser(User user, String username, String email) {
        // 유저가 null이면 create, 있으면 update
        // 유저가 null이 아닌 경우만(update면) 값이 변경되었는지 equal로 체크해서 변경되지 않았으면 스킵
        if (email != null
                && userRepository.existsByEmail(email)
                && (user == null || !user.getEmail().equals(email))) {
            throw DuplicationUserException.withEmail(email);
        }

        if (username != null
                && userRepository.existsByUsername(username)
                && (user == null || !user.getUsername().equals(username))) {
            throw DuplicationUserException.withUserName(username);
        }
    }

    // Helper
    private UserDto.Response toDto(User user) {
        UserStatus userStatus = Optional.ofNullable(user.getStatus())
                .orElseThrow(() -> InternalServerException.dataIntegrity(
                        "유저(ID: %s)의 상태 정보가 누락되었습니다.", user.getId()
                ));

        return userMapper.toResponse(user);
    }

    private User createUser(
        String username,
        String email,
        String rawPassword,
        UUID profileId,
        Role role
    ) {
        validateUser(null, username, email);

        String encodedPassword = passwordEncoder.encode(rawPassword);

        BinaryContent profile = profileId == null
            ? null
            : binaryContentRepository.findById(profileId)
              .orElseThrow(() -> BinaryContentNotFoundException.withId(profileId));

        User user = new User(username, email, encodedPassword, profile, role);
        user.setStatus(new UserStatus(user, Instant.now()));

        try {
            User savedUser = userRepository.saveAndFlush(user);
            log.info("[User Created] ID: {}, Username: {}, Email: {}, Role: {}",
                savedUser.getId(), username, email, role);
            return savedUser;
        } catch (DataIntegrityViolationException e) {
            throw DatabaseConflictException.withUser(username, email, e);
        }
    }
}
