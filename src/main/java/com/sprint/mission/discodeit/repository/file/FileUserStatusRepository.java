package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.repository.UserStatusRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository("userStatusRepository")
public class FileUserStatusRepository implements UserStatusRepository {
    private List<UserStatus> data;

    public FileUserStatusRepository() {
        this.data = new ArrayList<>();
    }

    // 직렬화
    public void serialize(List<UserStatus> userStatuses) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("userStatusList.ser"))) {
            oos.writeObject(userStatuses);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 역직렬화
    public List<UserStatus> deserialize() {
        List<UserStatus> userStatuses = List.of();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("userStatusList.ser"))) {
            userStatuses = (List<UserStatus>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("역직렬화가 안됨");
        }
        return userStatuses;
    }

    @Override
    public void save(UserStatus userStatus) {
        this.data.add(userStatus);
        serialize(this.data);
    }

    @Override
    public void delete(UUID userStatusId) {
        this.data = deserialize();
        for (UserStatus userStatus : this.data) {
            if (userStatus.getId().equals(userStatusId)) {
                this.data.remove(userStatus);
                serialize(this.data);
                break;
            }
        }
    }

    @Override
    public List<UserStatus> loadAll() {
        return deserialize();
    }

    @Override
    public UserStatus loadById(UUID userId) {
        this.data = deserialize();
        for(UserStatus userStatus : this.data) {
            if (userStatus.getUserId().equals(userId)) {
                return userStatus;
            }
        }
        throw new NoSuchElementException();
    }
}
