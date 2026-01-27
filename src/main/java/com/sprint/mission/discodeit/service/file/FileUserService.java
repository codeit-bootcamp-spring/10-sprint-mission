package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileUserService implements UserService {
    private ChannelService channelService;
    private MessageService messageService;

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public User createUser(String userName, String password, String email) {
        validateUserExist(userName);
        User user = new User(userName, password, email);
        save(user);
        return user;
    }

    @Override
    public User getUser(UUID userId) {
        Path userPath = getUserPath(userId);

        if(!Files.exists(userPath)) {
            throw new NoSuchElementException();
        }
        try (
                FileInputStream fis = new FileInputStream(userPath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
        ) {
            User user = (User) ois.readObject();
            return user;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("유저를 가져오는데 실패했습니다.");
        }
    }

    @Override
    public List<User> getAllUsers() {
        Path userPath = Path.of("./users");
        if(Files.exists(userPath)) {
            try {
                List<User> users = Files.list(userPath)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                User user = (User) ois.readObject();
                                return user;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException("모든 유저를 가져오는데 실패했습니다.");
                            }
                        })
                        .toList();
                return users;
            } catch (IOException e) {
                throw new RuntimeException("모든 유저를 가져오는데 실패했습니다.");
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<User> getUsersByChannelId(UUID channelId) {
        List<User> result = new ArrayList<>();
        channelService.getChannel(channelId)
                .getUserIds()
                .forEach(userId -> result.add(getUser(userId)));
        return result;
    }

    @Override
    public User updateUser(UUID userId, String userName, String password, String email) {
        validateUserExist(userName);
        User updateUser = getUser(userId);
        Optional.ofNullable(userName)
                .ifPresent(updateUser::updateUserName);
        Optional.ofNullable(password)
                .ifPresent(updateUser::updatePassword);
        Optional.ofNullable(email)
                .ifPresent(updateUser::updateEmail);
        save(updateUser);
        return updateUser;
    }

    @Override
    public void deleteUser(UUID userId) {
        Path userPath = getUserPath(userId);
        try {
            Files.delete(userPath);
        } catch (IOException e) {
            throw new RuntimeException("유저를 삭제하는데 실패했습니다.");
        }
    }

    private Path getUserPath(UUID userId) {
        return Paths.get("users", userId.toString() + ".ser");
    }

    private void save(User user) {
        Path userPath = getUserPath(user.getId());
        try (
                FileOutputStream fos = new FileOutputStream(userPath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(user);
        } catch (IOException e) {
            throw new RuntimeException("유저를 저장하는데 실패했습니다.");
        }
    }

    private void validateUserExist(String userName) {
        if(getAllUsers().stream().anyMatch(u -> u.getUserName().equals(userName)))
            throw new IllegalStateException("이미 존재하는 사용자 이름입니다.");
    }
}
