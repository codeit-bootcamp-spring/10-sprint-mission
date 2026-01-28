package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;


public class FileUserService implements UserService {
    private final String FILE_PATH = "users.dat";
    private MessageService messageService;

    public void setMessageService(MessageService messageService) { this.messageService = messageService;}

    private Map<UUID, User> loadData(){
        File file = new File(FILE_PATH);
        if(!file.exists()) return new HashMap<>();

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))){
            return (Map<UUID, User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new HashMap<>();
        }
    }

    private void saveData(Map<UUID, User> data){
        File file = new File(FILE_PATH);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))){
            oos.writeObject(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User createUser(String name, String email) {
        validateUserInput(name, email);
        Map<UUID, User> data = loadData();
        User user = new User(name, email);
        data.put(user.getId(), user);
        saveData(data);
        return user;
    }

    @Override
    public User getUser(UUID id) {
        Map<UUID, User> data = loadData();
        return data.get(id);
    }

    @Override
    public List<User> getAllUsers() {
        Map<UUID, User> data = loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateUser(UUID id, String name, String email) {
       Map<UUID, User> data = loadData();
       User user = validateUserId(id, data);
        Optional.ofNullable(name)
                .filter(n -> !n.isBlank())
                .ifPresent(user::updateName);
       Optional.ofNullable(email)
               .filter(e -> !e.isBlank())
               .ifPresent(user::updateEmail);
       saveData(data);
       return user;
    }

    @Override
    public void deleteUser(UUID id) {
        Map<UUID, User> data = loadData();
        User user = validateUserId(id, data);
        if (messageService != null) {
            new ArrayList<>(user.getMessages()).forEach(m -> messageService.deleteMessage(m.getId()));
        }
        user.getChannels().forEach(channel -> channel.removeUser(user));
        data.remove(id);
        saveData(data);
    }

    private void validateUserInput(String name, String email) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("이름은 필수입니다.");
        if (email == null || email.isBlank()) throw new IllegalArgumentException("이메일은 필수입니다.");
    }

    private User validateUserId(UUID id) {
        return validateUserId(id, loadData());
    }

    private User validateUserId(UUID id, Map<UUID, User> data) {
        return Optional.ofNullable(data.get(id))
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저 입니다."));
    }
}
