package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
<<<<<<< HEAD
import com.sprint.mission.discodeit.service.ChannelService;
=======
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f

import java.util.*;

public class JCFChannelService implements ChannelService {
<<<<<<< HEAD

    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Channel createChannel(String name, String description) {
        Channel channel = new Channel(name, description);
        data.add(channel);
=======
    private final Map<UUID, Channel> channelMap;
    private final UserService userService;

    public JCFChannelService(UserService userService){
        this.channelMap = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel createChannel(String channelName) {
        Channel channel = new Channel(channelName);
        channelMap.put(channel.getId(), channel);
        System.out.println(channel.getChannelName() + "채널 생성이 완료되었습니다.");
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
        return channel;
    }

    @Override
<<<<<<< HEAD
    public Channel findById(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Channel not found"+id));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void updateChannel(UUID id, String name, String description) {
        Channel channel = findById(id);
        Optional.ofNullable(name).ifPresent(channel::updateChannelName);
        Optional.ofNullable(description).ifPresent(channel::updateChannelDescription);

        channel.touch();
    }

    @Override
    public void delete(UUID id) {
        Channel channel = findById(id);
        data.remove(channel);
    }

=======
    public Channel findChannelByChannelId(UUID id){
        Channel channel = channelMap.get(id);
        if (channel == null) {
            throw new IllegalArgumentException("해당 채널이 없습니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> findChannelByUserId(UUID userID){
        User user = userService.findUserById(userID);
        return new ArrayList<>(user.getMyChannels());
    }

    @Override
    public List<Channel> findAllChannels(){
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public void deleteChannel(UUID id){
        Channel targetChannel = findChannelByChannelId(id);

        targetChannel.getParticipants()
                        .forEach(user -> user.getMyChannels().remove(targetChannel));
        channelMap.remove(id);
    }

    public Channel updateChannel(UUID id, String channelName){
        Channel targetChannel = findChannelByChannelId(id);
        targetChannel.updateChannelInfo(channelName);
        return targetChannel;
    }

    @Override
    public void joinChannel(UUID userID, UUID channelID) {
        Channel targetChannel = findChannelByChannelId(channelID);
        User targetUser = userService.findUserById(userID);

        targetChannel.getParticipants().stream()
                .filter(participant -> participant.getId().equals(targetUser.getId()))
                .findAny()
                .ifPresent(participant -> {
                    throw new IllegalArgumentException("이미 채널에 참여중인 사용자입니다.");
                });

        targetChannel.getParticipants().add(targetUser);
        targetUser.getMyChannels().add(targetChannel);

        targetChannel.getParticipants().add(targetUser);
        targetUser.getMyChannels().add(targetChannel);

        System.out.println(targetUser.getUsername() + "님이 "
                                + targetChannel.getChannelName() + " 채널에 입장했습니다.");
    }

    @Override
    public void leaveChannel(UUID userID, UUID channelID) {
        Channel targetChannel = findChannelByChannelId(channelID);
        User targetUser = userService.findUserById(userID);

        if (!targetChannel.getParticipants().contains(targetUser)) {
            throw new IllegalArgumentException("채널에 참여중이지 않습니다.");
        }

        targetChannel.getParticipants().remove(targetUser);
        targetUser.getMyChannels().remove(targetChannel);
    }
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
}
