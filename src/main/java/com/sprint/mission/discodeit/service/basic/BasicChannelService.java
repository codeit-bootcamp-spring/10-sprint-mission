package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
    private final ChannelRepository channelRepository;

    private final UserService userService;
    private MessageService messageService;

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void save(Channel channel) {
        channelRepository.save(channel);
    }

    @Override
    public Channel create(String name, String description, ChannelType type) {
        Channel newChannel = new Channel(name, description, type);
        return channelRepository.save(newChannel);
    }

    @Override
    public Channel findById(UUID id) {
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다. ID: "  + id));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public List<Channel> findChannelsByUserId(UUID userId) {
        User user = userService.findById(userId);
        return user.getChannels();
    }

    @Override
    public Channel update(UUID id, String name, String description) {
        Channel updateChannel = findById(id);
        updateChannel.update(name, description);
        return channelRepository.save(updateChannel);
    }

    public void addChannelByUserId(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        channel.addMember(user);
        user.addChannel(channel);
        channelRepository.save(channel);
        userService.save(user);
    }

    @Override
    public void removeChannelByUserId(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        channel.removeMember(user);
        user.removeChannel(channel);
        channelRepository.save(channel);
        userService.save(user);
    }

    @Override
    public void deleteChannelByChannelId(UUID channelId) {
        Channel channel = findById(channelId);
        messageService.deleteMessagesByChannelId(channelId);
        new ArrayList<>(channel.getMembers()).forEach(user -> {
            user.removeChannel(channel);
            userService.save(user);
        });
        channelRepository.deleteById(channelId);
    }
}
