package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {
    // 필드
    private final List<Channel> channelData;

    // 생성자
    public JCFChannelService() {
        this.channelData = new ArrayList<>();
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
    public List<Channel> findAll(){
        return channelData;
    } // realALl이랑 read랑 type 맞춰줘야 하나?

    // 수정
    @Override
    public Channel updateName(UUID id, String name) {
        Channel channel = find(id);
        channel.updateName(name);
        return channel;
    }

    // Channel 자체 삭제
    @Override
    public void deleteChannel(UUID channelID) {
        Channel channel = find(channelID);

        List<User> members = new ArrayList<>(channel.getMembersList());
        List<Message> messageList = new ArrayList<>(channel.getMessageList());

        // User 마다
        for(User user : members){
            user.leaveChannel(channel);
        }

        // Message 마다
        for(Message msg : messageList){
            //
        }
        // channelData에서 channel 삭제
        channelData.remove(channel);
    }

    // channel에 user 가입
    @Override
    public void joinChannel (User user, UUID channelID){
        Channel channel = find(channelID);

        // channel에 user 추가
        channel.addMember(user);

        // user에 가입한 channel 추가
        user.joinChannel(channel);
    }

    @Override
    public void leaveChannel (User user, UUID channelID){
        Channel channel = find(channelID);

        // user에서 channel 삭제
        user.leaveChannel(channel);

        // channel에서 user 삭제
        channel.removeMember(user);

        // message에 있는 데이터들도 삭제해야 하나?
    }

    // Channel 안 모든 User 조회
    @Override
    public List<String> findMembers(UUID channelID){
        Channel channel = find(channelID);
        return channel.getMembersList().stream()
                .map(User::getName)
                .collect(Collectors.toList());
    }
}
