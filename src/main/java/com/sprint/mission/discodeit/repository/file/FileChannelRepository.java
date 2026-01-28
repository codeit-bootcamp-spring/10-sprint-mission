package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public Channel findChannelById(UUID channelId) {
        return findAllChannel().stream()
                .filter(channel -> channel.getId().equals(channelId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
    }

    @Override
    public List<Channel> findAllChannel() {
        if(!Files.exists(dirPath)) {
            return new ArrayList<>();
        }
        try {
            List<Channel> list = Files.list(dirPath)
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
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Channel channel) {
        File file = new File(dirPath.toFile(), channel.getId().toString() + ".ser");
        if (file.exists()) {
            file.delete();
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
