//package com.sprint.mission.service.jcf;
//
//import com.sprint.mission.entity.Channel;
//import com.sprint.mission.entity.User;
//import com.sprint.mission.service.UserService;
//
//import java.util.*;
//
//public class JCFUserService implements UserService {
//    private final Map<UUID, User> users;
//
//    public JCFUserService() {
//        this.users = new HashMap<>();
//    }
//
//    @Override
//    public User create(String name, String email) {
//        User user = new User(name, email);
//        users.put(user.getId(), user);
//        return user;
//    }
//
//    @Override
//    public List<Channel> findByUserId(UUID userId) {
//        User user = findById(userId);
//        return user.getChannels();
//    }
//
//    @Override
//    public User findById(UUID id) {
//        validateUserExist(id);
//        return users.get(id);
//    }
//
//    @Override
//    public List<User> findAll() {
//        return new ArrayList<>(users.values());
//    }
//
//    @Override
//    public User update(UUID id, String name) {
//        User user = findById(id);
//        validateChangeNameExist(id, name);
//        user.updateName(name);
//        return user;
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        validateUserExist(id);
//        users.remove(id);
//    }
//
//    private void validateChangeNameExist(UUID id, String name) {
//        String trimmedNickName = name.trim();
//        boolean exist = users.values().stream()
//                .anyMatch(user -> !user.getId().equals(id) && user.getName().equals(name));
//        if (exist) {
//            throw new IllegalArgumentException("존재하는 닉네임입니다. 다른이름을 선택해주세요");
//        }
//    }
//
//    private void validateUserExist(UUID id) {
//        if (!users.containsKey(id)) {
//            throw new IllegalArgumentException("유저가 존재하지 않습니다.");
//        }
//    }
//}
