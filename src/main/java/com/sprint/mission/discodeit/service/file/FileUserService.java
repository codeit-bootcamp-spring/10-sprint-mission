//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class FileUserService implements UserService {
//    private ChannelService channelService;
//    private MessageService messageService;
//
//    private static final String FILE_PATH = "users.dat";
//    private final List<User> users;
//
//    public FileUserService() {
//        this.users = loadFromFile();
//    }
//
//    public void setChannelService(ChannelService channelService) {
//        this.channelService = channelService;
//    }
//
//    public void setMessageService(MessageService messageService) {
//        this.messageService = messageService;
//    }
//
//    @Override
//    public void save(User user) {
//        saveToFile();
//    }
//
//    @Override
//    public User create(String userName, String userEmail) {
//        validateDuplicateEmail(userEmail);
//        User newUser = new User(userName, userEmail);
//        users.add(newUser);
//        saveToFile();
//        return newUser;
//    }
//
//    @Override
//    public User findById(UUID uuid) {
//        return users.stream()
//                .filter(u -> u.getId().equals(uuid))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다. ID: " + uuid));
//    }
//
//    @Override
//    public List<User> findAll() {
//        return new ArrayList<>(users);
//    }
//
//    @Override
//    public List<User> findMembers(UUID channelId) {
//        return channelService.findById(channelId).getMembers();
//    }
//
//    @Override
//    public User update(UUID id, String userNickname) {
//        User user = findById(id);
//        user.updateNickname(userNickname);
//        saveToFile();
//        return user;
//    }
//
//    @Override
//    public void deleteUserByUserId(UUID userId) {
//        User user = findById(userId);
//        messageService.deleteMessagesByUserId(userId);
//        new ArrayList<>(user.getMyChannels()).forEach(channel -> {
//            channel.removeMember(user);
//        });
//        users.remove(user);
//        saveToFile();
//    }
//
//    // 이메일 중복 시 예외를 던져 가입 중단 (Fail-Fast)
//    private void validateDuplicateEmail(String userEmail) {
//
//        boolean exists = users.stream()
//                .anyMatch(existingUser ->
//                        existingUser.getUserEmail()
//                                .equals(userEmail));
//        if (exists) {
//            throw new IllegalArgumentException("이미 존재하는 이메일입니다: " + userEmail);
//        }
//    }
//
//    // 직렬화
//    private void saveToFile() {
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
//            oos.writeObject(users);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 역직렬화
//    @SuppressWarnings("unchecked")
//    private List<User> loadFromFile() {
//        File file = new File(FILE_PATH);
//        if (!file.exists()) {
//            return new ArrayList<>();
//        }
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
//            return (List<User>) ois.readObject();
//        } catch (IOException | ClassNotFoundException e) {
//            return new ArrayList<>();
//        }
//    }
//}