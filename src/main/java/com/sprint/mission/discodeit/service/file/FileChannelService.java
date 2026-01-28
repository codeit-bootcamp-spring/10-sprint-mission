package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private final UserService userService;
    private final ChannelRepository channelRepo;
    private final Map<UUID, Set<UUID>> channelUsers = new HashMap<>();
    private final Map<UUID, Set<UUID>> channelMessages = new HashMap<>();


    private final File file = new File("channels.dat");

    public FileChannelService(UserService userService, ChannelRepository channelRepo)
    {   this.userService = userService;
        this.channelRepo = channelRepo;
    }

    public Channel createChannel(String name, String intro) {
        List<Channel> channel = channelRepo.findAll();
        if(channel.stream()
                .anyMatch(c -> c.getName().equals(name))){
            throw new IllegalArgumentException("이미 존재하는 채널명 입니다");
        }

        Channel newChannel = new Channel(name,intro);
        channelRepo.save(newChannel);
        return newChannel;
    }

    public Channel findChannelById(UUID channelId) {
        Channel channel = channelRepo.findById(channelId);
        if(channel == null){
            throw new IllegalArgumentException("해당 채널을 찾을 수 없습니다.");
        }

        return channel;
    }

    public List<Channel> findAllChannels () {return channelRepo.findAll();}

    public void deleteChannel(UUID channelId){
        Channel channel = findChannelById(channelId);
        channelRepo.deleteById(channelId);
    }

    public Channel updateChannel(UUID channelId, String name, String intro) {
        Channel channel = findChannelById(channelId);

        Optional.ofNullable(name)
                .ifPresent(n-> {
                    if (channel.getName().equals(n)) {
                        throw new IllegalArgumentException("현재 사용 중인 채널 이름입니다");
                    }
                    channel.setName(n);
                });

        Optional.ofNullable(intro)
                .ifPresent(i -> {
                    if(channel.getIntro().equals(i)){
                        throw new IllegalArgumentException("현재 사용 중인 채널 설명입니다");
                    }
                    channel.setIntro(i);
                });

        channel.setUpdatedAt(System.currentTimeMillis());
        return channel;
    }

    @Override
    public void enter(UUID userId, UUID channelId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);

        channelUsers
                .computeIfAbsent(channelId, id -> new HashSet<>())
                .add(userId);

        channel.setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public void exit(UUID userId , UUID channelId) {
        Channel channel = findChannelById(channelId);
        User user = userService.findUserById(userId);

        Set<UUID> users = channelUsers.get(channelId);
        if (users != null) {
            users.remove(userId);
            if (users.isEmpty()) {
                channelUsers.remove(channelId); //       나중에 다시 볼 메서드
            }
        }
    }

    @Override
    public void addMessage(UUID channelId, UUID messageId) {

        channelMessages.computeIfAbsent(channelId, k -> new HashSet<>()).add(messageId);// 나중에 다시 볼 메서드
        findChannelById(channelId).setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public void removeMessage(UUID channelId, UUID messageId) {
        Set<UUID> messages = channelMessages.get(channelId);
        if (messages != null) {
            messages.remove(messageId);
            if (messages.isEmpty()) {
                channelMessages.remove(channelId);                              // 나중에 다시 볼 메서드
            }
        }
        findChannelById(channelId).setUpdatedAt(System.currentTimeMillis());
    }

    @Override
    public int getCurrentUserCount(UUID channelId) {
        return channelUsers.getOrDefault(channelId, Collections.emptySet()).size();
    }

    @Override
    public int getMessageCount(UUID channelId) {
        Channel channel = findChannelById(channelId);
        return channelMessages.getOrDefault(channelId, Collections.emptySet()).size();
    }

}
