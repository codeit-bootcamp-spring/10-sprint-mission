package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private MessageService messageService;
    private UserService userService;

    // 생성자를 통해 레포지토리 주입
    public BasicChannelService(ChannelRepository channelRepository) {
        this.channelRepository = channelRepository;
    }

    // 서비스 간 순환 참조 방지를 위한 Setter 주입
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void save(Channel channel) {
        channelRepository.save(channel);
    }

    @Override
    public Channel createChannel(String name, String description, Channel.ChannelType visibility) {
        Channel newChannel = new Channel(name, description, visibility);
        return channelRepository.save(newChannel);
    }

    @Override
    public Channel findById(UUID channelId) {
        return channelRepository.findById(channelId)
                .orElseThrow(() -> new NoSuchElementException("Channel with id " + channelId + " does not exist"));
    }

    @Override
    public List<Channel> findAll() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID channelId, String newName, String description, Channel.ChannelType newVisibility) {
        Channel channel = findById(channelId);

        Optional.ofNullable(newName)
                .filter(name -> !name.equals(channel.getChannelName()))
                .ifPresent(channel::updateName);

        Optional.ofNullable(description)
                .filter(desc -> !desc.equals(channel.getDescription()))
                .ifPresent(channel::updateDescription);

        Optional.ofNullable(newVisibility)
                .filter(v -> v != channel.getChannelVisibility())
                .ifPresent(channel::updateVisibility);

        channelRepository.save(channel);
        syncChannelChanges(channel);

        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        if (!channelRepository.existsById(channelId)) {
            throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
        }

        Channel channel = findById(channelId);

        if (messageService != null) {
            messageService.findMessagesByChannel(channelId)
                    .forEach(msg -> messageService.deleteMessage(msg.getId()));
        }

        if (userService != null) {
            channel.getUsers().forEach(user -> {
                User lastestUser = userService.findById(user.getId());
                lastestUser.leaveChannel(channel);
                userService.save(lastestUser); // 유저 정보 업데이트
            });
        }

        channelRepository.deleteById(channelId);
    }

    // Helper
    private void syncChannelChanges(Channel channel) {
        // 1. 채널에 속한 모든 메시지 업데이트
        messageService.findMessagesByChannel(channel.getId()).forEach(msg -> {
            msg.updateChannel(channel);
            messageService.save(msg);
        });

        // 2. 채널에 가입한 모든 유저의 채널 목록 업데이트
        channel.getUsers().forEach(user -> {
            User lastedUser = userService.findById(user.getId());
            lastedUser.updateChannelInSet(channel);
            userService.save(lastedUser);
        });
    }
}