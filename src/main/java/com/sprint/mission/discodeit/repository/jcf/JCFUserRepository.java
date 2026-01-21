package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> userMap = new HashMap<>();

    @Override
    public User save(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(UUID id){
        userMap.remove(id);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public List<User> findAll(){
        return new ArrayList<>(userMap.values());
    }

    @Override
    public List<User> findByChannelId(UUID channelId) {
        return userMap.values().stream()
                      .filter(user -> user.getMyChannels().stream()
                                          .anyMatch(channel -> channel.getId().equals(channelId)))
                      .toList();
    }
}
