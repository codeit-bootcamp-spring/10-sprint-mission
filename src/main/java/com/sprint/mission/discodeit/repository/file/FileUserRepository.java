package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository("userRepository")
public class FileUserRepository implements UserRepository {
    private List<User> data;

    public FileUserRepository() {
        this.data = new ArrayList<>();
    }

    // 직렬화
    public void serialize(List<User> users) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("userList.ser"))) {
            oos.writeObject(users);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 역직렬화
    public List<User> deserialize() {
        List<User> newUsers = List.of();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("userList.ser"))) {
            newUsers = (List<User>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("역직렬화가 안됨");
        }
        return newUsers;
    }

    @Override
    public void save(User user) {
        this.data.add(user);
        serialize(this.data);
    }

    @Override
    public void delete(UUID userId) {
        this.data = deserialize();
        for (User user : this.data) {
            if (user.getId().equals(userId)) {
                this.data.remove(user);
                serialize(this.data);
                break;
            }
        }
    }

    public User updateUserName(UUID userId, String name) {
        this.data = deserialize();
        for (User user : this.data) {
            if (user.getId().equals(userId)) {
                user.updateName(name);
                serialize(this.data);
                return user;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public List<User> loadAll() {
        return deserialize();
    }

    @Override
    public User loadById(UUID userId) {
        this.data = deserialize();
        for(User user : this.data) {
            if (user.getId().equals(userId)) {
                return user;
            }
        }
        throw new NoSuchElementException();
    }
}
