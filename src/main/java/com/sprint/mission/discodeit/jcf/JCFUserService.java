package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User create(User user) {
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User read(UUID id) {
        if(!data.containsKey(id)){
            throw new NoSuchElementException("조회 실패 : 해당 ID의 사용자를 찾을 수 없습니다.");
        }
        return data.get(id);
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public User update(User user) {
        if (!data.containsKey(user.getId())) {
            throw new NoSuchElementException("수정 실패 : 존재하지 않는 사용자 ID입니다.");
        }
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("삭제 실패 : 존재하지 않는 사용자 ID입니다.");
        }
        data.remove(id);
    }
}
