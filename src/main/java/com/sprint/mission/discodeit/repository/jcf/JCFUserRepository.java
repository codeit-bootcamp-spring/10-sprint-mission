package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.util.Validators;

import java.util.*;
import java.util.stream.IntStream;

public class JCFUserRepository implements UserRepository {
    final ArrayList<User> data;

    public JCFUserRepository() {
        this.data = new ArrayList<>();
    }

    @Override
    public User save(User user) {
        OptionalInt indexOpt = IntStream.range(0, this.data.size())
                .filter(i -> data.get(i).getId().equals(user.getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            data.set(indexOpt.getAsInt(), user);
        } else {
            data.add(user);
        }

        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        return data.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }


    @Override
    public List<User> findAll() {
        return new ArrayList<>(data);
    } // 저장 로직


    @Override
    public void deleteById(UUID id) {
        data.removeIf(user -> user.getId().equals(id));
    }
}



