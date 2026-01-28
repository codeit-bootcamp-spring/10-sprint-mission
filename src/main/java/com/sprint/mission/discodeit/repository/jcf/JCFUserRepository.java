package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User>userStore;

    public JCFUserRepository(){
        userStore = new HashMap<>();
    }

    @Override
    public User save(User user) {
        userStore.put(user.getId(), user);
        return user;
    }

    @Override
    public User findById(UUID id) {
        User user = userStore.get(id);
        if(user == null){
            throw new IllegalArgumentException("해당 유저를 찾을 수 없습니다");
        }

        return user;
    }

    public List<User> findAll(){
        return new ArrayList<>(userStore.values());
    }

    @Override
    public void delete(UUID id) {
        userStore.remove(id);
    }
}
