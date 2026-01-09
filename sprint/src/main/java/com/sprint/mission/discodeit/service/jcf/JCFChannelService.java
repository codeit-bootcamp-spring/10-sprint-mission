package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public class JCFChannelService implements ChannelService {

    private final Map<UUID, Channel> data;

    public JCFChannelService() {
        data = new HashMap<>();
    }

    @Override
    public UUID createChannel(String title, String description) {
        Channel rt = new Channel(title, description);
        UUID id = rt.getId();
        data.put(id, rt);

        return id;
    }

    @Override
    public void updateChannel(UUID id, String title, String description) {
        Channel channel = getChannel(id);
        if (channel == null) {
            throw new NotFoundException("Channel " + id.toString() + "을 찾을 수 없었습니다.");
        }
        channel.updateTitle(title);
        channel.updateDescription(description);
    }

    @Override
    public void deleteChannel(UUID id) {
        Channel target = data.remove(id);
        if (target == null) {
            throw new NotFoundException("Channel " + id.toString() + "은 이미 삭제되었거나 없는 channel입니다.");
        }
        target.updateIsDeleted(true);
    }

    @Override
    public Channel getChannel(UUID id) {
        return data.get(id);
    }

    @Override
    public Channel getChannel(String title) {
        Optional<Map.Entry<UUID, Channel>> result = data.entrySet().stream()
                .filter(ch -> ch.getValue().getTitle().equals(title))
                .findFirst();
        return result.map(Map.Entry::getValue).orElse(null);
    }

    @Override
    public Iterable<Channel> getAllChannels() {
        return data.values();
    }
}
