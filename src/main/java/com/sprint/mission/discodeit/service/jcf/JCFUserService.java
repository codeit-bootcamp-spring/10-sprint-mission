package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User createUser(String accountId, String password, String name, String mail) {
        findAllUsers().forEach(u -> {
            if (Objects.equals(u.getAccountId(), accountId)) throw new IllegalStateException("이미 가입된 유저입니다");
            if (Objects.equals(u.getMail(), mail)) throw new IllegalStateException("이미 가입된 유저입니다");
        });

        User user = new User(accountId, password, name, mail);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findUser(UUID uuid) {
        return findAllUsers().stream().filter(u -> Objects.equals(u.getId(), uuid)).findFirst();
    }

    @Override
    public Optional<User> findUserByAccountId(String accountId) {
        return findAllUsers().stream().filter(u -> Objects.equals(u.getAccountId(), accountId)).findFirst();
    }

    @Override
    public Optional<User> findUserByMail(String mail) {
        return findAllUsers().stream().filter(u -> Objects.equals(u.getMail(), mail)).findFirst();
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateUser(UUID uuid, String accountId, String password, String name, String mail) {
        User user = findUser(uuid).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
        // accountId와 mail 중복성 검사
        if (!Objects.equals(user.getAccountId(), accountId))
            findUserByAccountId(accountId).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 accountId입니다"); });
        if (!Objects.equals(user.getMail(), mail))
            findUserByMail(mail).ifPresent(u -> { throw new IllegalStateException("이미 존재하는 accountId입니다"); });

        boolean isChanged = false;
        if (!Objects.equals(user.getAccountId(), accountId)) {
            user.updateAccountId(accountId);
            isChanged = true;
        }
        if (!Objects.equals(user.getPassword(), password)) {
            user.updatePassword(password);
            isChanged = true;
        }
        if (!Objects.equals(user.getName(), name)) {
            user.updateName(name);
            isChanged = true;
        }
        if (!Objects.equals(user.getMail(), mail)) {
            user.updateMail(mail);
            isChanged = true;
        }

        if (isChanged) {
            user.updateUpdatedAt();
        }

        return user;
    }

    @Override
    public void deleteUser(UUID uuid) {
        User user = findUser(uuid).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
        data.remove(user.getId());
    }

    @Override
    public void deleteUserByAccountId(String accountId) {
        User user = findUserByAccountId(accountId).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
        data.remove(user.getId());
    }

    @Override
    public void deleteUserByMail(String mail) {
        User user = findUserByMail(mail).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));
        data.remove(user.getId());
    }
}
