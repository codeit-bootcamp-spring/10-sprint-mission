package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserDto.CreateRequest request) {
        String username = request.username();
        String email = request.email();
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + email);
        }
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.: " + username);
        }
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
                .map(this::mapToDto) // 헬퍼 사용
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다:" + userId));
    }

    @Override
    public List<UserDto.Response> findAll() {
        return userRepository.findAll().stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public User update(UUID userId, UserDto.UpdateRequest request) {
        User user=  userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다:" + userId));

        String newUsername = request.newUsername();
        String newEmail = request.newEmail();
        if(newUsername != null && !newUsername.equals(user.getUsername())) { // 값이 바뀌지 않으면 중복검사 X
            if (userRepository.existsByUsername(newUsername)) {
                throw new IllegalArgumentException("이미 존재하는 사용자명입니다.: " + newUsername);
            }
        }
        if(newEmail != null && !newEmail.equals(user.getEmail())) { // 값이 바뀌지 않으면 중복검사 X
            if (userRepository.existsByEmail(newEmail)) {
                throw new IllegalArgumentException("이미 존재하는 사용자명입니다.: " + newEmail);
            }
        }
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

    // Helper
    private UserDto.Response mapToDto (User user) {
        UserStatus userStatus = userStatusRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NoSuchElementException("유저의 상태가 없습니다:" + user.getId()));

        return UserDto.Response.from(user, userStatus.isOnline());
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
