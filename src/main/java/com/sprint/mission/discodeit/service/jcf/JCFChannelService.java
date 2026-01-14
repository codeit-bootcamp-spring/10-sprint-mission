package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {
    // 필드
    private final List<Channel> channelData;
    private MessageService messageService;
    // 생성자
    public JCFChannelService() {
        this.channelData = new ArrayList<>();
    }

    // Setter
    @Override
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
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
        if (messageService == null) {
            throw new IllegalStateException("MessageService is not set in JCFChannelService");
        }

        Channel channel = find(channelID);

        // channel에 속한 user들 삭제
        List<User> members = new ArrayList<>(channel.getMembersList());
        members.forEach(user -> user.leaveChannel(channel));

        // channel message 삭제 (Sender의 messageList, Channel messageList에서 삭제)
        List<Message> messageList = new ArrayList<>(channel.getMessageList());
        messageList.forEach(message -> messageService.deleteMessage(message.getId()));

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

        // user가 보낸 messageList 중 해당 channel에 관한 것 삭제해줘야 함.
        List<Message> messageList = new ArrayList<>(user.getMessageList());

        // for 루프로 messageList에서 channel 과 일치하는 것을 delete
        for(Message msg : messageList){
            // 해당 channel에서 떠나는 user가 보낸 메시지 삭제
            if (msg.getChannel().equals(channel)){
                messageService.deleteMessage(msg.getId()); // user, channel 입장에서도 msg 삭제해줌
            }
        }

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
