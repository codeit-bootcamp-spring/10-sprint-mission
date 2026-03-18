package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.response.ApiException;
import com.sprint.mission.discodeit.response.ErrorCode;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {

    private final UserRepository userRepository;
    private final BinaryContentService binaryContentService;
    private final ReadStatusService readStatusService;
    private final UserStatusService userStatusService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserCreateRequest request, BinaryContentRequest profileImage) {
        validateDuplicateUser(request);

        User user = new User(
                request.username(),
                request.password(),
                request.email()
        );

        updateProfileImage(user, profileImage);
        userRepository.save(user);
        userStatusService.createUserStatus(user);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findUserByUserID(UUID userId) {
        return userMapper.toDto(getUserOrThrow(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        return userRepository.findAllWithStatusAndProfile().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto updateUser(
            UUID requestId,
            UserUpdateRequest request,
            BinaryContentRequest profileImage
    ) {
        User user = getUserOrThrow(requestId);
        validateDuplicateUserOnUpdate(user, request);

        user.update(
                request.newUsername(),
                request.newPassword(),
                request.newEmail(),
                null
        );

        updateProfileImage(user, profileImage);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID requestId) {
        User user = getUserOrThrow(requestId);
        UUID profileId = user.getProfile() == null ? null : user.getProfile().getId();

        for (Message message : List.copyOf(user.getMessages())) {
            message.assignAuthor(null);
        }
        for (ReadStatus readStatus : List.copyOf(user.getReadStatuses())) {
            readStatusService.deleteReadStatus(readStatus.getId());
        }
        if (user.getStatus() != null) {
            userStatusService.deleteUserStatus(user.getStatus().getId());
        }
        if (profileId != null) {
            user.updateProfile(null);
            binaryContentService.deleteBinaryContent(profileId);
        }

        userRepository.delete(user);
    }

    private void validateDuplicateUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new ApiException(ErrorCode.USERNAME_ALREADY_EXISTS,
                    "이미 존재하는 username 입니다 username: " + request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS,
                    "이미 존재하는 email 입니다. email: " + request.email());
        }
    }

    private void updateProfileImage(User user, BinaryContentRequest profileImage) {
        UUID previousProfileId = user.getProfile() == null ? null : user.getProfile().getId();
        UUID profileId = binaryContentService.createBinaryContent(profileImage);

        if (profileId == null) {
            return;
        }

        BinaryContent profile = binaryContentService.findBinaryContentEntity(profileId);
        user.updateProfile(profile);

        if (previousProfileId != null && !previousProfileId.equals(profileId)) {
            binaryContentService.deleteBinaryContent(previousProfileId);
        }
    }

    private User getUserOrThrow(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND,
                        "사용자를 찾을 수 없습니다 userId: " + userId));
    }

    private void validateDuplicateUserOnUpdate(User user, UserUpdateRequest request) {
        String newUsername = request.newUsername();
        if (newUsername != null
                && !newUsername.equals(user.getUsername())
                && userRepository.existsByUsername(newUsername)) {
            throw new ApiException(ErrorCode.USERNAME_ALREADY_EXISTS,
                    "이미 존재하는 username 입니다 username: " + newUsername);
        }

        String newEmail = request.newEmail();
        if (newEmail != null
                && !newEmail.equals(user.getEmail())
                && userRepository.existsByEmail(newEmail)) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_EXISTS,
                    "이미 존재하는 email 입니다. email: " + newEmail);
        }
    }
}
