package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserCreateRequest;
import com.sprint.mission.discodeit.dto.request.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
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

@RequiredArgsConstructor
@Service
@Transactional
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    //
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public User create(UserCreateRequest userCreateRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();
        String password = userCreateRequest.password();

        if (userRepository.existsByEmail(email)) {//이메일 검증
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        if (userRepository.existsByUsername(username)) {//사용자이름 검증
            throw new IllegalArgumentException("User with username " + username + " already exists");
        }

        BinaryContent nullableProfile = userCreateRequest.optionalProfileCreateRequest()
                .map(profileRequest -> {
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long)bytes.length, contentType, bytes);
                    return binaryContentRepository.save(binaryContent);
                })
                .orElse(null);

        User user = new User(username, email, password, nullableProfile);
        UserStatus userStatus = new UserStatus(user);
        user.setStatus(userStatus);

        return userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> new UserNotFoundException(userId + "에 해당하는 사용자가 없습니다."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public User update(UUID userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId + "에 해당하는 사용자가 없습니다."));

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();

        if (newEmail != null && !newEmail.equals(user.getEmail())
                && userRepository.existsByEmail(newEmail)) {
            throw new IllegalArgumentException("User with email " + newEmail + " already exists");
        }
        if (newUsername != null && !newUsername.equals(user.getUsername())
                && userRepository.existsByUsername(newUsername)) {
            throw new IllegalArgumentException("User with username " + newUsername + " already exists");
        }

        BinaryContent nullableProfile = userUpdateRequest.optionalProfileCreateRequest()
                .map(profileRequest -> new BinaryContent(
                        profileRequest.fileName(),
                        (long) profileRequest.bytes().length,
                        profileRequest.contentType(),
                        profileRequest.bytes()
                ))
                .orElse(null);
//        binaryContentRepository.save(nullableProfile);//User Entity에 CascadeType.ALL 설정해서 BinaryContent도 자동 저장.
        user.update(newUsername, newEmail, newPassword, nullableProfile);

        return user;
    }

    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId + "에 해당하는 사용자가 없습니다."));

        userRepository.delete(user);
    }

    private UserDto toDto(User user) {
        Boolean online = userStatusRepository.findByUserId(user.getId())
                .map(UserStatus::isOnline)
                .orElse(null);

        return new UserDto(
                user.getId(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getUsername(),
                user.getEmail(),
                user.getProfile().getId(),
                online
        );
    }
}
