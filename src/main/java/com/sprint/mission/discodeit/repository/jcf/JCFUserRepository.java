package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JCFUserRepository implements UserRepository {
    private static JCFUserRepository instance = null;
    public static JCFUserRepository getInstance(){
        if(instance == null){
            instance = new JCFUserRepository();
        }
        return instance;
    }
    private JCFUserRepository(){}
    private Set<User> users = new HashSet<>();
    @Override
    public void fileSave(Set<User> users) {
        this.users = users;
    }

    @Override
    public Set<User> fileLoadAll() {
        return users;
    }

    @Override
    public User fileLoad(UUID id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("User not found: id = " + id));
    }

    @Override
    public void fileDelete(UUID id) {
        users.removeIf(user -> user.getId().equals(id));
    }
}
