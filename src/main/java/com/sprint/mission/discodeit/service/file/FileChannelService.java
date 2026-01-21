package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private final ChannelRepository channelRepository = new FileChannelRepository();
    private final UserRepository userRepository = new FileUserRepository();

    @Override
    public Channel createChannel(String channelName) {
        Channel channel = new Channel(channelName);
        channelRepository.save(channel);
        System.out.println(channel.getChannelName() + "채널 생성이 완료되었습니다.");
        return channel;
    }

    @Override
    public Channel findChannelByChannelId(UUID id){
        return channelRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("해당 채널을 찾을다 수 없습니다"));
    }

    @Override
    public List<Channel> findChannelByUserId(UUID userID){
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다"));
        return user.getMyChannels();
    }

    @Override
    public List<Channel> findAllChannels(){
        return channelRepository.findAll();
    }

    @Override
    public void deleteChannel(UUID id){
        Channel targetChannel = findChannelByChannelId(id);

        for (User user : targetChannel.getParticipants()) {
            user.getMyChannels().remove(targetChannel);
            userRepository.save(user);
        }
        channelRepository.delete(id);
    }

    public Channel updateChannel(UUID id, String channelName){
        Channel targetChannel = findChannelByChannelId(id);
        targetChannel.updateChannelInfo(channelName);
        channelRepository.save(targetChannel);
        return targetChannel;
    }

    @Override
    public void joinChannel(UUID userID, UUID channelID) {
        Channel targetChannel = findChannelByChannelId(channelID);
        User targetUser = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다"));

        if(targetChannel.getParticipants().stream()
                        .anyMatch(participant -> participant.getId().equals(userID))){
            throw new IllegalArgumentException("이미 채널에 참여중인 사용자입니다.");
        }

        targetChannel.addParticipant(targetUser); // 채널에 유저 추가
        targetUser.getMyChannels().add(targetChannel); // 유저에 채널 추가

        // 영속화
        channelRepository.save(targetChannel);
        userRepository.save(targetUser);

        System.out.println(targetUser.getUsername() + "님이 "
                + targetChannel.getChannelName() + " 채널에 입장했습니다.");
    }

    @Override
    public void leaveChannel(UUID userID, UUID channelID) {
        Channel targetChannel = findChannelByChannelId(channelID);
        User targetUser = userRepository.findById(userID)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다"));

        if (!targetChannel.getParticipants().contains(targetUser)) {
            throw new IllegalArgumentException("채널에 참여중이지 않습니다.");
        }

        targetChannel.getParticipants().remove(targetUser);
        targetUser.getMyChannels().remove(targetChannel);

        // 영속화
        channelRepository.save(targetChannel);
        userRepository.save(targetUser);
    }
}