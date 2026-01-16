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
        return messageService.createMessage(content, user.getId(), channel.getId());
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

    // 기능 추가 (유저 삭제 & 채널 삭제)

    // 유저삭제시-> 그 유저가 참가중인 채널 가져오기-> 모두 퇴실
    // -> 유저가 발행한 모든 메세지 가져오기 -> 유저가 발행한 메세지 삭제
    // -> 마지막으로 유저 delete 실행
    public void deleteUserClean(UUID uuid) {
        User user = userService.findUserById(uuid);
        // 유저가 참가중인 모든 채널에서 퇴실
        for(Channel ch : user.getJoinedChannels()){
            ch.removeParticipant(user);
        }
        // 모든 메세지 삭제
        List<UUID> messageIds = new ArrayList<>(user.getMessage()).stream()
                .map(msg->msg.getId())
                .toList();
        for(UUID messageId : messageIds){
            messageService.deleteMessage(messageId);
        }
        //유저 삭제
        userService.deleteUser(uuid);
        System.out.println("유저: " + user.getAlias() + " 삭제 완료!");
    }


    // 채널 삭제 시-> 그 채널의 모든 참가자 가져오고 -> 각 유저의 joinedChannel에서
    // 이 채널 제거-> 그 채널에 존재하는 모든 메세지 삭제...
    // 마지막으로 그 채널도 delete.
    public void deleteChannelClean(UUID uuid) {
        Channel channel = channelService.findChannelById(uuid);
        new ArrayList<>(channel.getParticipants()).stream()
                .forEach(user->user.leaveChannel(channel));
        System.out.println("채널 삭제 완료");

        //삭제할 체널의 메세지들도 삭제..
        List<UUID> messageIds = new ArrayList<>(channel.getMessages()).stream()
                .map(msg->msg.getId())
                .toList();
        for(UUID messageId : messageIds){
            messageService.deleteMessage(messageId);
        }

        // 채널 삭제
        channelService.deleteChannel(uuid);
        System.out.println("채널: " + channel.getChannelName() + " 삭제 완료!!");
    }


}
