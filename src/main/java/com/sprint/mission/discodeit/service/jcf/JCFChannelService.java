package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    // 필드
    private final List<Channel> channelData;
    private final UserService userService;

    // 생성자
    public JCFChannelService(UserService userService) {
        this.channelData = new ArrayList<>();
        this.userService = userService;
    }
    //생성
    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        channelData.add(channel);
        return channel;
    }

    // 조회
    @Override
    public Channel find(UUID id) {
        return channelData.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));
    }

    // 전체 조회
    @Override
    public List<Channel> readAll(){
        return channelData;
    } // realALl이랑 read랑 type 맞춰줘야 하나?

    // 수정
    @Override
    public void update(UUID id, String name) {
        Channel channel = find(id);
        channel.updateName(name);
    }

    // 삭제
    @Override
    public void delete(UUID id) {
        Channel channel = find(id);
        channelData.remove(channel);
    }

    // channel에 member 추가
    @Override
    public void addMember(UUID userID, UUID channelID){
        Channel channel = find(channelID);
        User user = userService.find(userID);

        if (channel == null || user == null){
            throw new IllegalArgumentException("id must not be null");
        }

        channel.addMember(user);
        user.joinChannel(channel);

    }

    // channel에서 member 제거
    @Override
    public void removeMember(UUID userID, UUID channelID){
        Channel channel = find(channelID);
        User user = userService.find(userID);

        channel.removeMembersIDs(user);
        user.leaveChannel(channel);
    }
}
