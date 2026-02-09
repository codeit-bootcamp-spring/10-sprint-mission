package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "jcf"
)
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
        return anyMatch(user -> user.matchUsername(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return anyMatch(user -> user.matchEmail(email));
    }

    @Override
    public List<User> findAll() throws IOException, ClassNotFoundException {
        return streamAll(Stream::toList);
    }

}
