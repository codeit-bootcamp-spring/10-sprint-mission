package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import com.sprint.mission.discodeit.utils.FileLockProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileChannelRepository extends FileRepositoryLockSupport implements ChannelRepository {

    private static final Path CHANNEL_DIRECTORY =
            FileIOHelper.resolveDirectory("channels");

    public FileChannelRepository(FileLockProvider fileLockProvider) {
        super(fileLockProvider);
    }

    @Override
    public void save(Channel channel) {
        Path channelFilePath = CHANNEL_DIRECTORY.resolve(channel.getId().toString());

        withLock(CHANNEL_DIRECTORY, () -> FileIOHelper.save(channelFilePath, channel));
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        Path channelFilePath = CHANNEL_DIRECTORY.resolve(id.toString());

        return withLock(CHANNEL_DIRECTORY, () -> FileIOHelper.load(channelFilePath));
    }

    @Override
    public void delete(Channel channel) {
        Path channelFilePath = CHANNEL_DIRECTORY.resolve(channel.getId().toString());

        withLock(CHANNEL_DIRECTORY, () -> FileIOHelper.delete(channelFilePath));
    }

    @Override
    public List<Channel> findVisibleChannels(UUID requesterId) {
        return withLock(CHANNEL_DIRECTORY, () -> FileIOHelper.<Channel>loadAll(CHANNEL_DIRECTORY).stream()
                .filter(channel -> channel.isPublic()
                        || (channel.isPrivate() && channel.hasMember(requesterId)))
                .toList());
    }

    @Override
    public boolean existsById(UUID channelId) {
        Path channelFilePath = CHANNEL_DIRECTORY.resolve(channelId.toString());

        return withLock(CHANNEL_DIRECTORY, () -> FileIOHelper.exists(channelFilePath));
    }
}
