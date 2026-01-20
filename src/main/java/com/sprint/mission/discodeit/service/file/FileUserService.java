package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;
import java.util.UUID;

public class FileUserService implements UserService {
    private static final String FILE_PATH = "users.ser";
    private ChannelService channelService;

    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    private Map<UUID, User> data;

    public FileUserService() {
        this.data = loadUsers();
    }

    @Override
    public User createUser(String username) {
        User user = new User(username);
        data.put(user.getId(), user);
        saveUsers();

        return user;
    }

    @Override
    public List<User> getUserList() {
        return data.values().stream()
                .toList();
    }

    @Override
    public List<User> getUsersByChannel(UUID channelId) {
        // 채널이 존재하지 않을 경우 예외 처리
        if (channelService.getChannelInfoById(channelId) == null) {
            throw new NoSuchElementException("해당 id를 가진 채널이 존재하지 않습니다.");
        }

        return data.values().stream()
                .filter(user -> user.getJoinedChannels().stream()
                        .anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public User getUserInfoByUserId(UUID userId) {
        return findUserById(userId);
    }

    // id 를 기준으로 수정
    @Override
    public User updateUserName(UUID userId, String newName) {
        Objects.requireNonNull(newName, "username은 null일 수 없습니다.");

        User user = findUserById(userId);

        user.updateUsername(newName);
        saveUsers();

        return user;
    }

    @Override
    public void deleteUser(UUID userId) {
        User user = findUserById(userId);

        data.remove(userId);
        saveUsers();
    }

    private User findUserById(UUID userId) {
        Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");

        User user = data.get(userId);

        if (user == null) {
            throw new NoSuchElementException("해당 id를 가진 유저가 존재하지 않습니다.");
        }

        return user;
    }

    @SuppressWarnings("unchecked")
    private Map<UUID, User> loadUsers() {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, User>) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException("사용자 데이터 로드 실패", e);
        }
    }

    private void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
