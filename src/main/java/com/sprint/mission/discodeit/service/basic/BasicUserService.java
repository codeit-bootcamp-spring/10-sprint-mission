package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicUserService implements UserService {
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;

    @Override
    public UserDto.response createUser(UserDto.createRequest userReq, BinaryContentDto.createRequest profileReq) {
        userRepository.findAll().forEach(u -> {
            if (Objects.equals(u.getAccountId(), userReq.accountId())) throw new IllegalStateException("이미 존재하는 accountId입니다");
            if (Objects.equals(u.getMail(), userReq.mail())) throw new IllegalStateException("이미 존재하는 mail입니다");
        });

        User user = new User(userReq.accountId(), userReq.password(), userReq.name(), userReq.mail());
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
    public UserDto.response findUserByMail(String mail) {
        return findUserEntityByMail(mail)
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
        if (userReq.mail() != null && !Objects.equals(user.getMail(), userReq.accountId()))
            validateDuplicateMail(userReq.accountId());

        Optional.ofNullable(userReq.accountId()).ifPresent(user::updateAccountId);
        Optional.ofNullable(userReq.password()).ifPresent(user::updatePassword);
        Optional.ofNullable(userReq.name()).ifPresent(user::updateName);
        Optional.ofNullable(userReq.mail()).ifPresent(user::updateMail);

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

        Optional.ofNullable(user.getProfileId())
                        .ifPresent(binaryContentRepository::deleteById);
        userStatusRepository.deleteByUserId(user.getId());
        userRepository.deleteById(user.getId());
    }

    private void validateDuplicateAccount(String accountId) {
        findUserEntityByAccountId(accountId).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 accountId입니다"); });
    }

    private void validateDuplicateMail(String mail) {
        findUserEntityByMail(mail).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 mail입니다"); });
    }

    private UserDto.response toResponse(User user) {
        boolean isOnline = userStatusRepository.findByUserId(user.getId())
                                                .map(UserStatus::isOnline).orElse(false);

        return new UserDto.response(user.getId(), user.getCreatedAt(), user.getUpdatedAt(),
                user.getAccountId(), user.getName(), user.getMail(),
                user.getProfileId(), isOnline,
                user.getJoinedChannels().stream().toList(),
                user.getMessageHistory());
    }

    private void processUpdateProfile(User user, BinaryContentDto.createRequest profileReq) {
        Optional.ofNullable(profileReq).ifPresent(p -> {
            BinaryContentType contentType = p.contentType();
            String fileName = p.filename();
            Byte[] contentBytes = p.contentBytes();
            BinaryContent content = new BinaryContent(contentType, fileName, contentBytes);
            binaryContentRepository.save(content);
            binaryContentRepository.findById(content.getId()).ifPresent(c -> {
                user.updateProfileId(c.getId());
            });
        });
    }

    private Optional<User> findUserEntityByAccountId(String accountId) {
        return userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getAccountId(), accountId))
                .findFirst();
    }

    private Optional<User> findUserEntityByMail(String mail) {
        return userRepository.findAll().stream()
                .filter(u -> Objects.equals(u.getMail(), mail))
                .findFirst();
    }
}
