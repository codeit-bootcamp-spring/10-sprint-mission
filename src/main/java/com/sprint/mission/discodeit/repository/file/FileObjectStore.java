package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.*;
import lombok.Getter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class FileObjectStore implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private transient Path storagePath;

    private final Map<UUID, User> usersData = new HashMap<>();
    private final Map<UUID, Channel> channelsData = new HashMap<>();
    private final Map<UUID, Message> messagesData = new HashMap<>();
    private final Map<UUID, UserStatus> userStatusesData = new HashMap<>();
    private final Map<UUID, BinaryContent> binaryContentsData = new HashMap<>();
    private final Map<UUID, ReadStatus> readStatusesData = new HashMap<>();

    public FileObjectStore(Path storagePath) {
        this.storagePath = storagePath;
    }

    public void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storagePath.toFile()))) {
            oos.writeObject(this);
        } catch (IOException | IllegalStateException e) {
            System.out.println(e);
        }
    }
    public static FileObjectStore loadData(Path storagePath) {
        // 파일 존재 X
        if (!Files.exists(storagePath)) {
            return new FileObjectStore(storagePath);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storagePath.toFile()))) {
            Object fileData = ois.readObject();
            if (fileData instanceof FileObjectStore) {
                FileObjectStore fileObjectStore = (FileObjectStore) fileData;
                fileObjectStore.storagePath = storagePath;
                return fileObjectStore;
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
        return new FileObjectStore(storagePath); // 파일이 존재하지만 읽기 실패 시
    }
}
