package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {

    private final Map<UUID, User> users;    // 유저 전체 목록

    public JCFUserService() {
        this.users = new HashMap<>();
    }

    @Override
    public User create(String name, String email, String profileImageUrl) {
        Objects.requireNonNull(name, "유저 이름은 필수 항목입니다.");
        Objects.requireNonNull(email, "이메일은 필수 항목입니다.");
        Objects.requireNonNull(profileImageUrl, "프로필 이미지 URL은 필수 항목입니다.");

        User user = new User(name, email, profileImageUrl);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public void delete(UUID userId) {

        findById(userId);

        users.remove(userId);
    }


    @Override
    public User findById(UUID userId) {
        Objects.requireNonNull(userId, "유저 Id가 유효하지 않습니다.");
        return Objects.requireNonNull(users.get(userId), "Id에 해당하는 유저가 존재하지 않습니다.");
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User update(UUID userId, String name, String email, String profileImageUrl) {
        Objects.requireNonNull(userId, "유저 Id가 유효하지 않습니다.");
        User user = findById(userId);
        if (user == null) {
            System.out.println("수정할 유저가 존재하지 않습니다.");
            return null;
        }

        Optional.ofNullable(name).ifPresent(user::updateName);
        Optional.ofNullable(email).ifPresent(user::updateEmail);
        Optional.ofNullable(profileImageUrl).ifPresent(user::updateProfileImageUrl);
        return user;
    }

}
