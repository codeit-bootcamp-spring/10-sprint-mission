package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public Channel update(UUID id, String name) {
        Channel channel = find(id);
        channel.updateName(name);
        return channel;
    }

    // Channel 삭제
    @Override
    public void deleteChannel(UUID channelID) {
        Channel channel = find(channelID);

        List<User> members = new ArrayList<>(channel.getMembersSet());

        for(User user : members){
            removeMember(user.getId(), channelID);
        }

        channelData.remove(channel);
    }

    // User 삭제
    public void deleteUser(UUID userID){
        User user = userService.find(userID);

        List<Channel> channels = new ArrayList<>(user.getChannels());

        for(Channel c : channels){
            removeMember(userID, c.getId());
        }

    }

    // channel에 member 추가
    @Override
    public void addMember(UUID userID, UUID channelID){
        Channel channel = find(channelID);
        User user = userService.find(userID);

        channel.addMember(user);
        user.joinChannel(channel);
    }

    // channel에서 member 제거, member에서 channel 제거
    @Override
    public void removeMember(UUID userID, UUID channelID){
        Channel channel = find(channelID);
        User user = userService.find(userID);

        channel.removeMembersIDs(user);
        user.leaveChannel(channel);
    }

    // Channel 안 User 조회
    public List<String> getMembers(UUID channelID){
        Channel channel = find(channelID);
        return channel.getMembersSet().stream()
                .map(User::getName)
                .collect(Collectors.toList());
    }

    // Channel 안 Message 조회
    public List<String> getMessages(UUID channelID){
        Channel channel = find(channelID);
        return channel.getMessageList().stream()
                .map(Message::getContents)
                .collect(Collectors.toList());
    }

    // 특정 User가 가입한 Channel 조회
    public List<String> findJoinedChannels(UUID userID){
        User user = userService.find(userID);
        return user.getChannels().stream()
                .map(Channel::getName)
                .toList();
    }
}
