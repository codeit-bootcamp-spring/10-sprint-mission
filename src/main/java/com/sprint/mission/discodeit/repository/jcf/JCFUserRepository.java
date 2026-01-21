package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.utils.CheckValidation;
import com.sprint.mission.discodeit.utils.SaveLoadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


public class JCFUserRepository implements UserRepository {

    private List<User> data = new ArrayList<>();
    private static final String path = "user.dat";

    @Override
    public void save(User user) {
        data.add(user);
    }

    @Override
    public User findByID(String userID) {
        Objects.requireNonNull(userID, "유효하지 않은 매개변수입니다.");

        return data.stream()
                .filter(u -> userID.equals(u.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다."));

    }

    @Override
    public List<User> findAll() {
        return data;
    }

    @Override
    public List<User> load() {
        this.data = SaveLoadUtil.load(path);
        return SaveLoadUtil.load(path);
    }

    @Override
    public User delete(User user) {
        data.remove(user);
        return user;
    }


}
