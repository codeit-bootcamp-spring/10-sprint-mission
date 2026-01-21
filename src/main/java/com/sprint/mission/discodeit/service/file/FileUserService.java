package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.utility.FileSerDe;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;


public class FileUserService extends FileSerDe<User> implements UserService {
    private final String USER_DATA_DIRECTORY = "data/user";
    private MessageService messageService;
    private ChannelService channelService;

    public FileUserService() {
        super(User.class);
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public User createUser(String accountId, String password, String name, String mail) {
        findAllUsers().forEach(u -> {
            if (Objects.equals(u.getAccountId(), accountId)) throw new IllegalStateException("이미 존재하는 accountId입니다");
            if (Objects.equals(u.getMail(), mail)) throw new IllegalStateException("이미 존재하는 mail입니다");
        });

        User user = new User(accountId, password, name, mail);
        return save(USER_DATA_DIRECTORY, user);
    }

    @Override
    public User getUser(UUID uuid) {
        return load(USER_DATA_DIRECTORY, uuid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
    }

    @Override
    public Optional<User> findUserByAccountId(String accountId) {
        return loadAll(USER_DATA_DIRECTORY).stream()
                .filter(u -> Objects.equals(u.getAccountId(), accountId)).findFirst();
    }

    @Override
    public Optional<User> findUserByMail(String mail) {
        return loadAll(USER_DATA_DIRECTORY).stream()
                .filter(u -> Objects.equals(u.getMail(), mail)).findFirst();
    }

    @Override
    public List<User> findAllUsers() {
        return loadAll(USER_DATA_DIRECTORY);
    }

    @Override
    public User updateUser(UUID uuid, String accountId, String password, String name, String mail) {
        User user = getUser(uuid);
        // accountId와 mail 중복성 검사
        if (accountId != null && !Objects.equals(user.getAccountId(), accountId))
            validateDuplicateAccount(accountId);
        if (mail != null && !Objects.equals(user.getMail(), mail))
            validateDuplicateMail(mail);

        Optional.ofNullable(accountId).ifPresent(user::updateAccountId);
        Optional.ofNullable(password).ifPresent(user::updatePassword);
        Optional.ofNullable(name).ifPresent(user::updateName);
        Optional.ofNullable(mail).ifPresent(user::updateMail);
        user.updateUpdatedAt();
        return save(USER_DATA_DIRECTORY, user);
    }

    @Override
    public User updateUser(User newUser) {
        newUser.updateUpdatedAt();
        return save(USER_DATA_DIRECTORY, newUser);
    }

    @Override
    public void deleteUser(UUID uuid) {
        User user = getUser(uuid);
        deleteProcess(user);
    }

    @Override
    public void deleteUserByAccountId(String accountId) {
        User user = findUserByAccountId(accountId).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
        deleteProcess(user);
    }

    @Override
    public void deleteUserByMail(String mail) {
        User user = findUserByMail(mail).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
        deleteProcess(user);
    }

    private void validateDuplicateAccount(String accountId) {
        findUserByAccountId(accountId).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 accountId입니다"); });
    }

    private void validateDuplicateMail(String mail) {
        findUserByMail(mail).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 mail입니다"); });
    }

    private void deleteProcess(User user) {
        List.copyOf(user.getJoinedChannels()).forEach(ch -> channelService.leaveChannel(ch.getId(), user.getId()));
        List.copyOf(user.getMessageHistory()).forEach(m -> messageService.deleteMessage(m.getId()));
        delete(USER_DATA_DIRECTORY, user.getId());
    }
}
