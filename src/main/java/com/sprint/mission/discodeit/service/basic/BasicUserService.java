package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserCreateRequest;
import com.sprint.mission.discodeit.dto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.user.EmailAlreadyExistException;
import com.sprint.mission.discodeit.exception.user.UserNameAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.exception.user.UserStatusNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.Role;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.IOException;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Slf4j
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ReadStatusRepository readStatusRepository;
    private final UserMapper userMapper;
    private final BinaryContentStorage storage;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    @Override
    @Transactional
    public UserDto create(UserCreateRequest request, MultipartFile file) throws IOException {
        log.info("사용자 생성 시작: username={}, email={}", request.username(), request.email());

        userRepository.findByUsername(request.username())
                .ifPresent(u -> {
                    throw new UserNameAlreadyExistsException(request.username());
                });
        userRepository.findByEmail(request.email())
                .ifPresent(u -> {
                    throw new EmailAlreadyExistException(request.email());
                });

        BinaryContent profile = null;
        if (file != null && !file.isEmpty()) {
            profile = new BinaryContent(
                    file.getContentType(),
                    file.getSize(),
                    file.getOriginalFilename()
            );
            profile = binaryContentRepository.save(profile);
            storage.put(profile.getId(), file.getBytes());
            log.debug("프로필 이미지 등록 완료: profileName={}", profile.getFileName());
        }
        // 비밀번호 해시로 저장
        String encodePassword = passwordEncoder.encode(request.password());

        User user = new User(request.username(), request.email(), encodePassword, profile,
                Role.USER);
        userRepository.save(user);
        log.info("사용자 생성 완료: username={}, email={}", request.username(), request.email());

        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(UUID id) {
        User user = userRepository.findById(id).orElseThrow(()
                -> new UserNotFoundException(id));
        UserDto dto = userMapper.toUserDto(user);
        return new UserDto(
                dto.id(),
                dto.username(),
                dto.email(),
                dto.profile(),
                isOnline(id),
                dto.role()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> {
                    UserDto dto = userMapper.toUserDto(user);
                    return new UserDto(
                            dto.id(),
                            dto.username(),
                            dto.email(),
                            dto.profile(),
                            isOnline(user.getId()),
                            dto.role()
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public UserDto update(UUID id, UserUpdateRequest request, MultipartFile file)
            throws IOException {
        log.debug("사용자 수정 시작: id={}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.newUsername() != null && !request.newUsername().equals(user.getUsername())) {
            userRepository.findByUsername(request.newUsername())
                    .ifPresent(u -> {
                        throw new UserNameAlreadyExistsException(request.newUsername());
                    });
        }

        if (request.newEmail() != null && !request.newEmail().equals(user.getEmail())) {
            userRepository.findByEmail(request.newEmail())
                    .ifPresent(u -> {
                        throw new EmailAlreadyExistException(request.newEmail());
                    });
        }

        BinaryContent profile = user.getProfile();
        if (file != null && !file.isEmpty()) {
            profile = new BinaryContent(
                    file.getContentType(),
                    file.getSize(),
                    file.getOriginalFilename()
            );
            profile = binaryContentRepository.save(profile);
            storage.put(profile.getId(), file.getBytes());
            log.debug("프로필 이미지 수정 완료: fileName={}", profile.getFileName());
        }

        user.update(request.newUsername(), profile, request.newEmail(), request.newPassword());
        log.info("사용자 수정 완료: id={}", id);

        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        log.debug("사용자 삭제 시작: id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        // 사용자가 작성한 메시지 삭제
        messageRepository.deleteByAuthorId(id);
        // 사용자의 ReadStatus 삭제
        readStatusRepository.deleteByUserId(id);
        userRepository.deleteById(id);
        log.info("사용자 삭제 완료: id={}", id);
    }

    @Override
    @Transactional
    public UserDto updateRole(UserRoleUpdateRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));
        user.setRole(request.newRole());
        UserDto updatedUser = userMapper.toUserDto(userRepository.save(user));

        sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> {
                    if (principal instanceof DiscodeitUserDetails details) {
                        return details.getUserDto().id().equals(request.userId());
                    }
                    return false;
                })
                .flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
                .forEach(SessionInformation::expireNow);
        return updatedUser;
    }

    private boolean isOnline(UUID userId) {
        return sessionRegistry.getAllPrincipals().stream()
                .filter(principal -> principal instanceof DiscodeitUserDetails details
                        && details.getUserDto().id().equals(userId))
                .flatMap(principal -> sessionRegistry.getAllSessions(principal, false).stream())
                .anyMatch(session -> !session.isExpired());
    }
}
