package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final HashMap<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }
    public JCFUserService(User user) {
        this.data = new HashMap<>();
        data.put(user.getId(), user);
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> read(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public ArrayList<User> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public User update(UUID id, String name) {
        try {
            this.data.get(id).updateName(name);
            return this.data.get(id);
        } catch (NoSuchElementException e) {
            System.out.println("변경하고자 하는 데이터가 없습니다.");
        } catch (Exception e) {
            System.out.println("잘못된 응답입니다.");
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        try {
            data.remove(id);
        } catch (NoSuchElementException e) {
            System.out.println("삭제할 데이터가 존재하지 않습니다.");
        } catch (Exception e) {
            System.out.println("데이터가 존재하지 않습니다.");
        }
    }
}
