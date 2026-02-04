package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
public class FileChannelRepository implements ChannelRepository {
    private final Path channelPath;

    public FileChannelRepository(
            @Value("${discodeit.repository.file-directory:data}") String rootPath
    ) {
        this.channelPath = Paths.get(rootPath, "channels");
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        Path channelPath = getChannelPath(channelId);

        if (!Files.exists(channelPath)) {
            return Optional.empty();
        }

        try (FileInputStream fis =  new FileInputStream(channelPath.toFile());
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return Optional.ofNullable((Channel) ois.readObject());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널을 가져오는데 실패했습니다.");
        }
    }

    @Override
    public Optional<Channel> findByName(String channelName) {
        return findAll().stream()
                .filter(c -> c.getChannelName().equals(channelName))
                .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        Path channelPath = Path.of("channels");
        if(Files.exists(channelPath)) {
            try {
                List<Channel> channels = Files.list(channelPath)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Channel channel = (Channel) ois.readObject();
                                return channel;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException("모든 채널을 가져오는데 실패했습니다.");
                            }
                        })
                        .toList();
                return channels;
            } catch (IOException e) {
                throw new RuntimeException("모든 채널을 가져오는데 실패했습니다.");
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Channel> findAllByUserId(UUID userId) {
        return findAll().stream()
                .filter(c -> c.getUserIds()
                        .stream()
                        .anyMatch(findUserId -> findUserId.equals(userId)))
                .toList();
    }

    @Override
    public void save(Channel channel) {
        Path channelPath = getChannelPath(channel.getId());
        try (
                FileOutputStream fos = new FileOutputStream(channelPath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("채널을 저장하는데 실패했습니다.");
        }
    }

    @Override
    public void deleteById(UUID channelId) {
        Path channelPath = getChannelPath(channelId);
        try {
            Files.delete(channelPath);
        } catch (IOException e) {
            throw new RuntimeException("채널을 삭제하는데 실패했습니다.");
        }
    }

    private Path getChannelPath(UUID channelId) {
        try {
            Files.createDirectories(channelPath);
        } catch (IOException e) {
            throw new IllegalStateException("channels 경로를 만드는데 실패했습니다.");
        }

        return channelPath.resolve(channelId.toString() + ".ser");
    }
}
