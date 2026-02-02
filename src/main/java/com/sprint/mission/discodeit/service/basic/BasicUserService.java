package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.BinaryContentType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
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
    private final MessageRepository messageRepository;

    @Override
    public User createUser(UserDto.createRequest userReq, BinaryContentDto.createRequest profileReq) {
        userRepository.findAll().forEach(u -> {
            if (Objects.equals(u.getAccountId(), userReq.accountId())) throw new IllegalStateException("이미 존재하는 accountId입니다");
            if (Objects.equals(u.getMail(), userReq.mail())) throw new IllegalStateException("이미 존재하는 mail입니다");
        });

        User user = new User(userReq.accountId(), userReq.password(), userReq.name(), userReq.mail());
        UserStatus userStatus = new UserStatus(user.getId());
        userStatusRepository.save(userStatus);

        // profile 이미지를 같이 추가하면
        processUpdateProfile(user, profileReq);

        return userRepository.save(user);
    }


    @Override
    public UserDto.response getUser(UUID uuid) {
        return userRepository.findById(uuid)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
    }

    @Override
    public Optional<UserDto.response> findUserByAccountId(String accountId) {
        return findUserEntityByAccountId(accountId)
                .map(this::toResponse);
    }

    @Override
    public Optional<UserDto.response> findUserByMail(String mail) {
        return findUserEntityByMail(mail)
                .map(this::toResponse);
    }

    @Override
    public List<UserDto.response> findAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse).toList();
    }

    @Override
    public User updateUser(UUID uuid, UserDto.updateRequest userReq, BinaryContentDto.createRequest profileReq) {
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

        // 다른 객체도 변경
        channelRepository.findAll().forEach(c -> {
            c.getParticipants().stream()
                    .filter(u -> Objects.equals(u.getId(), user.getId()))
                    .findFirst()
                    .ifPresent(u -> {
                        c.updateParticipant(user);
                        channelRepository.save(c);
                    });
        });
        messageRepository.findAll().stream()
                .filter(m -> Objects.equals(m.getUser().getId(), user.getId()))
                .forEach(m -> {
                    m.updateUserIfSameId(user);
                    messageRepository.save(m);
                });

        return user;
    }

    @Override
    public void deleteUser(UUID uuid) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
        deleteProcess(user);
    }

    @Override
    public void deleteUserByAccountId(String accountId) {
        User user = findUserEntityByAccountId(accountId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
        deleteProcess(user);
    }

    @Override
    public void deleteUserByMail(String mail) {
        User user = findUserEntityByMail(mail)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
        deleteProcess(user);
    }

    private void validateDuplicateAccount(String accountId) {
        findUserEntityByAccountId(accountId).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 accountId입니다"); });
    }

    private void validateDuplicateMail(String mail) {
        findUserEntityByMail(mail).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 mail입니다"); });
    }

    private void deleteProcess(User user) {
//        List.copyOf(user.getJoinedChannels()).forEach(ch -> channelService.leaveChannel(ch.getId(), user.getId()));
//        List.copyOf(user.getMessageHistory()).forEach(m -> messageService.deleteMessage(m.getId()));
        binaryContentRepository.deleteById(user.getProfileId());
        userStatusRepository.deleteByUserId(user.getId());
        userRepository.deleteById(user.getId());
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
