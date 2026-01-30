package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;


import java.util.*;

public class JCFChannelService implements ChannelService {
    private final ChannelRepository channelRepository;
    private final UserService userService;

    public JCFChannelService(ChannelRepository channelRepository, UserService userService) {
        this.channelRepository = channelRepository;
        this.userService = userService;
    }

    @Override
    public Channel createChannel(ChannelType type, String channelName, String channelDescription) {
        Validators.validationChannel(type, channelName, channelDescription);
        Channel channel = new Channel(type, channelName, channelDescription);
        return channelRepository.save(channel);
    }

    @Override
    public Channel find(UUID id) {
        return validateExistenceChannel(id);
    }

    @Override
    public List<Channel> findAllByUserId() {
        return channelRepository.findAll();
    }

    @Override
    public Channel updateChannel(UUID id, ChannelType type, String channelName, String channelDescription) {
        Channel channel = validateExistenceChannel(id);
        Optional.ofNullable(type).ifPresent(t -> {Validators.requireNonNull(t, "type");
            channel.updateChannelType(t);
        });
        Optional.ofNullable(channelName)
                .ifPresent(name -> {Validators.requireNotBlank(name, "type");
                    channel.updateChannelName(name);
                });
        Optional.ofNullable(channelDescription).ifPresent(des -> {
            Validators.requireNotBlank(des, "channelDescription");
            channel.updateChannelDescription(des);
        });

        return channel;
    }



    @Override
    public void deleteChannel(UUID id) {
        Channel channel = find(id);
        channelRepository.deleteById(id);
    }

    @Override
    public void joinChannel(UUID channelId, UUID userId) {
        Channel channel = find(channelId);
        User user = userService.find(userId);

        boolean alreadyJoined = channel.getJoinedUsers().stream()
                .anyMatch(u -> u.getId().equals(userId));

        if (alreadyJoined) {
            throw new IllegalArgumentException("이미 참가한 유저입니다.");
        }

        channel.getJoinedUsers().add(user);
        user.getJoinedChannels().add(channel);
    }

    @Override
    public void leaveChannel(UUID channelId, UUID userId) {
        Channel channel = find(channelId);
        User user = userService.find(userId);

        boolean alreadyLeaved = channel.getJoinedUsers().stream()
                .noneMatch(u -> u.getId().equals(userId));

        if (alreadyLeaved) {
            throw new IllegalArgumentException("채널에 속해 있지 않은 유저입니다.");
        }

        channel.getJoinedUsers().removeIf(u -> user.getId().equals(u.getId()));
        user.getJoinedChannels().removeIf(c -> channelId.equals(c.getId()));
    }

    @Override
    public List<Channel> readChannelsByUser(UUID userId) {
        return channelRepository.findAll().stream()
                .filter(ch -> ch.getJoinedUsers().stream()
                        .anyMatch(u -> userId.equals(u.getId())))
                .toList();
    }


    private Channel validateExistenceChannel(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return channelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
    }
}

