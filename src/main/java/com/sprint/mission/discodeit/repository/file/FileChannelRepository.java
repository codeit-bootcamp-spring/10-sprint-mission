package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.IOException;
import java.nio.file.Paths;

public class FileChannelRepository extends FileDomainRepository<Channel> implements ChannelRepository {

    public FileChannelRepository() throws IOException {
        super(Paths.get(System.getProperty("user.dir"), "file-data-map", "Channel"),
                ".chn");
    }

    @Override
    public Channel save(Channel channel) throws IOException {
        return save(channel, Channel::getId);
    }
}
