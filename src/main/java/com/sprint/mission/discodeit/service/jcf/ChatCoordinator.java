package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.List;
import java.util.UUID;

public class ChatCoordinator {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public ChatCoordinator(UserService userService, ChannelService chanelService, MessageService messageService){
        this.userService = userService;
        this.channelService = chanelService;
        this.messageService = messageService;
    }
    //유저가 채널에 입장
    public void joinChannel(UUID userId, UUID channelId) {
        User user = userService.findUserById(userId);
        Channel chanel = channelService.findChannelById(channelId);
        user.joinChannel(chanel);
    }
    //유저가 채널에서 퇴장
    public void leaveChannel(UUID userId, UUID channelId) {
        User user = userService.findUserById(userId);
        Channel chanel = channelService.findChannelById(channelId);
        user.leaveChannel(chanel);
    }
    // 특정 채널에 참가중인 유저리스트 조회
    public List<User> getUsersInChannel(UUID channelId) {
        Channel channel = channelService.findChannelById(channelId);
        return channel.getParticipants();
    }
    // 특정 유저가 참여중인 채널 리스트 조회
    public List<Channel> getChannelsByUser(UUID userId) {
        User user = userService.findUserById(userId);
        return user.getJoinedChannels();
    }

    // 메세지 관련 서비스
    public Message sendMessage(UUID userId, UUID channelId, String content){
        // 여기서 유효성 검사..
        User user = userService.findUserById(userId);
        Channel channel = channelService.findChannelById(channelId);
        return messageService.createMessage(content, user, channel);
    }
    // 채널에 있는 메세지들 조회
    public List<Message> getMessageInChannel(UUID channelId){
        Channel channel = channelService.findChannelById(channelId);
        return channel.getMessages();
    }
    // 유저가 보낸 메세지들 조회
    public List<Message> getMessagesByUser(UUID userId){
        User user = userService.findUserById(userId);
        return messageService.getMsgListSenderId(userId);
    }


}
