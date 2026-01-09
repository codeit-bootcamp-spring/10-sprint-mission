//package com.sprint.mission.discodeit.jcf;
//
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//
//public class JCFUserService implements UserService {
//    private final Map<UUID, User> data;
//
//    public JCFUserService() {
//        this.data = new HashMap<>();
//    }
//
//    @Override
//    public User create(User user) {
//        data.put(user.getId(), user);
//        return user;
//    }
//
//    @Override
//    public User read(UUID id) {
//        return data.get(id);
//    }
//
//    @Override
//    public List<User> readAll() {
//        return new ArrayList<>(data.values());
//    }
//
//    @Override
//    public User update(User user) {
//        data.put(user.getId(), user);
//        return user;
//    }
//
//    @Override
//    public void delete(UUID uuid) {
//        data.remove(uuid);
//    }
//}
