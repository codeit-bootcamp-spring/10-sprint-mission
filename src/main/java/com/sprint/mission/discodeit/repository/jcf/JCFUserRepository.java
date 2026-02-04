package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
@ConditionalOnProperty(name = "repository.type", havingValue = "jcf")
public class JCFUserRepository implements UserRepository {
    private List<User> userData;

    public JCFUserRepository() {
        userData = new ArrayList<>();
    }

    @Override
    public Optional<User> find(UUID userID) {
        return userData.stream()
                .filter(user -> user.getId().equals(userID))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return userData;
    }


    @Override
    public void deleteUser(User user) {
        userData.remove(user);
    }

    @Override
    public User save(User user){
        userData.removeIf(ch -> ch.getId().equals(user.getId()));
        userData.add(user);
        return user;
    }
}
