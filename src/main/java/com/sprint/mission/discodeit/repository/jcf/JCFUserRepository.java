package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class JCFUserRepository extends JCFDomainRepository<User> implements UserRepository {

    public JCFUserRepository() {
        super(new HashMap<>());
    }

    @Override
    public User save(User user) {
        getData().put(user.getId(), user);
        return user;
    }

    @Override
    public boolean existsByUsername(String username) {
        return getData().values()
                .stream()
                .anyMatch(user -> user.matchUsername(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return getData().values()
                .stream()
                .anyMatch(user -> user.matchUsername(email));
    }
}
