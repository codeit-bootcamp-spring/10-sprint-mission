package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.UUID;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    private JCFUserService(){
        this.data = new HashMap<>();
    }

    @Override
    public void create(User user){
        data.put(user.getId(), user);
    }

    @Override
    public User readById(UUID id){
        return data.get(id);
    }

    @Override
    public List<User> readAll(){
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(User user){
        User existingUser = data.get(user.getId());
        if (existingUser != null){
            existingUser.update(user.getName(), user.getEmail());
        }
    }

    @Override
    public void delete(UUID id){
        data.remove(id);
    }
}
