package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {
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
        return channel;
    }

    @Override
    public Optional<Channel> findChannelById(UUID id){
        return Optional.ofNullable(channelMap.get(id));
    }

    @Override
    public List<Channel> findAllChannels(){
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public void deleteChannel(UUID id){
        Channel targetChannel = findChannelById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널 없음"));

        for (User participant : targetChannel.getParticipants()) {
            participant.getMyChannels().remove(targetChannel);
        }

        channelMap.remove(id);
    }

    public Channel updateChannel(UUID id, String channelName){
        Channel targetChannel = findChannelById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널 없음"));
        targetChannel.updateChannelInfo(channelName);
        return targetChannel;
    }

    @Override
    public void joinChannel(UUID userID, UUID channelID) {
        Channel targetChannel = findChannelById(channelID)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널 없음"));
        User targetUser = userService.findUserById(userID)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자 없음"));

        boolean isAlreadyExist = targetChannel.getParticipants().stream()
                        .anyMatch(participant -> participant.getId().equals(targetUser.getId()));

        if(isAlreadyExist) {
            throw new IllegalArgumentException("이미 채널에 참여중인 사용자입니다.");
        }

        targetChannel.getParticipants().add(targetUser);
        targetUser.getMyChannels().add(targetChannel);

        System.out.println(targetUser.getUsername() + "님이 "
                                + targetChannel.getChannelName() + " 채널에 입장했습니다.");
    }

    @Override
    public List<User> findParticipants(UUID channelID){
        Channel targetChannel = findChannelById(channelID)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널 없음"));
        return targetChannel.getParticipants();
    }
}
