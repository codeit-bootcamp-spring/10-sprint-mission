package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.event.ChannelEvents;
import com.sprint.mission.discodeit.event.UserEvents;
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
import org.springframework.cache.annotation.CacheEvict;
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

/**
 * 사용자 관련 비즈니스 로직을 처리하는 기본 서비스 클래스입니다.
 * 사용자 생성, 수정, 삭제 및 권한 관리 기능을 제공합니다.
 */
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
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 새로운 일반 사용자를 생성합니다.
     *
     * @param request 사용자 생성 요청 정보
     * @param profileId 프로필 이미지 ID (선택 사항)
     * @return 생성된 사용자 정보
     */
    @Override
    @Transactional
    public UserDto.Response create(UserDto.CreateRequest request, UUID profileId) {
        User user = createNewUser(request.username(), request.email(), request.password(), profileId, Role.USER);
        
        log.info("[User] 신규 사용자 생성 완료: ID={}, Username={}, Email={}", user.getId(), user.getUsername(), user.getEmail());
        
        eventPublisher.publishEvent(new UserEvents.Updated(user.getId()));
        
        return toDto(user);
    }

    /**
     * 관리자 계정을 생성합니다.
     *
     * @param username 관리자 ID
     * @param email 관리자 이메일
     * @param rawPassword 비밀번호 (평문)
     */
    @Override
    @Transactional
    public void createAdmin(String username, String email, String rawPassword) {
        User admin = createNewUser(username, email, rawPassword, null, Role.ADMIN);
        log.info("[User] 관리자 계정 생성 완료: Username={}", username);
        eventPublisher.publishEvent(new UserEvents.Updated(admin.getId()));
    }

    /**
     * 사용자의 권한(Role)을 업데이트합니다.
     *
     * @param userId 업데이트할 사용자 ID
     * @param newRole 새로운 권한
     * @return 업데이트된 사용자 정보
     */
    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto.Response updateRole(UUID userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        Role oldRole = user.getRole();
        user.updateRole(newRole);

        log.info("[User] 사용자 권한 변경: ID={}, Role={} -> {}", userId, oldRole, newRole);

        eventPublisher.publishEvent(new UserEvents.RoleUpdated(user.getId(), oldRole, newRole));
        eventPublisher.publishEvent(new UserEvents.Updated(user.getId()));

        return toDto(user);
    }

    /**
     * 사용자를 ID로 조회합니다.
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 상세 정보
     */
    @Override
    public UserDto.Response find(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
    }

    /**
     * 전체 사용자 목록을 조회합니다. 결과는 캐시됩니다.
     *
     * @return 전체 사용자 목록
     */
    @Override
    @Cacheable(value = "usersCache")
    public List<UserDto.Response> findAll() {
        List<UserDto.Response> users = userRepository.findAll().stream()
                .map(this::toDto)
                .toList();

        log.debug("[User] 전체 사용자 목록 조회: Count={}", users.size());
        return users;
    }

    /**
     * 사용자 정보를 수정합니다.
     *
     * @param userId 수정할 사용자 ID
     * @param request 수정 요청 정보
     * @param newProfileId 새로운 프로필 이미지 ID (선택 사항)
     * @return 수정된 사용자 정보
     */
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

        try {
            User updatedUser = userRepository.saveAndFlush(user);
            log.info("[User] 사용자 정보 수정 완료: ID={}, Username={}", userId, updatedUser.getUsername());
            
            eventPublisher.publishEvent(new UserEvents.Updated(userId));
            
            return toDto(updatedUser);
        } catch (DataIntegrityViolationException e) {
            throw DatabaseConflictException.withUser(request.newUsername(), request.newEmail(), e);
        }
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN') or principal.userDto.id == #userId")
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));

        // 1. 삭제 전, 이 사용자가 참여 중인 모든 비공개 채널의 '다른' 참여자들을 수집
        // (사용자가 삭제되면 비공개 채널이 사라지거나 멤버 목록이 변하므로 다른 참여자들의 캐시도 비워야 함)
        List<UUID> myChannelIds = readStatusRepository.findChannelIdsByUserId(userId);
        Set<UUID> affectedUserIds = new HashSet<>();
        if (!myChannelIds.isEmpty()) {
            affectedUserIds = readStatusRepository.findAllByChannelIdsWithUser(myChannelIds).stream()
                    .map(rs -> rs.getUser().getId())
                    .filter(id -> !id.equals(userId))
                    .collect(Collectors.toSet());
        }

        // 2. 유저 삭제 (ReadStatus 등 CASCADE 삭제됨)
        userRepository.delete(user);

        // 3. 참여자가 없는 채널 정리
        if (!myChannelIds.isEmpty()) {
            channelRepository.deleteEmptyOrLonelyChannels(myChannelIds);
        }

        log.info("[User] 사용자 삭제 완료: ID={}, Username={}", userId, user.getUsername());
        
        // 4. 이벤트 발행: 본인 및 영향받은 다른 참여자들의 캐시 무효화
        eventPublisher.publishEvent(new UserEvents.Updated(userId)); // usersCache 비우기
        affectedUserIds.forEach(id -> eventPublisher.publishEvent(new ChannelEvents.AccessChanged(id)));
    }

    // --- Private Helpers ---

    private User createNewUser(String username, String email, String rawPassword, UUID profileId, Role role) {
        validateUserUniqueness(null, username, email);

        String encodedPassword = passwordEncoder.encode(rawPassword);
        BinaryContent profile = (profileId != null) 
                ? binaryContentRepository.findById(profileId).orElseThrow(() -> BinaryContentNotFoundException.withId(profileId))
                : null;

        User user = new User(username, email, encodedPassword, profile, role);
        user.setStatus(new UserStatus(user, Instant.now()));

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
        if (user.getStatus() == null) {
            log.error("[Data Integrity] 유저 상태 정보 누락: ID={}", user.getId());
            throw InternalServerException.dataIntegrity("유저(ID: %s)의 상태 정보가 누락되었습니다.", user.getId());
        }
        return userMapper.toResponse(user);
    }
}

