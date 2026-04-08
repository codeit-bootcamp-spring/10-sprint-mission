package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.exception.user.UserAlreadyExistsException;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import com.sprint.mission.discodeit.service.ReadStatusService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        log.debug("사용자 생성 처리 시작: username={}, email={}", request.username(), request.email());
        validateDuplicateUser(request);

        User user = new User(
                request.username(),
                request.password(),
                request.email()
        );

        updateProfileImage(user, profileImage);
        userRepository.save(user);
        userStatusService.createUserStatus(user);
        log.info("사용자 생성 완료: userId={}, username={}", user.getId(), user.getUsername());

        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findUserByUserID(UUID userId) {
        log.debug("사용자 조회: userId={}", userId);
        return userMapper.toDto(getUserOrThrow(userId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        log.debug("전체 사용자 목록 조회");
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
        log.debug("사용자 수정 처리 시작: userId={}", requestId);
        User user = getUserOrThrow(requestId);
        validateDuplicateUserOnUpdate(user, request);

        user.update(
                request.newUsername(),
                request.newPassword(),
                request.newEmail(),
                null
        );

        updateProfileImage(user, profileImage);
        log.info("사용자 수정 완료: userId={}", requestId);

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID requestId) {
        log.debug("사용자 삭제 처리 시작: userId={}", requestId);
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
        log.info("사용자 삭제 완료: userId={}", requestId);
    }

    private void validateDuplicateUser(UserCreateRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            log.warn("중복 username으로 사용자 생성 시도: username={}", request.username());
            throw new UserAlreadyExistsException(
                    ErrorCode.USERNAME_ALREADY_EXISTS,
                    "이미 존재하는 username 입니다 username: " + request.username(),
                    Map.of("username", request.username())
            );
        }

        if (userRepository.existsByEmail(request.email())) {
            log.warn("중복 email로 사용자 생성 시도: email={}", request.email());
            throw new UserAlreadyExistsException(
                    ErrorCode.EMAIL_ALREADY_EXISTS,
                    "이미 존재하는 email 입니다. email: " + request.email(),
                    Map.of("email", request.email())
            );
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
                .orElseThrow(() -> new UserNotFoundException(
                        "사용자를 찾을 수 없습니다 userId: " + userId,
                        Map.of("userId", userId)
                ));
    }

    private void validateDuplicateUserOnUpdate(User user, UserUpdateRequest request) {
        String newUsername = request.newUsername();
        if (newUsername != null
                && !newUsername.equals(user.getUsername())
                && userRepository.existsByUsername(newUsername)) {
            throw new UserAlreadyExistsException(
                    ErrorCode.USERNAME_ALREADY_EXISTS,
                    "이미 존재하는 username 입니다 username: " + newUsername,
                    Map.of("username", newUsername)
            );
        }

        String newEmail = request.newEmail();
        if (newEmail != null
                && !newEmail.equals(user.getEmail())
                && userRepository.existsByEmail(newEmail)) {
            throw new UserAlreadyExistsException(
                    ErrorCode.EMAIL_ALREADY_EXISTS,
                    "이미 존재하는 email 입니다. email: " + newEmail,
                    Map.of("email", newEmail)
            );
        }
    }
}
