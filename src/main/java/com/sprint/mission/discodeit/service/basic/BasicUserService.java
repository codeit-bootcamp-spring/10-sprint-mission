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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserDto createUser(CreateUserRequestDTO dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new IllegalArgumentException("이미 사용중인 username입니다.");
        }

        if (userRepository.existsByEmail(dto.email())) {
            throw new IllegalArgumentException("이미 사용중인 email입니다.");
        }

        // userId를 받아오기 위해 우선 객체 생성
        User user = UserMapper.toEntity(dto, null);

        if (dto.profileImage() != null) {
            var payload = dto.profileImage();
            BinaryContent bc = BinaryContentMapper.toEntity(user.getId(), null, payload);

            binaryContentRepository.save(bc);

            // 프로필 사진이 있으면 갱신하기
            user.updateProfileImage(bc.getId());
        }

        UserStatus status = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.save(status);

        userRepository.save(user);

        return UserMapper.toResponse(user, status);
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userRepository.findAll();
        List<UserStatus> statuses = userStatusRepository.findAll();

        return UserMapper.toResponseList(users, statuses);
    }

    @Override
    public List<UserDto> findAllByChannel(UUID channelId) {

        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getJoinedChannelIds().stream()
                        .anyMatch(c -> c.equals(channelId)))
                .toList();
        List<UserStatus> statuses = userStatusRepository.findAll();

        return UserMapper.toResponseList(users, statuses);
    }

    @Override
    public UserDto findByUserId(UUID userId) {
        return UserMapper.toResponse(
                findUserOrThrow(userId),
                userStatusRepository.findByUserId(userId)
                        .orElseThrow(() -> new NoSuchElementException(
                                "해당 userId에 대한 UserStatus가 존재하지 않습니다. userId=" + userId
                        ))
        );
    }

    @Override
    public UserDto updateUserInfo(UUID userId, UpdateUserRequestDTO dto) {
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
        if (dto.profileImage() != null) {
            updateUserProfileImage(dto, user);
        }

        return UserMapper.toResponse(
                user,
                userStatusRepository.findByUserId(userId)
                        .orElseThrow(() -> new NoSuchElementException(
                                "해당 userId에 대한 UserStatus가 존재하지 않습니다. userId=" + userId
                        ))
        );
    }

    @Override
    public UserDto updateUserStatus(UUID userId, UpdateUserStatusRequestDTO dto) {
        User user = findUserOrThrow(userId);
        UserStatus status = userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "해당 userId에 대한 UserStatus가 존재하지 않습니다. userId=" + userId
                ));

        status.updateIsOnline(dto.newLastActiveAt());
        userStatusRepository.save(status);

        return UserMapper.toResponse(user, status);
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = findUserOrThrow(userId);
        UUID binaryContentId = user.getProfileImageId();

        userStatusRepository.deleteById(userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "해당 userId에 대한 UserStatus가 존재하지 않습니다. userId=" + userId
                )).getId());
        if (binaryContentId != null){
            binaryContentRepository.deleteById(binaryContentId);
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

        user.updateUsername(dto.newUsername());
        userRepository.save(user);
    }

    private void updateEmail(UpdateUserRequestDTO dto, User user) {
        if (user.getEmail().equals(dto.newEmail())){
            throw new IllegalArgumentException("현재 사용중인 email과 동일합니다.");
        }

        if (userRepository.existsByEmail(dto.newEmail())) {
            throw new IllegalArgumentException("이미 사용중인 email입니다.");
        }

        user.updateEmail(dto.newEmail());
        userRepository.save(user);
    }

    private void updatePassword(UpdateUserRequestDTO dto, User user) {
        user.updatePassword(dto.newPassword());
        userRepository.save(user);
    }

    private void updateUserProfileImage(UpdateUserRequestDTO dto, User user) {
        if (dto.profileImage() == null) {
            throw new IllegalArgumentException("profile 값이 존재하지 않습니다.");
        }

        CreateBinaryContentPayloadDTO payload = dto.profileImage();

        BinaryContent binaryContent = BinaryContentMapper.toEntity(user.getId(), null, payload);

        binaryContentRepository.save(binaryContent);

        // 프로필 사진이 있으면 갱신하기
        user.updateProfileImage(binaryContent.getId());
        userRepository.save(user);
    }
}
