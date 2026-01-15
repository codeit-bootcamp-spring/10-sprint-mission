//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.entity.UserStatus;
//import com.sprint.mission.discodeit.service.ClearMemory;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class FileUserService extends AbstractFileService implements UserService, ClearMemory {
//
//    public FileUserService(String path) {
//        super(path);
//    }
//
//    private void save(User user) {
//        Map<UUID, User> data = load();
//
//        if (data.containsKey(user.getId())) {
//            User existing = data.get(user.getId());
//            existing.updateName(user.getName());
//            existing.updateStatus(user.getStatus());
//            existing.updateUpdatedAt(System.currentTimeMillis());
//            data.put(existing.getId(), existing);
//        } else {
//            data.put(user.getId(), user);
//        }
//        writeToFile(data);
//    }
//
//    @Override
//    public User create(String name, UserStatus status) {
//        Map<UUID, User> data = load();
//        boolean isDuplicate = data.values().stream()
//                .anyMatch(user -> user.getName().equals(name));
//        if (isDuplicate) {
//            throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
//        }
//        User user = new User(name, status);
//        save(user);
//        return user;
//    }
//
//    @Override
//    public User findById(UUID id) {
//        Map<UUID, User> data = load();
//        validateExistence(data, id);
//        return data.get(id);
//    }
//
//    @Override
//    public List<User> readAll() {
//        Map<UUID, User> data = load();
//        return List.copyOf(data.values());
//    }
//
//    @Override
//    public User update(UUID id, String newName, UserStatus newStatus) {
//        Map<UUID, User> data = load();
//        validateExistence(data, id);
//        User user = findById(id);
//        save(user);
//        return user;
//    }
//
//    @Override
//    public List<Message> getUserMessages(UUID id) {
//        User user = findById(id);
//        String allMessages = user.getMessages().stream()
//                .map(Message::getContent)
//                .collect(Collectors.joining("\n"));
//
//        System.out.println("[" + user.getName() + "이 보낸 메시지 내역]\n" + allMessages);
//
//    }
//
//    @Override
//    public void delete(UUID id) {
//        remove(id);
//    }
//
//    private void remove(UUID id) {
//        Map<UUID, User> data = load();
//        validateExistence(data, id);
//        data.remove(id);
//        writeToFile(data);
//    }
//    @Override
//    public  List<Channel> getUserChannels(UUID id) {
//        User user = findById(id);
//        String result = user.getChannels().stream()
//                .map(Channel::getName)
//                .collect(Collectors.joining("\n"));
//        System.out.println("[" + user.getName() + "님의 채널들]");
//        if(result.isEmpty()){
//            System.out.println("보유 채널이 없습니다.");
//        }
//        else {
//            System.out.println(result);
//        }
//    }
//    @Override
//    public void clear() {
//        writeToFile(new HashMap<UUID, User>());
//    }
//
//    private void validateExistence(Map<UUID, User> data, UUID id){
//        if (!data.containsKey(id)) {
//            throw new NoSuchElementException("실패 : 존재하지 않는 사용자 ID입니다.");
//        }
//    }
//}
