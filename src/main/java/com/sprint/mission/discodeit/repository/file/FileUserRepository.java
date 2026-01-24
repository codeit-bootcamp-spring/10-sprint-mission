package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.utils.SaveLoadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileUserRepository implements UserRepository {

    private static final String path = "user.dat";
    private final List<User> data;

    public FileUserRepository(){
        this.data = new ArrayList<>();
        load();
    }

    public void persist() {
        SaveLoadUtil.save(data, path);
    }

    @Override
    public void save(User user) {
        Objects.requireNonNull(user, "유효하지 않은 유저입력.");

        for (int i = 0; i < data.size(); i++) {
            if (user.getId().equals(data.get(i).getId())) {
                data.set(i, user); // 덮어쓰기
                persist();
                return;
            }
        }

        data.add(user);

        persist();
    }

    @Override
    public User findByID(String userID) {
        Objects.requireNonNull(userID, "유효하지 않은 USERID 입니다.");

        return data
                .stream()
                .filter(u -> userID.equals(u.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다."));
    }

    @Override
    public List<User> findAll() {
        return List.copyOf(data);
    }


    public List<User> load() {
        List<User> loaded = SaveLoadUtil.load(path);
        if(loaded != null){
            this.data.addAll(loaded);
        }
        return this.data;

    }

    @Override
    public User delete(User user) {
        return null;
    }
}
