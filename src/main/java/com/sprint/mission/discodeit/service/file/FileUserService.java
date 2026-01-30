//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.exception.DuplicationEmailException;
//import com.sprint.mission.discodeit.exception.UserNotFoundException;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.io.*;
//import java.util.*;
//
//public class FileUserService implements UserService {
//
//    private static final String FILE_PATH = "users.dat";
//
//    // ======= 영속화 관련 private 메서드 =======
//    private Map<UUID, User> load() {
//        File file = new File(FILE_PATH);
//        if (!file.exists()) return new LinkedHashMap<>();
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
//            return (Map<UUID, User>) ois.readObject();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void save(Map<UUID, User> users) {
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
//            oos.writeObject(users);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    // ======= CRUD =======
//    @Override
//    public User createUser(String userName, String userEmail) {
//        Map<UUID, User> users = load();
//        if (users.values().stream().anyMatch(u -> u.getEmail().equals(userEmail))) {
//            throw new DuplicationEmailException();
//        }
//        User user = new User(userName, userEmail);
//        users.put(user.getId(), user);
//        save(users);
//        return user;
//    }
//
//    @Override
//    public User findUser(UUID userId) {
//        Map<UUID, User> users = load();
//        User user = users.get(userId);
//        if (user == null) throw new UserNotFoundException();
//        return user;
//    }
//
//    @Override
//    public List<User> findAllUser() {
//        return new ArrayList<>(load().values());
//    }
//
//    @Override
//    public User updateUser(UUID userId, String userName, String userEmail) {
//        Map<UUID, User> users = load();
//        User user = users.get(userId);
//        if (user == null) throw new UserNotFoundException();
//
//        user.update(userName, userEmail);
//        save(users);
//        return user;
//    }
//
//    @Override
//    public User deleteUser(UUID userId) {
//        Map<UUID, User> users = load();
//        User removed = users.remove(userId);
//        if (removed == null) throw new UserNotFoundException();
//        save(users);
//        return removed;
//    }
//}
