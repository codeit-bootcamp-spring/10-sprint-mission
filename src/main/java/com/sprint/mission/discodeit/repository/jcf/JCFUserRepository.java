package com.sprint.mission.discodeit.repository.jcf;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@Primary
public class JCFUserRepository implements UserRepository {
    private final Map<UUID, User> data = new HashMap<>();

    @Override
    public void save(User user) {
        if (user == null) throw new IllegalArgumentException("user는 null일수 없습니다.");
        if (user.getId() == null) throw new IllegalArgumentException("user.id는 null일 수 없습니다.");

        data.put(user.getId(), user);
    }

    @Override
    public void delete(UUID id){
        if (id == null) throw new IllegalArgumentException("id는 null일 수 없습니다.");
        data.remove(id);
    }

    @Override
    public Optional<User> findById(UUID id){
        if (id == null) throw new IllegalArgumentException("id는 null일 수 없습니다.");

        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<User> findAll(){
        return new ArrayList<>(data.values());
    }


}
