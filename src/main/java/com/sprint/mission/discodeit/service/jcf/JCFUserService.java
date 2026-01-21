package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;
    private MessageService messageService;
    private ChannelService channelService;

    public JCFUserService() {
        this.data = new HashMap<>();
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
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(UUID uuid) {
        return findAllUsers().stream()
                .filter(u -> Objects.equals(u.getId(), uuid)).findFirst()
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
    }

    @Override
    public Optional<User> findUserByAccountId(String accountId) {
        return findAllUsers().stream()
                .filter(u -> Objects.equals(u.getAccountId(), accountId)).findFirst();
    }

    @Override
    public Optional<User> findUserByMail(String mail) {
        return findAllUsers().stream()
                .filter(u -> Objects.equals(u.getMail(), mail)).findFirst();
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(data.values());
    }

    // 변경사항이 없는 필드는 null로 받음
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

        return user;
    }

    @Override
    public User updateUser(User newUser) {
        return updateUser(newUser.getId(), null, null, null, null);
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
        data.remove(user.getId());
    }
}
