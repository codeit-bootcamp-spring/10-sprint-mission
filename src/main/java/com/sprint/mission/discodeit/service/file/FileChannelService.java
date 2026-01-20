package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

public class FileChannelService extends BaseFileService<Channel>implements ChannelService {


    private MessageService messageService;
    private UserService userService;
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
    public void setUserService(UserService userService) {this.userService = userService;}

    public FileChannelService(Path directory) {
        super(directory);
    }

    @Override
    public void save(Channel channel) {
        super.save(channel);
    }

    @Override
    public Channel createChannel(String name, String description, Channel.ChannelType visibility) {
        Channel newChannel = new Channel(name, description, visibility);
        save(newChannel);
        return newChannel;
    }

    @Override
    public Channel findById(UUID channelId) {
        return super.findById(channelId);
    }

    @Override
    public List<Channel> findAll() {
        return super.findAll();
    }

    @Override
    public Channel updateChannel(UUID channelId, String newName, String description, Channel.ChannelType newVisibility) {
        Channel channel = findById(channelId);

        Optional.ofNullable(newName)
                .filter(name -> !name.equals(channel.getChannelName()))
                .ifPresent(channel::updateName);

        Optional.ofNullable(description)
                .filter(name -> !name.equals(channel.getDescription()))
                .ifPresent(channel::updateDescription);

        Optional.ofNullable(newVisibility)
                .filter(v -> v != channel.getChannelVisibility())
                .ifPresent(channel::updateVisibility);

        save(channel);

        return channel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        if (!Files.exists(getFilePath(channelId))) {
            throw new NoSuchElementException("Channel with id " + channelId + " does not exist");
        }
        Channel channel = findById(channelId);
        messageService.deleteMessagesByChannelId(channelId);
        channel.getUsers().forEach(user -> {
            user.leaveChannel(channel);
            userService.save(user);});

        try {
            Files.delete(getFilePath(channelId));
        } catch (IOException e) {
            throw new RuntimeException("채널 삭제 중 오류 발생: " + channelId,e);
        }

    }
}
