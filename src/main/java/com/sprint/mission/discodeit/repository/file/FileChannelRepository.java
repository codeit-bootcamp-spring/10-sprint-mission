package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.extend.FileSerDe;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository extends FileSerDe<Channel> implements ChannelRepository {
    private final String CHANNEL_DATA_DIRECTORY = "data/channel";

    public FileChannelRepository() {
        super(Channel.class);
    }

    @Override
    public Channel save(Channel channel) {
        return super.save(CHANNEL_DATA_DIRECTORY, channel);
    }

    @Override
    public Optional<Channel> findById(UUID channelId) {
        return super.load(CHANNEL_DATA_DIRECTORY, channelId);
    }

    @Override
    public List<Channel> findAll() {
        return super.loadAll(CHANNEL_DATA_DIRECTORY);
    }

    @Override
    public void deleteById(UUID channelId) {
        super.delete(CHANNEL_DATA_DIRECTORY, channelId);
    }
}
