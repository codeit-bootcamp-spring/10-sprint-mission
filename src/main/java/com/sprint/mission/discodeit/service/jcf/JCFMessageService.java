package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;


//public class JCFMessageService implements MessageService {
//    private final Map<UUID, Message> messageMap;
//    private final UserService userService;
//    private final ChannelService channelService;
//
//    public JCFMessageService(UserService userService, ChannelService channelService){
//        this.messageMap = new HashMap<>();
//        this.userService = userService;
//        this.channelService = channelService;
//    }
//
//    public Message createMessage(String content, UUID channelId, UUID userId){
//        Channel channel = channelService.findChannelByChannelId(channelId);
//        User user = userService.findUserById(userId);
//
//        Message newMessage = new Message(content, channel, user);
//        messageMap.put(newMessage.getId(), newMessage);
//
//        user.addMessage(newMessage);
//        channel.addMessage(newMessage);
//
//        return newMessage;
//    }
//
//    public List<Message> findMessagesByChannelId(UUID channelId){
//        Channel channel = channelService.findChannelByChannelId(channelId);
//        return channel.getChannelMessages();
//    }
//
//    public List<Message> findMessagesByUserId(UUID userId){
//        User user = userService.findUserById(userId);
//
//        return user.getMyMessages();
//    }
//
//    public List<Message> findAllMessages(){
//        return new ArrayList<>(messageMap.values());
//    }
//
//    public Message findMessageById(UUID id){
//        Message message = messageMap.get(id);
//        if (message == null) {
//            throw new IllegalArgumentException("해당 메시지가 없습니다.");
//        }
//        return message;
//    }
//
//    public Message updateMessage(UUID id, String newContent){
//        Message targetMessage = findMessageById(id);
//
//        targetMessage.updateContent(newContent);
//        System.out.println("메시지가 수정되었습니다");
//
//        return targetMessage;
//    }
//
//    public void deleteMessage(UUID id){
//        Message targetMessage = findMessageById(id);
//
//        // 유저 쪽 리스트에서 삭제
//        if (targetMessage.getUser() != null) {
//            targetMessage.getUser().getMyMessages().remove(targetMessage);
//        }
//        // 채널 쪽 리스트에서 삭제
//        if (targetMessage.getChannel() != null) {
//            targetMessage.getChannel().getChannelMessages().remove(targetMessage);
//        }
//
//        messageMap.remove(id);
//        System.out.println("메시지 삭제 완료: " + targetMessage.getContent());
//    }
//
//}
