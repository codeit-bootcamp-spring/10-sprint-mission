package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {

    @Override
    public void save(Channel channel) {

    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Channel> findAll() {
        return List.of();
    }

    @Override
    public void delete(UUID id) {

    }

    @Override
    public void deleteAll() {

    }
}
