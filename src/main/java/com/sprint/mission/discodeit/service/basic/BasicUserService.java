package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.CreateBinaryContentPayloadDTO;
import com.sprint.mission.discodeit.dto.user.CreateUserRequestDTO;
import com.sprint.mission.discodeit.dto.user.UpdateUserStatusRequestDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UpdateUserRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
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
            throw new IllegalArgumentException("이미 사용중인 username입니다.");
        }

        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("이미 사용중인 email입니다.");
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

        return userMapper.toDto(user);
    }

    @Override
    public UserDto updateUserStatus(UUID userId, UpdateUserStatusRequestDTO dto) {
        User user = findUserOrThrow(userId);
        UserStatus status = userStatusRepository.findByUser_Id(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "해당 userId에 대한 UserStatus가 존재하지 않습니다. userId=" + userId
                ));

        // 갱신
        status.updateLastActiveAt(dto.newLastActiveAt());

        return userMapper.toDto(user);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = findUserOrThrow(userId);

        BinaryContent profile = user.getProfile();
        if (profile != null && profile.getId() != null) {
            binaryContentRepository.deleteById(profile.getId());
        }
        userRepository.deleteById(userId);
    }

    // === 여기부터 내부 메서드 ===

    private User findUserOrThrow(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null 값일 수 없습니다.");

        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 id를 가진 유저가 존재하지 않습니다."));
    }

    private void updateUserName(UpdateUserRequestDTO dto, User user) {
        if (user.getUsername().equals(dto.newUsername())){
            throw new IllegalArgumentException("현재 사용중인 username과 동일합니다.");
        }

        if (userRepository.existsByUsername(dto.newUsername())) {
            throw new IllegalArgumentException("이미 사용중인 username입니다.");
        }

        user.updateUsername(dto.newUsername());     // 객체를 수정하면 JPA가 트랜잭션 커밋되는 순간에 update를 실행해줌
        // 그래서 userRepository.save(user); 코드가 삭제된 것
    }

    private void updateEmail(UpdateUserRequestDTO dto, User user) {
        if (user.getEmail().equals(dto.newEmail())){
            throw new IllegalArgumentException("현재 사용중인 email과 동일합니다.");
        }

        if (userRepository.existsByEmail(dto.newEmail())) {
            throw new IllegalArgumentException("이미 사용중인 email입니다.");
        }

        user.updateEmail(dto.newEmail());
    }

    private void updatePassword(UpdateUserRequestDTO dto, User user) {
        user.updatePassword(dto.newPassword());
    }
}
