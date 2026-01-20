package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.utils.Validation;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.*;

public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    public BasicChannelService(ChannelRepository channelRepository){
        this.channelRepository = channelRepository;
    }
    @Override
    public Channel createChannel(String channelName) {
        Validation.notBlank(channelName, "채널 이름");
        Validation.noDuplicate(
                channelRepository.findAll(),
                ch -> ch.getChannelName().equals(channelName),
                "이미 존재하는 채널명입니다: " + channelName
        );
        Channel channel = new Channel(channelName);
        channelRepository.save(channel);
        return channel;
    }
    @Override
    public List<Channel> getChannelAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel findChannelById(UUID id) {
        return channelRepository.findById(id);
    }
    @Override
    public Channel getChannelByName(String channelName) {
        return channelRepository.findAll().stream()
                .filter(ch -> ch.getChannelName().equals(channelName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("채널이 존재하지 않습니다: " + channelName));
    }
    @Override
    public void deleteChannel(UUID uuid) {
        channelRepository.delete(uuid);
    }

    @Override
    public Channel updateChannel(UUID uuid, String newName) {
        Channel existing = channelRepository.findById(uuid);
        existing.update(newName);
        channelRepository.save(existing);
        return existing;
    }



}

