package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.event.ChannelEvents;
import com.sprint.mission.discodeit.event.UserEvents;
import com.sprint.mission.discodeit.exception.etc.DatabaseConflictException;
import com.sprint.mission.discodeit.exception.etc.InternalServerException;
import com.sprint.mission.discodeit.exception.user.DuplicationUserException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public UserDto.Response create(UserDto.CreateRequest request, UUID profileId) {
        User user = createNewUser(request.username(), request.email(), request.password(), profileId, Role.USER);
        log.info("[User] 신규 사용자 생성 완료: ID={}, Username={}, Email={}", user.getId(), user.getUsername(), user.getEmail());

        UserDto.Response createdUser = toDto(user);
        eventPublisher.publishEvent(new UserEvents.Created(createdUser));
        return createdUser;
    }

    @Override
    @Transactional
    public void createAdmin(String username, String email, String rawPassword) {
        User admin = createNewUser(username, email, rawPassword, null, Role.ADMIN);
        log.info("[User] 관리자 계정 생성 완료: Username={}", username);
        eventPublisher.publishEvent(new UserEvents.Created(toDto(admin)));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto.Response updateRole(UUID userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        Role oldRole = user.getRole();
        user.updateRole(newRole);

        log.info("[User] 사용자 권한 변경: ID={}, Role={} -> {}", userId, oldRole, newRole);

        UserDto.Response updatedUser = toDto(user);
        eventPublisher.publishEvent(new UserEvents.RoleUpdated(user.getId(), oldRole, newRole));
        eventPublisher.publishEvent(new UserEvents.Updated(updatedUser));

        return updatedUser;
    }

    @Override
    public UserDto.Response find(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
    }

    @Override
    @Cacheable(value = "usersCache")
    public List<UserDto.Response> findAll() {
        List<UserDto.Response> users = userRepository.findAll().stream()
                .map(this::toDto)
                .toList();

        log.debug("[User] 전체 사용자 목록 조회: Count={}", users.size());
        return users;
    }
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or principal.userDto.id == #userId")
    public UserDto.Response update(UUID userId, UserDto.UpdateRequest request, UUID newProfileId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        validateUserUniqueness(user, request.newUsername(), request.newEmail());

        String encodedPassword = (request.newPassword() != null) 
                ? passwordEncoder.encode(request.newPassword()) 
                : null;

        BinaryContent newProfile = (newProfileId == null) ? null :
                binaryContentRepository.findById(newProfileId)
                        .orElseThrow(() -> BinaryContentNotFoundException.withId(newProfileId));

        user.update(request.newUsername(), request.newEmail(), encodedPassword, newProfile);
        
        log.info("[User] 사용자 정보 수정 완료: ID={}, Username={}", userId, user.getUsername());


        UserDto.Response updatedUser = toDto(user);
        eventPublisher.publishEvent(new UserEvents.Updated(updatedUser));

        return updatedUser;
    }


    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or principal.userDto.id == #userId")
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        UserDto.Response deletedUser = toDto(user);

        // 1. 삭제될 비공개 채널과 알림 대상자(상대방)를 한 번에 조회
        List<ChannelEvents.Deleted> deletionEvents = readStatusRepository.findAffectedPrivateChannelInfo(userId).stream()
                .map(row -> new ChannelEvents.Deleted(
                        (UUID) row[0], 
                        ChannelType.PRIVATE,
                        List.of((UUID) row[1])
                ))
                .toList();

        // 2. 이 유저가 참여한 전체 채널 목록 확보 (물리적 삭제 대상)
        List<UUID> myChannelIds = readStatusRepository.findChannelIdsByUserId(userId);

        // 3. 유저 본인 삭제 (CASCADE 삭제 진행)
        userRepository.delete(user);
        userRepository.flush();

        // 4. 참여자가 0명 또는 1명만 남은 방들을 물리적으로 제거
        if (!myChannelIds.isEmpty()) {
            channelRepository.deleteEmptyOrLonelyChannels(myChannelIds);
        }

        log.info("[User] 사용자 삭제 완료: ID={}, DeletedPrivateChannels={}", userId, deletionEvents.size());
        
        // 5. 정합성 및 실시간성 보장을 위한 이벤트 발행
        eventPublisher.publishEvent(new UserEvents.Deleted(deletedUser)); // 전체 유저 목록 갱신 및 본인 캐시 무효화
        deletionEvents.forEach(eventPublisher::publishEvent); // 상대방들에게 채널 삭제 실시간 알림 및 캐시 무효화
    }

    private User createNewUser(String username, String email, String rawPassword, UUID profileId, Role role) {
        validateUserUniqueness(null, username, email);

        String encodedPassword = passwordEncoder.encode(rawPassword);
        BinaryContent profile = (profileId != null) 
                ? binaryContentRepository.findById(profileId).orElseThrow(() -> BinaryContentNotFoundException.withId(profileId))
                : null;

        User user = new User(username, email, encodedPassword, profile, role);

        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException e) {
            throw DatabaseConflictException.withUser(username, email, e);
        }
    }

    private void validateUserUniqueness(User currentUser, String newUsername, String newEmail) {
        if (newEmail != null && userRepository.existsByEmail(newEmail) && 
            (currentUser == null || !currentUser.getEmail().equals(newEmail))) {
            throw DuplicationUserException.withEmail(newEmail);
        }

        if (newUsername != null && userRepository.existsByUsername(newUsername) && 
            (currentUser == null || !currentUser.getUsername().equals(newUsername))) {
            throw DuplicationUserException.withUserName(newUsername);
        }
    }

    private UserDto.Response toDto(User user) {
        return userMapper.toResponse(user);
    }
}
