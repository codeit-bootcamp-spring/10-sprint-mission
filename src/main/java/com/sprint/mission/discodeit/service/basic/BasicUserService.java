package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.user.ProfileImageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final ReadStatusRepository readStatusRepository;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public UserResponse create(UserCreateRequest request) {
        requireNonNull(request, "request");
        requireNonNull(request.userName(), "userName");
        requireNonNull(request.email(), "email");
        requireNonNull(request.password(), "password");

        if (request.password().isEmpty()) {
            throw new BusinessLogicException(ErrorCode.PASSWORD_EMPTY);
        }

        if (userRepository.existsByName(request.userName())) {
            throw new BusinessLogicException(ErrorCode.DUPLICATION_USER);
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessLogicException(ErrorCode.DUPLICATION_EMAIL);
        }

        User user = new User(request.userName(), request.email(), request.password());
        userRepository.save(user);

        UserStatus status = new UserStatus(user.getId(), Instant.now());
        userStatusRepository.save(status);

        if (request.profileImage() != null) {
            ProfileImageCreateRequest imgReq = request.profileImage();

            BinaryContent image = new BinaryContent(
                    imgReq.fileName(),
                    imgReq.contentType(),
                    imgReq.data(),
                    user.getId(),
                    null
            );

            binaryContentRepository.save(image);
            user.updateProfileImage(image.getId());
            userRepository.save(user);
        }

        return toResponse(user, status);
    }

    @Override
    public UserResponse find(UUID userId) {
        requireNonNull(userId, "userId");

        User user = userRepository.findById(userId);
        if (user == null) throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);

        UserStatus status = userStatusRepository.findByUserId(userId);
        if (status == null) throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);

        return toResponse(user, status);
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus status = userStatusRepository.findByUserId(user.getId());
                    if (status == null) throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);
                    return toResponse(user, status);
                })
                .toList();
    }

    @Override
    public List<UserDto> findAllDto() {
        return userRepository.findAll().stream()
                .map(user -> {
                    UserStatus status = userStatusRepository.findByUserId(user.getId());
                    if (status == null) throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);

                    return new UserDto(
                            user.getId(),
                            user.getCreatedAt(),
                            user.getUpdatedAt(),
                            user.getName(),
                            user.getEmail(),
                            user.getProfileImageId(),
                            status.isOnline()
                    );
                })
                .toList();
    }

    @Override
    public UserResponse update(UserUpdateRequest request) {
        requireNonNull(request, "request");
        requireNonNull(request.userId(), "userId");

        User user = userRepository.findById(request.userId());
        if (user == null) throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);

        request.userName().ifPresent(newName -> {
            if (!user.getName().equals(newName) && userRepository.existsByName(newName)) {
                throw new BusinessLogicException(ErrorCode.DUPLICATION_USER);
            }
            user.updateName(newName);
        });

        request.email().ifPresent(newEmail -> {
            if (!user.getEmail().equals(newEmail) && userRepository.existsByEmail(newEmail)) {
                throw new BusinessLogicException(ErrorCode.DUPLICATION_EMAIL);
            }
            user.updateEmail(newEmail);
        });

        request.password().ifPresent(newPassword -> {
            if (newPassword.isEmpty()) {
                throw new BusinessLogicException(ErrorCode.PASSWORD_EMPTY);
            }
            user.updatePassword(newPassword);
        });

        request.profileImage().ifPresent(imgReq -> {
            if (user.getProfileImageId() != null) {
                binaryContentRepository.delete(user.getProfileImageId());
            }

            BinaryContent newImage = new BinaryContent(
                    imgReq.fileName(),
                    imgReq.contentType(),
                    imgReq.data(),
                    user.getId(),
                    null
            );

            binaryContentRepository.save(newImage);
            user.updateProfileImage(newImage.getId());
        });

        userRepository.save(user);

        UserStatus status = userStatusRepository.findByUserId(user.getId());
        if (status == null) throw new BusinessLogicException(ErrorCode.STATUS_NOT_FOUND);

        return toResponse(user, status);
    }

    @Override
    public void delete(UUID userId) {
        requireNonNull(userId, "userId");

        User user = userRepository.findById(userId);
        if (user == null) throw new BusinessLogicException(ErrorCode.USER_NOT_FOUND);

        if (user.getProfileImageId() != null) {
            binaryContentRepository.delete(user.getProfileImageId());
        }

        userStatusRepository.deleteByUserId(userId);
        readStatusRepository.deleteByUserId(userId);
        userRepository.delete(userId);
    }

    private UserResponse toResponse(User user, UserStatus status) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                status.isOnline(),
                status.getLastSeenAt(),
                user.getProfileImageId()
        );
    }

    private static <T> void requireNonNull(T value, String name) {
        if (value == null) {
            throw new IllegalArgumentException(name + " null이 될 수 없습니다.");
        }
    }
}
