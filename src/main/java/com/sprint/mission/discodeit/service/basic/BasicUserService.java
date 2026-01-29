package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;
    private final UserMapper userMapper;

    @Override
    public User create(UserDto.CreateRequest request) {
        String username = request.username();
        String email = request.email();
        validationUser(null, username, email);
        String password = request.password();
        UUID profileId = saveProfileImage(request.profileImage());

        User user = new User(username, email, password, profileId);
        User createdUser = userRepository.save(user);

        UserStatus userStatus = new UserStatus(createdUser.getId(), Instant.now());
        userStatusRepository.save(userStatus);
        return createdUser;
    }

    @Override
    public UserDto.Response find(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다:" + userId));
    }

    @Override
    public List<UserDto.Response> findAll() {
        return userRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public User update(UUID userId, UserDto.UpdateRequest request) {
        User user=  userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다:" + userId));

        String newUsername = request.newUsername();
        String newEmail = request.newEmail();
        validationUser(user, newUsername, newEmail);
        String newPassword = request.newPassword();
        UUID newProfileId = saveProfileImage(request.newProfileImage());

        user.update(newUsername, newEmail, newPassword, newProfileId);

        return userRepository.save(user);
    }

    @Transactional
    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다: " + userId));

        if (user.getProfileId() != null) {
            binaryContentRepository.deleteById(user.getProfileId());
        }
        userStatusRepository.deleteByUserId(userId);
        userRepository.deleteById(userId);
    }

    // validation
    private void validationUser(User user, String username, String email) {
        if (userRepository.existsByEmail(email) && (user == null || !user.getEmail().equals(email))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
        }

        if (userRepository.existsByUsername(username) && (user == null || !user.getUsername().equals(username))) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다: " + username);
        }
    }

    // Helper
    private UserDto.Response toDto(User user) {
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저의 상태가 없습니다:" + user.getId()));

        return userMapper.toResponse(user, userStatus.isOnline());
    }

    private UUID saveProfileImage(BinaryContentDto.CreateRequest imageRequest) {
        if (imageRequest == null) {
            return null;
        }

        BinaryContent content = new BinaryContent(
                imageRequest.fileName(),
                imageRequest.contentType(),
                imageRequest.content()
        );

        return binaryContentRepository.save(content).getId();
    }
}
