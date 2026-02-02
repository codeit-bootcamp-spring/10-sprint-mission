//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.UserRepository;
//
//import java.util.*;
//
//public class JCFUserRepository implements UserRepository {
//    private final Map<UUID, User> data;
//
//    public JCFUserRepository(){
//        data = new HashMap<>();
//    }
//
//    @Override
//    public User findById(UUID userId) {
//        return data.get(userId);
//    }
//
//    public List<User> findAll(){
//        return new ArrayList<>(data.values());
//    }
//
//    @Override
//    public void save(UUID userId, User user) {
//        data.put(userId, user);
//    }
//
//    public void delete(UUID userId){
//        data.remove(userId);
//    }
//}
