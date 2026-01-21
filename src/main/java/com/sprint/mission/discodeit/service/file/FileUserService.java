package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private static final String FILE_PATH = "data/users.ser";
    private final List<User> data;

    public FileUserService() {
        this.data = loadUsers();
    }

    private void saveUsers(){
        File file = new File(FILE_PATH);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.data);
            System.out.println("직렬화 완료: users.ser에 저장되었습니다.");
        } catch (IOException e) {
            throw new RuntimeException("유저 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<User> loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object data = ois.readObject();
            System.out.println("역직렬화 완료: " + data);
            return (List<User>) data;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저 데이터 로드 중 오류 발생", e);
        }
    }


    @Override
    public User createUser(String userName, String userEmail) {
        Validators.validationUser(userName, userEmail); // 비즈니스 로직
        validateDuplicationEmail(userEmail); // 비즈니스 로직
        User user = new User(userName, userEmail);
        this.data.add(user);
        saveUsers();// 저장 로직
        return user;
    }

    @Override
    public User readUser(UUID id) {
        return validateExistenceUser(id); // 비즈니스 로직
    }

    @Override
    public List<User> readAllUser() {
        return this.data;
    } // 저장 로직

    @Override
    public User updateUser(UUID id, String userName, String userEmail) {
        User user = validateExistenceUser(id);// 비즈니스 로직

        Optional.ofNullable(userName)
                .ifPresent(name -> {Validators.requireNotBlank(name, "userName");
                    user.updateUserName(name);
                }); //비즈니스 로직 + 저장 로직
        Optional.ofNullable(userEmail)
                .ifPresent(email -> {Validators.requireNotBlank(email, "userEmail");
                    validateDuplicationEmail(email);
                    user.updateUserEmail(email);
                }); // 비즈니스 로직 + 저장 로직
        saveUsers();
        return user;
    }

    @Override
    public void deleteUser(UUID id) {
        User user = validateExistenceUser(id); //비즈니스 로직
        this.data.remove(user);
        saveUsers();// 저장 로직
    }


    @Override
    public List<User> readUsersByChannel(UUID channelId) {
        return this.data.stream()
                .filter(user -> user.getJoinedChannels().stream()
                        .anyMatch(ch -> channelId.equals(ch.getId())))
                .toList();
    } // 비즈니스 로직 + 저장 로직

    private void validateDuplicationEmail(String userEmail) {
        if(this.data.stream()
                .anyMatch(user -> userEmail.equals(user.getUserEmail()))) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }
    } // 비즈니스 로직 + 저장 로직


    private User validateExistenceUser(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다."); // 비즈니스 로직
        return this.data.stream()
                .filter(user -> id.equals(user.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));
    } // 비즈니스 로직 + 저장 로직
}
