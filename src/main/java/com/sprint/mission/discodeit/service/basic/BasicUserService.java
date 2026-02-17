package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.*;
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
    public UserDto.response createUser(UserDto.createRequest userReq, BinaryContentDto.createRequest profileReq) {
        userRepository.findAll().forEach(u -> {
            if (Objects.equals(u.getAccountId(), userReq.accountId())) throw new IllegalStateException("이미 존재하는 accountId입니다");
            if (Objects.equals(u.getEmail(), userReq.email())) throw new IllegalStateException("이미 존재하는 mail입니다");
        });

        User user = new User(userReq.accountId(), userReq.password(), userReq.username(), userReq.email());
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        // profile 이미지를 같이 추가하면
        processUpdateProfile(user, profileReq);
        userRepository.save(user);

        return toResponse(user);
    }


    @Override
    public UserDto.response findUser(UUID uuid) {
        return userRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
    }

    @Override
    public UserDto.response findUserByAccountId(String accountId) {
        return findUserEntityByAccountId(accountId)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
    }

    @Override
    public UserDto.response findUserByEmail(String email) {
        return findUserEntityByEmail(email)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
    }

    @Override
    public List<UserDto.response> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    @Override
    public UserDto.response updateUser(UUID uuid, UserDto.updateRequest userReq, BinaryContentDto.createRequest profileReq) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));

        // accountId와 mail 중복성 검사
        if (userReq.accountId() != null && !Objects.equals(user.getAccountId(), userReq.accountId()))
            validateDuplicateAccount(userReq.accountId());
        if (userReq.email() != null && !Objects.equals(user.getEmail(), userReq.email()))
            validateDuplicateEmail(userReq.email());

        Optional.ofNullable(userReq.accountId()).ifPresent(user::updateAccountId);
        Optional.ofNullable(userReq.password()).ifPresent(user::updatePassword);
        Optional.ofNullable(userReq.username()).ifPresent(user::updateUserName);
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
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));

        // 유저의 참여채널 제거
        user.getJoinedChannels().forEach(chId -> {
            Channel channel = channelRepository.findById(chId)
                    .orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
            channel.removeParticipant(user.getId());
            channel.updateUpdatedAt();
            channelRepository.save(channel);
        });

        // 기존 프로필 이미지 있으면 삭제
        deleteProfileIfExists(user.getProfileId(), Paths.get(PROFILE_DIR));

        userStatusRepository.deleteByUserId(user.getId());
        userRepository.deleteById(user.getId());
    }

    private void validateDuplicateAccount(String accountId) {
        findUserEntityByAccountId(accountId).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 accountId입니다"); });
    }

    private void validateDuplicateEmail(String email) {
        findUserEntityByEmail(email).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 mail입니다"); });
    }

    private UserDto.response toResponse(User user) {
        boolean online = userStatusRepository.findByUserId(user.getId())
                                                .map(UserStatus::isOnline).orElse(false);

        return new UserDto.response(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getAccountId(), user.getUsername(), user.getEmail(),
                user.getProfileId(), online,
                user.getJoinedChannels().stream().toList(),
                user.getMessageHistory());
    }

    private void processUpdateProfile(User user, BinaryContentDto.createRequest profileReq) {
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

    private Optional<User> findUserEntityByAccountId(String accountId) {
        return userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getAccountId(), accountId))
                .findFirst();
    }

    private Optional<User> findUserEntityByEmail(String email) {
        return userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getEmail(), email))
                .findFirst();
    }
}
