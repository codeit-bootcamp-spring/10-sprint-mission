package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private final Path directory = Paths.get(System.getProperty("user.dir"), "data", "channels");;

    public FileChannelRepository() {
        FileUtil.init(directory);
    }

    // 채널 저장
    @Override
    public void save(Channel channel) {
        Path filePath = directory.resolve(channel.getId() + ".ser");
        FileUtil.save(filePath, channel);
    }

    // 채널 단건 조회
    @Override
    public Optional<Channel> findById(UUID channelId) {
        Channel channel = FileUtil.loadSingle(directory.resolve(channelId + ".ser"));
        return Optional.ofNullable(channel);
    }

    // 채널 전체 조회
    @Override
    public List<Channel> findAll() {
        return FileUtil.load(directory);
    }

    // 채널 삭제
    @Override
    public void delete(Channel channel) {
        try {
            Files.deleteIfExists(directory.resolve(channel.getId() + ".ser"));
        } catch (IOException e) {
            throw new RuntimeException("[삭제 실패] 시스템 오류가 발생했습니다.", e);
        }
    }
}
