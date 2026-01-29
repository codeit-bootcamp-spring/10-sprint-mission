//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.UserRepository;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.UUID;
//
//public class JCFUserRepository implements UserRepository {
//    private final HashMap<UUID, User> data;
//
//    public JCFUserRepository() {
//        this.data = new HashMap<>();
//    }
//
//    @Override
//    public void save(User user) {
//        data.put(user.getId(), user);
//    }
//
//    @Override
//    public void delete(UUID userId) {
//        this.data.remove(userId);
//    }
//
//    @Override
//    public List<User> loadAll() {
//        return new ArrayList<>(this.data.values());
//    }
//
//}
