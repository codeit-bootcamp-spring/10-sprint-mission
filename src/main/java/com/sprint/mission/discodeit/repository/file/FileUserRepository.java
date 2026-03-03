package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileUserRepository extends FileDomainRepository<User> implements UserRepository {

    public FileUserRepository() {
        super(Paths.get(System.getProperty("user.dir"), "file-data-map", "User"),
                ".user");
    }

    @Override
    public User save(User user) {
        return save(user, User::getId);
    }

    @Override
    public List<User> findAll() {
        return streamAll(Stream::toList);
    }

    @Override
    public boolean existsByUsername(String username) {
        return anyMatch(user -> user.matchUsername(username));
    }

    @Override
    public boolean existsByEmail(String email) {
        return anyMatch(user -> user.matchEmail(email));
    }
}
