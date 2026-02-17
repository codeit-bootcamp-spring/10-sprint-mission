package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public UserDto create(UserCreateRequest userCreateRequest) {
        String username = userCreateRequest.username();
        String email = userCreateRequest.email();

        if (userRepository.existsByUsername(username)) {
            throw new BusinessException(ErrorCode.USERNAME_DUPLICATED);
        }
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
        }

        UUID nullableProfileId = userCreateRequest.profile()
                .map(profileRequest -> {
                    String fileName = profileRequest.fileName();
                    String contentType = profileRequest.contentType();
                    byte[] bytes = profileRequest.bytes();
                    BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
                    return binaryContentRepository.save(binaryContent).getId();
                })
                .orElse(null);
        String password = userCreateRequest.password();

        User user = new User(username, email, password, nullableProfileId);
        userRepository.save(user);

        Instant now = Instant.now();
        UserStatus userStatus = new UserStatus(user.getId(), now);
        userStatusRepository.save(userStatus);

        return toDto(user);
    }

    @Override
    public UserDto find(UUID userId) {
        return userRepository.findById(userId)
                .map(this::toDto)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public UserDto update(UUID userId, UserUpdateRequest userUpdateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        String newUsername = userUpdateRequest.newUsername();
        String newEmail = userUpdateRequest.newEmail();
        String newPassword = userUpdateRequest.newPassword();
        BinaryContentCreateRequest newProfile = userUpdateRequest.newProfile();

        if (newUsername != null) {
            if (newUsername.isBlank()) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR);
            }
            if (!newUsername.equals(user.getUsername()) && userRepository.existsByUsername(newUsername)) {
                throw new BusinessException(ErrorCode.USERNAME_DUPLICATED);
            }
        }

        if (newEmail != null) {
            if (newEmail.isBlank()) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR);
            }
            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new BusinessException(ErrorCode.EMAIL_DUPLICATED);
            }
        }

        if (newPassword != null) {
            if (newPassword.isBlank()) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR);
            }
        }

        UUID profileIdToApply = user.getProfileId();

        if (newProfile != null) {
            String fileName = newProfile.fileName();
            String contentType = newProfile.contentType();
            byte[] bytes = newProfile.bytes();

            if (fileName == null || fileName.isBlank()) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR);
            }
            if (contentType == null || contentType.isBlank()) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR);
            }
            if (bytes == null || bytes.length == 0) {
                throw new BusinessException(ErrorCode.VALIDATION_ERROR);
            }

            Optional.ofNullable(user.getProfileId())
                    .ifPresent(binaryContentRepository::deleteById);

            BinaryContent binaryContent = new BinaryContent(fileName, (long) bytes.length, contentType, bytes);
            profileIdToApply = binaryContentRepository.save(binaryContent).getId();
        }

        user.update(newUsername, newEmail, newPassword, profileIdToApply);
        userRepository.save(user);

        return toDto(user);
    }


    @Override
    public void delete(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Optional.ofNullable(user.getProfileId())
                .ifPresent(binaryContentRepository::deleteById);
        userStatusRepository.deleteByUserId(userId);

        userRepository.deleteById(userId);
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
                user.getProfileId(),
                online
        );
    }
}
