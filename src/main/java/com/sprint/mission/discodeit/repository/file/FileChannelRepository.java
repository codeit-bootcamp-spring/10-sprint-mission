package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.util.SerializedFileUtils;
import org.springframework.stereotype.Repository;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private static final String FILE_PREFIX = "channel";
    private static final String ENTITY_NAME = "채널";

    private final Map<UUID, Channel> data; // 빠른 조회를 위한 컬렉션
    private final Path channelDir;

    public FileChannelRepository(Path baseDir) {
        this.data = new HashMap<>();
        this.channelDir = baseDir.resolve(FILE_PREFIX);
        try {
            // 파일이 저장될 디렉토리가 존재하지 않을 경우 폴더 생성
            Files.createDirectories(channelDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // 생성 시 디렉토리와 컬렉션 동기화
        loadAllFromDirectory();
    }

    private Path channelFilePath(UUID channelId) {
        // 채널을 구분하기 위한 파일 경로 생성
        return channelDir.resolve(FILE_PREFIX + "-" + channelId + ".ser");
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
        if (!data.containsKey(channelId)) {
            throw new RuntimeException("채널이 존재하지 않습니다.");
        }

        Path filePath = channelFilePath(channelId);
        SerializedFileUtils.deleteFileOrThrow(filePath, ENTITY_NAME);
        data.remove(channelId);
    }

    public Channel loadByIdFromFile(UUID channelId) {
        // 경로 생성 (channel-id.ser)
        Path filePath = channelFilePath(channelId);
        // 파일 역직렬화
        Channel channel = (Channel) SerializedFileUtils.deserialize(filePath, ENTITY_NAME);
        // 컬렉션과 동기화
        data.put(channel.getId(), channel);
        return channel;
    }

    private void loadAllFromDirectory() {
        data.clear();

        for (Object object : SerializedFileUtils.deserializeAll(channelDir, FILE_PREFIX, ENTITY_NAME)) {
            Channel channel = (Channel) object;
            data.put(channel.getId(), channel);
        }
    }
}
