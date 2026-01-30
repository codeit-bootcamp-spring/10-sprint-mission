package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelService implements ChannelService {
    private UserService userService;
    private MessageService messageService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public Channel createChannel(String channelName, ChannelType channelType, String description) {
        validateChannelExist(channelName);
        Channel channel = new Channel(channelName, channelType, description);
        save(channel);
        return channel;
    }

    @Override
    public Channel getChannel(UUID channelId) {
        Path channelPath = getChannelPath(channelId);

        if(!channelPath.toFile().exists()) {
            throw new NoSuchElementException("해당 채널이 존재하지 않습니다.");
        }
        try (FileInputStream fis =  new FileInputStream(channelPath.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (Channel) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("채널을 가져오는데 실패했습니다.");
        }
    }

    @Override
    public List<Channel> getAllChannels() {
        Path channelPath = Path.of("channels");
        if(Files.exists(channelPath)) {
            try {
                List<Channel> channels = Files.list(channelPath)
                        .map(path -> {
                            try(
                                    FileInputStream fis = new FileInputStream(path.toFile());
                                    ObjectInputStream ois = new ObjectInputStream(fis)
                            ) {
                                Channel channel = (Channel) ois.readObject();
                                return channel;
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException("모든 채널을 가져오는데 실패했습니다.");
                            }
                        })
                        .toList();
                return channels;
            } catch (IOException e) {
                throw new RuntimeException("모든 채널을 가져오는데 실패했습니다.");
            }
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Channel> getChannelsByUserId(UUID userId) {
        List<Channel> result = new ArrayList<>();
        userService.getUser(userId)
                .getChannelIds()
                .forEach(channelId -> result.add(getChannel(channelId)));
        return result;
    }

    @Override
    public Channel updateChannel(UUID channelId, String channelName, ChannelType channelType, String description) {
        validateChannelExist(channelName);
        Channel updatechannel = getChannel(channelId);
        Optional.ofNullable(channelName)
                .ifPresent(updatechannel::updateChannelName);
        Optional.ofNullable(channelType)
                .ifPresent(updatechannel::updateChannelType);
        Optional.ofNullable(description)
                .ifPresent(updatechannel::updateDescription);
        save(updatechannel);
        return updatechannel;
    }

    @Override
    public void deleteChannel(UUID channelId) {
        Path channelPath = getChannelPath(channelId);
        try {
            Files.delete(channelPath);
        } catch (IOException e) {
            throw new RuntimeException("채널을 삭제하는데 실패했습니다.");
        }
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = getChannel(channelId);
        User user = userService.getUser(userId);
        channel.addUserId(userId);
        user.addChannelId(channelId);
        // save(channel);
        // save(user);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = getChannel(channelId);
        User user = userService.getUser(userId);
        channel.removeUserId(userId);
        user.removeChannelId(channelId);
        // save(channel);
        // save(user);
    }

    private Path getChannelPath(UUID channelId) {
        return Paths.get("channels", channelId.toString() + ".ser");
    }

    private void save(Channel channel) {
        Path channelPath = getChannelPath(channel.getId());
        try (
                FileOutputStream fos = new FileOutputStream(channelPath.toFile());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(channel);
        } catch (IOException e) {
            throw new RuntimeException("채널을 저장하는데 실패했습니다.");
        }
    }

    private void validateChannelExist(String channelName) {
        if(getAllChannels().stream().anyMatch(c -> c.getChannelName().equals(channelName)))
            throw new IllegalStateException("이미 존재하는 채널 이름입니다.");
    }
}
