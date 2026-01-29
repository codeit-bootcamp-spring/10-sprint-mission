package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileUserService implements UserService {
    private List<User> data;

    public FileUserService() {
        this.data = new ArrayList<>();
    }

    public List<User> getData() {
        return this.data;
    }

    // 직렬화
    public void serialize(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("users.ser"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 역직렬화
    public List<User> deserialize() {
        List<User> newUsers = List.of();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("users.ser"))) {
            newUsers = (List<User>) ois.readObject();
//            System.out.println(newUsers);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("역직렬화가 안됨");
        }
        return newUsers;
    }

    @Override
    public User create(String name) {
        User user = new User(name);
        data.add(user);
        serialize(this.data);
        return user;
    }

    @Override
    public User read(UUID id) {
        for (User user : deserialize()) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public List<User> readAll() {
        return deserialize();
    }

    @Override
    public User updateUserName(UUID id, String name) {
        this.data = deserialize();
        for (User user : this.data) {
            if (user.getId().equals(id)) {
                user.updateName(name);
                serialize(this.data);
                return user;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public void delete(UUID id) {
        this.data = deserialize();
        for (User user : this.data) {
            if (user.getId().equals(id)) {
                this.data.remove(user);
                serialize(this.data);
                break;
            }
        }
    }

    // 특정 채널의 참가한 유저 목록 조회
    public List<User> readChannelUserList(UUID channelId, FileChannelService channelService) {
        Channel channel = channelService.read(channelId);
        return channel.getUserList();
    }
}
