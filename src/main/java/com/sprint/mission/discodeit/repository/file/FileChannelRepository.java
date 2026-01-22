package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.util.FileIo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    public static final Path CHANNEL_DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "channels");

    private static ChannelRepository instance;
    private final FileIo<Channel> channelFileIo;
    private final FileIo<User> userFileIo;

    private FileChannelRepository() {
        channelFileIo = new FileIo<>(CHANNEL_DIRECTORY);
        this.channelFileIo.init();
        userFileIo = new FileIo<>(FileUserRepository.USER_DIRECTORY);
        this.userFileIo.init();
    }

    public static ChannelRepository getInstance() {
        if (instance == null) instance = new FileChannelRepository();
        return instance;
    }

    @Override
    public Channel save(Channel channel) {
        channelFileIo.save(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return channelFileIo.load().stream()
            .filter(ch -> ch.getId().equals(channelId))
            .findFirst();
    }

    @Override
    public List<Channel> findAll() {
        return channelFileIo.load();
    }

    @Override
    public void delete(UUID channelId) {
        channelFileIo.delete(channelId);
    }
}
