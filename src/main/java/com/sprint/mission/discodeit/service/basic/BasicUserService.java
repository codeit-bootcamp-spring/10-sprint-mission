package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.CreateUserRequestDto;
import com.sprint.mission.discodeit.dto.UpdateUserRequestDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentStorage binaryContentStorage;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(CreateUserRequestDto request) {
        log.debug("사용자 가입 요청: username={}, email={}", request.username(), request.email());
        validateDuplicateUser(request.username(), request.email());

        User user = new User(request.username(), request.email(), request.password());
        uploadProfileImage(user, request.profileImage());

        userRepository.save(user);

        UserStatus status = new UserStatus(user);
        userStatusRepository.save(status);

        log.info("사용자 가입 성공: userId={}", user.getId());
        return userMapper.toDto(user);
    }

    @Override
    public Optional<UserDto> findById(UUID userId) {
        return userRepository.findById(userId)
                .map(userMapper::toDto);
    }

    @Override
    public UserDto find(UUID userId) {
        User user = getUserEntity(userId);
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto update(UUID userId, UpdateUserRequestDto request) {
        log.debug("사용자 정보 수정 요청: userId={}", userId);
        User user = getUserEntity(userId);

        user.update(request.newUsername(), request.newEmail(), request.newPassword());

        if (request.newProfileImage() != null) {
            uploadProfileImage(user, request.newProfileImage());
        }

        log.info("사용자 정보 수정 완료: userId={}", user.getId());
        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void delete(UUID userId) {
        log.warn("사용자 삭제 요청: userId={}", userId);
        User user = getUserEntity(userId);
        userRepository.delete(user);
        log.info("사용자 삭제 완료: userId={}", userId);
    }

    // --------- 내부 메서드 구현 ---------
    // 유저 엔티티 조회
    private User getUserEntity(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    // 프로필 이미지 업로드 및 유저 정보 업데이트
    private void uploadProfileImage(User user, BinaryContentDto contentDto) {
        if (contentDto == null || contentDto.bytes() == null) return;

        BinaryContent content = new BinaryContent(
                contentDto.fileName(),
                contentDto.contentType(),
                contentDto.size()
        );
        content = binaryContentRepository.save(content);
        binaryContentStorage.put(content.getId(), contentDto.bytes());

        user.updateProfileImageId(content);
    }

    // 중복 가입 검증
    private void validateDuplicateUser(String username, String email) {
        if (userRepository.findByUsername(username).isPresent()) {
            log.warn("사용자 생성 실패 - 중복된 username: {}", username);
            throw new UserAlreadyExistsException("username", username);
        }

        if (userRepository.existsByEmail(email)) {
            log.warn("사용자 생성 실패 - 중복된 email: {}", email);
            throw new UserAlreadyExistsException("email", email);
        }
    }
}