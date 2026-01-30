package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository implements ChannelRepository {

    private static final Path dirPath = Paths.get(System.getProperty("user.dir") + "/data/channels");

    public FileChannelRepository() {
        init();
    }

    private void init() {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException("채널 데이터 폴더 생성 실패", e);
            }
        }
    }

    @Override
    public Channel save(Channel channel) {
        writeToFile(channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return findAll().stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findAny();
    }

    @Override
    public List<Channel> findAll() {
        if (!Files.exists(dirPath)) {
            return List.of();
        }
        try (Stream<Path> stream = Files.list(dirPath)) {
            return stream
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (Channel) data;
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Channel channel) {
        File file = new File(dirPath.toFile(), channel.getId().toString() + ".ser");

        if (file.exists()) {
            if (!file.delete()) {
                throw new RuntimeException("채널 파일 삭제 실패");
            }
        }
    }

    private void writeToFile(Channel channel) {
        File file = new File(dirPath.toFile(), channel.getId().toString() + ".ser");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("채널 데이터 저장 실패", e);
        }
    }
}
