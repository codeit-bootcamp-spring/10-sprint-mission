package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileChannelRepository implements ChannelRepository {
    private final Path DIRECTORY;
    private final String EXTENSION = ".ser";

    public FileChannelRepository() {
        this.DIRECTORY = Paths.get(System.getProperty("user.dir"),
                "file-data-map", Channel.class.getSimpleName());
        if (Files.notExists(DIRECTORY)) {
            try {
                Files.createDirectories(DIRECTORY);
            } catch (IOException e) {
                throw new RuntimeException("채널 디렉토리 생성 실패", e);
            }
        }
    }

    private Path resolvePath(UUID id) {
        return DIRECTORY.resolve(id +  EXTENSION);
    }

    @Override
    public Channel save(Channel channel) {
        Path path = resolvePath(channel.getId());
        try (ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(path.toFile()))) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 실패", e);
        }
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return readChannelFromFile(resolvePath(id));
    }

    @Override
    public List<Channel> findAll() {
        try {
            return Files.list(DIRECTORY)
                    .filter(path -> path.toString().endsWith(EXTENSION))
                    .map(this::readChannelFromFile)
                    .flatMap(Optional::stream)
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }

    @Override
    public boolean existsById(UUID id) {
        return Files.exists(resolvePath(id));
    }

    @Override
    public void deleteById(UUID id) {
        try {
            Files.deleteIfExists(resolvePath(id));
        } catch (IOException e) {
            throw new RuntimeException("채널 삭제 실패", e);
        }
    }

    // [헬퍼 메서드] findId, findAll: 중복되는 역직렬화 로직 통합
    private Optional<Channel> readChannelFromFile(Path path) {
        if (Files.notExists(path)) return Optional.empty();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path.toFile()))) {
            return Optional.ofNullable((Channel) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            return Optional.empty();
        }
    }
}
