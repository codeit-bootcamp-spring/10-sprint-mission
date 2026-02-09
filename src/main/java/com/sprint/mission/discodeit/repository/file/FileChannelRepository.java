package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    private static final Path CHANNEL_DIRECTORY =
            FileIOHelper.resolveDirectory("channels");

    @Override
    public void save(Channel channel) {
        Path channelFilePath = CHANNEL_DIRECTORY.resolve(channel.getId().toString());

        FileIOHelper.save(channelFilePath, channel);
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Path channelFilePath = CHANNEL_DIRECTORY.resolve(id.toString());

        return FileIOHelper.load(channelFilePath);
    }

    @Override
    public void delete(Channel channel) {
        Path channelFilePath = CHANNEL_DIRECTORY.resolve(channel.getId().toString());

        FileIOHelper.delete(channelFilePath);
    }

    @Override
    public List<Channel> findVisibleChannels(UUID requesterId) {
        List<Channel> channels = FileIOHelper.loadAll(CHANNEL_DIRECTORY);

        return channels.stream()
                .filter(channel ->
                        channel.isPublic()
                                || (channel.isPrivate() && channel.hasMember(requesterId))
                )
                .toList();
    }

    @Override
    public boolean existsById(UUID channelId) {
        Path channelFilePath = CHANNEL_DIRECTORY.resolve(channelId.toString());
        return FileIOHelper.exists(channelFilePath);
    }
}
