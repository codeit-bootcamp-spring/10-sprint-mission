package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileChannelRepository implements ChannelRepository {
    private final Map<UUID, Channel> data; // 빠른 조회를 위한 컬렉션
    private final Path channelDir;

    public FileChannelRepository(Path baseDir) {
        this.data = new HashMap<>();
        this.channelDir = baseDir.resolve("channels");
        try {
            // 파일이 저장될 디렉토리가 존재하지 않을 경우 폴더 생성
            Files.createDirectories(channelDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 생성 시 디렉토리와 컬렉션 동기화
        loadAllFromFiles();
    }

    private Path channelFilePath(UUID channelId) {
        // 채널을 구분하기 위한 파일 경로 생성
        return channelDir.resolve("channel-" + channelId + ".ser");
    }

    @Override
    public Channel save(Channel channel) {
        // 경로 생성 (channel-id.ser)
        Path filePath = channelFilePath(channel.getId());

        try (FileOutputStream fos = new FileOutputStream(filePath.toFile());
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            // 파일 저장
            oos.writeObject(channel);
            data.put(channel.getId(), channel);
            return channel;
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 저장을 실패했습니다.");
        }
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return Optional.ofNullable(data.get(channelId));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID channelId) {
        deleteFileAndRemoveFromData(channelId);
    }

    public Channel loadChannelFile(UUID channelId) {
        // 경로 생성 (channel-id.ser)
        Path filePath = channelFilePath(channelId);

        // 파일 존재 여부 확인
        if (!Files.exists(filePath)) {
            throw new RuntimeException("채널 파일이 존재하지 않습니다.");
        }

        try (FileInputStream fis = new FileInputStream(filePath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            // 파일 조회 후 컬렉션과 동기화
            Channel channel = (Channel) ois.readObject();
            data.put(channel.getId(), channel);
            return channel;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널 파일 로딩을 실패했습니다.");
        }
    }

    private void loadAllFromFiles() {
        try {
            Files.list(channelDir)
                    .filter(path -> path.getFileName().toString().startsWith("channel-")) // 파일명이 "channel-"로 시작해야 함
                    .filter(path -> path.getFileName().toString().endsWith(".ser")) // 파일의 확장자가 ".ser"이어야 함
                    .forEach(path -> {
                        try (FileInputStream fis = new FileInputStream(path.toFile());
                             ObjectInputStream ois = new ObjectInputStream(fis)) {
                            // 파일 조회 후 컬렉션에 저장
                            Channel channel = (Channel) ois.readObject();
                            data.put(channel.getId(), channel);
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException("채널 파일 로딩을 실패했습니다.");
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("채널 디렉토리 조회를 실패했습니다.");
        }
    }

    private void deleteFileAndRemoveFromData(UUID channelId) {
        // 경로 생성 (channel-id.ser)
        Path filePath = channelFilePath(channelId);

        try {
            // 파일이 존재한다면 삭제 후 true 반환
            boolean deleted = Files.deleteIfExists(filePath);
            if (!deleted) {
                throw new RuntimeException("채널이 존재하지 않습니다.");
            }
            // 컬렉션에서도 삭제
            data.remove(channelId);
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 삭제를 실패했습니다.");
        }
    }
}
