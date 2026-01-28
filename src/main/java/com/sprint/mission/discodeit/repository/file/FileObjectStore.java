package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileObjectStore implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Path STORAGE_PATH = Path.of("./FileIOTestStorage/objectStorage.ser");

    private final Map<UUID, User> usersData = new HashMap<>();
    private final Map<UUID, Channel> channelsData = new HashMap<>();
    private final Map<UUID, Message> messagesData = new HashMap<>();

    public Map<UUID, User> getUsersData() {
        return usersData;
    }

    public Map<UUID, Channel> getChannelsData() {
        return channelsData;
    }

    public Map<UUID, Message> getMessagesData() {
        return messagesData;
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STORAGE_PATH.toFile()))) {
            oos.writeObject(this);
        } catch (IOException | IllegalStateException e) {
            System.out.println(e);
        }
    }
    public static FileObjectStore loadData() {
        // 파일 존재 X
        if (!Files.exists(STORAGE_PATH)) {
            return new FileObjectStore();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STORAGE_PATH.toFile()))) {
            Object fileData = ois.readObject();
            if (fileData instanceof FileObjectStore) {
                FileObjectStore fileObjectStore = (FileObjectStore) fileData;
                return fileObjectStore;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
        return new FileObjectStore(); // 파일이 존재하지만 읽기 실패 시
    }
}
