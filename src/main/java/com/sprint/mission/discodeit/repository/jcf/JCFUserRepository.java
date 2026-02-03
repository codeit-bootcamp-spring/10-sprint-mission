package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data;

    public JCFUserRepository() {
        this.data = new HashMap<>();
    }

    @Override
    public User save(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return Optional.ofNullable(data.get(userId));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID userId) {
        data.remove(userId);
    }

    @Override
    public boolean existsByEmail(String email) {
        // 유저 목록을 순회하며 이메일이 존재하는지 확인
        return data.values().stream()
                .anyMatch(user -> email.equals(user.getEmail()));
    }

    @Override
    public boolean existsByNickname(String nickname) {
        // 유저 목록을 순회하며 닉네임이 존재하는지 확인
        return data.values().stream()
                .anyMatch(user -> nickname.equals(user.getNickname()));
    }

    @Override
    public boolean existsByNicknameExceptUserId(String nickname, UUID exceptUserId) {
        // 유저 목록을 순회하며 닉네임은 일치하지만 id는 다른 유저가 있는지 확인
        return data.values().stream()
                .anyMatch(user ->
                        nickname.equals(user.getNickname()) &&
                                !user.getId().equals(exceptUserId));
    }

    @Override
    public Optional<User> findByEmailAndPassword(String email, String password) {
        return data.values().stream()
                .filter(user ->
                        user.getEmail().equals(email) &&
                                user.getPassword().equals(password))
                .findFirst();
    }
}
