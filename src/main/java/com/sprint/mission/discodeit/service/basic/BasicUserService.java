package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    @Value("${discodeit.upload.profile}")
    private String PROFILE_DIR;

    @Override
    public UserDto.userResponse createUser(UserDto.userCreateRequest userReq, BinaryContentDto.binaryContentCreateRequest profileReq) {
        userRepository.findAll().forEach(u -> {
            if (Objects.equals(u.getUsername(), userReq.username())) throw new BusinessLogicException(ErrorCode.DUPLICATE_USER);
            if (Objects.equals(u.getEmail(), userReq.email())) throw new BusinessLogicException(ErrorCode.DUPLICATE_USER);
        });

        User user = new User(userReq.username(), userReq.password(), userReq.email());
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        // profile 이미지를 같이 추가하면
        processUpdateProfile(user, profileReq);
        userRepository.save(user);

        return toResponse(user);
    }


    @Override
    public UserDto.userResponse findUser(UUID uuid) {
        return userRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public UserDto.userResponse findUserByUsername(String username) {
        return findUserEntityByUsername(username)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public UserDto.userResponse findUserByEmail(String email) {
        return findUserEntityByEmail(email)
                .map(this::toResponse)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public List<UserDto.userResponse> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    @Override
    public UserDto.userResponse updateUser(UUID uuid, UserDto.userUpdateRequest userReq, BinaryContentDto.binaryContentCreateRequest profileReq) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        // username과 mail 중복성 검사
        if (userReq.username() != null && !Objects.equals(user.getUsername(), userReq.username()))
            validateDuplicateUsername(userReq.username());
        if (userReq.email() != null && !Objects.equals(user.getEmail(), userReq.email()))
            validateDuplicateEmail(userReq.email());

        Optional.ofNullable(userReq.username()).ifPresent(user::updateUserName);
        Optional.ofNullable(userReq.password()).ifPresent(user::updatePassword);
        Optional.ofNullable(userReq.email()).ifPresent(user::updateEmail);

        // 변경되는 프로필 이미지가 있으면
        processUpdateProfile(user, profileReq);

        user.updateUpdatedAt();
        userRepository.save(user);

        return toResponse(user);
    }

    @Override
    public void deleteUser(UUID uuid) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new BusinessLogicException(ErrorCode.USER_NOT_FOUND));

        // 유저의 참여채널 제거
        user.getJoinedChannels().forEach(chId -> {
            Channel channel = channelRepository.findById(chId)
                    .orElseThrow(() -> new BusinessLogicException(ErrorCode.CHANNEL_NOT_FOUND));
            channel.removeParticipant(user.getId());
            channel.updateUpdatedAt();
            channelRepository.save(channel);
        });

        // 기존 프로필 이미지 있으면 삭제
        deleteProfileIfExists(user.getProfileId(), Paths.get(PROFILE_DIR));

        userStatusRepository.deleteByUserId(user.getId());
        userRepository.deleteById(user.getId());
    }

    private void validateDuplicateUsername(String username) {
        findUserEntityByUsername(username).ifPresent(u -> { throw new BusinessLogicException(ErrorCode.DUPLICATE_USERNAME); });
    }

    private void validateDuplicateEmail(String email) {
        findUserEntityByEmail(email).ifPresent(u -> { throw new BusinessLogicException(ErrorCode.DUPLICATE_EMAIL); });
    }

    private UserDto.userResponse toResponse(User user) {
        boolean online = userStatusRepository.findByUserId(user.getId())
                                                .map(UserStatus::isOnline).orElse(false);

        return new UserDto.userResponse(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getUsername(), user.getEmail(),
                user.getProfileId(), online);
    }

    private void processUpdateProfile(User user, BinaryContentDto.binaryContentCreateRequest profileReq) {
        Optional.ofNullable(profileReq).ifPresent(req -> {
            Path dir = Paths.get(PROFILE_DIR);
            // 기존 프로필 이미지 있으면 삭제
            deleteProfileIfExists(user.getProfileId(), dir);

            BinaryContent content = new BinaryContent(req.contentType(), req.filename(), PROFILE_DIR);
            // 이미지 저장
            String fileName = content.getId() + "." + StringUtils.getFilenameExtension(req.filename());
            try {
                Files.createDirectories(dir);
                Files.write(dir.resolve(fileName), req.bytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            binaryContentRepository.save(content);
            user.updateProfileId(content.getId());
        });
    }

    private void deleteProfileIfExists(UUID profileId, Path dir) {
        if (profileId != null) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, profileId + ".*")) {
                for (Path p : stream) {
                    Files.deleteIfExists(p);
                    break;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            binaryContentRepository.deleteById(profileId);
        }
    }

    private Optional<User> findUserEntityByUsername(String username) {
        return userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getUsername(), username))
                .findFirst();
    }

    private Optional<User> findUserEntityByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getEmail(), email))
                .findFirst();
    }
}
