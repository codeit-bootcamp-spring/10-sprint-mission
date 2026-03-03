package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.common.exception.code.ErrorCode;
import com.sprint.mission.discodeit.common.exception.custom.APIException;
import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserProfileImageDto;
import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserResponse;
import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserUniquenessDto;
import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserUpdateDto;
import com.sprint.mission.discodeit.dto.user.command.UserCreateCommand;
import com.sprint.mission.discodeit.dto.user.command.UserUpdateCommand;
import com.sprint.mission.discodeit.dto.user.request.UserFindRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.IdGenerator;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicUserService extends BasicDomainService<User> implements UserService {
    private final UserRepository userRepository;
    private final BinaryContentRepository profileRepository;
    private final UserStatusRepository userStatusRepository;
    private final IdGenerator idGenerator;

    @Override
    public UserResponse find(UserFindRequest request) {
        User user = userRepository.filter(u -> u.matchUsername(request.username()))
                .filter(u -> u.matchPassword(request.password()))
                .findFirst()
                .orElseThrow(() -> new APIException(ErrorCode.USERNAME_OR_PASSWORD_INCORRECT));
        UserStatus userStatus = findUserStatusByUserId(user.getId());
        return user.toResponse(userStatus.isActive());
    }

    @Override
    public UserResponse find(UUID userId) {
        User user = findById(userId);
        UserStatus userStatus = findUserStatusByUserId(user.getId());
        return user.toResponse(userStatus.isActive());
    }

    @Override
    public List<UserResponse> findAll() {
        return userRepository.streamAll(
                        stream -> stream.map(this::getUserResponse))
                .toList();
    }

    @Override
    public UserResponse create(UserCreateCommand dto) {
        validateUserUniqueness(dto.getUserUniquenessDto());

        BinaryContent profileImage = registerProfile(dto.getProfile());
        UUID profileImageId = getProfileId(profileImage);
        User user = new User(idGenerator.generateId(), dto.getUsername(), dto.getEmail(),
                dto.getPassword(), profileImageId);
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);
        userRepository.save(user);
        return user.toResponse(userStatus.isActive());
    }

    /*
     * todo: Regarding profile image updates.
     *  If an image already exists and the received image is null,
     *  distinguish whether to delete the image or not update it.
     * */
    @Override
    public UserResponse update(UserUpdateCommand command) {
        User user = findById(command.getId());
        validateUserUniqueness(command.getUserUniquenessDto());

        UserStatus userStatus = findUserStatusByUserId(command.getId());
        BinaryContent newProfileImage = registerProfile(command.getProfile());
        UUID newProfileId = getProfileId(newProfileImage);
        user.update(
                UserUpdateDto.builder()
                        .username(command.getUsername())
                        .email(command.getEmail())
                        .password(command.getPassword())
                        .profileId(newProfileId)
                        .build());
        userRepository.save(user);
        return user.toResponse(userStatus.isActive());
    }

    @Override
    public void delete(UUID userId) {
        User user = findById(userId);
        UserStatus status = findUserStatusByUserId(userId);
        UserResponse userResponse = user.toResponse(status.isActive());
        deleteIfExist(userResponse.profileId(), profileRepository,
                () -> new APIException(ErrorCode.BINARYCONTENTID_NOT_FOUND, userResponse.profileId()));
        deleteIfExist(status.getId(), userStatusRepository,
                () -> new APIException(ErrorCode.USERSTATUSID_NOT_FOUND, status.getId()));
        deleteIfExist(userId, userRepository,
                () -> new APIException(ErrorCode.USERID_NOT_FOUND, userId));
    }

    @Override
    protected User findById(UUID id) {
        return findEntityById(id, userRepository, () -> new APIException(ErrorCode.USERID_NOT_FOUND, id));
    }

    private void validateUserUniqueness(UserUniquenessDto dto) {
        if (userRepository.existsByUsername(dto.username())) {
            throw new APIException(ErrorCode.USERNAME_ALREADY_EXIST, dto.username());
        }
        if (userRepository.existsByEmail(dto.email())) {
            throw new APIException(ErrorCode.EMAIL_ALREADY_EXIST, dto.email());
        }
    }

    private BinaryContent registerProfile(UserProfileImageDto dto) {
        if (dto == null) {
            return null;
        }
        BinaryContent profileImage = new BinaryContent(dto.fileName(), dto.data());
        return profileRepository.save(profileImage);
    }

    private UserStatus findUserStatusByUserId(UUID userId) {
        return userStatusRepository.findByUserId(userId)
                .orElseThrow(() -> new APIException(ErrorCode.USERSTATUS_NOT_FOUND_BY_USERID, userId));
    }

    private UserResponse getUserResponse(User user) {
        UserStatus userStatus = findUserStatusByUserId(user.getId());
        return user.toResponse(userStatus.isActive());
    }

    @Nullable
    private UUID getProfileId(BinaryContent profileImage) {
        return profileImage == null ? null : profileImage.getId();
    }
}
