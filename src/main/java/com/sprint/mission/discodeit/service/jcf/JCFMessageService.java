<<<<<<< HEAD

=======
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final List<Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService,ChannelService channelService) {
        this.data = new ArrayList<>();
=======
import java.util.*;


public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageMap;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService){
        this.messageMap = new HashMap<>();
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
        this.userService = userService;
        this.channelService = channelService;
    }

<<<<<<< HEAD
    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        // 1. userId로 실제 User 조회 (없으면 예외)
        User user = userService.findById(userId);

        // 2. channelId로 실제 Channel 조회 (없으면 예외)
        Channel channel = channelService.findById(channelId);

        // 3. 조회한 실제 객체로 Message 생성
        Message message = new Message(user, channel, content);
        data.add(message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void updateMessage(UUID id, String content, UUID userId, UUID channelId) {
       //1. 수정할 메시지 찾기
        Message message = findById(id);

        //2. content가 null이 아니면 수정
        Optional.ofNullable(content).ifPresent(message::setContent);

        //3. userId가 null이 아니면 -> User 조회 후 설정
        if(userId != null) {
            User user = userService.findById(userId);
            message.setUser(user);
        }
        // 4. channelId가 null이 아니면 → Channel 조회 후 설정
        if (channelId != null) {
            Channel channel = channelService.findById(channelId);
            message.setChannel(channel);
        }
        message.touch();
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);
        data.remove(message);
=======
    public Message createMessage(String content, UUID channelId, UUID userId){
        Channel channel = channelService.findChannelByChannelId(channelId);
        User user = userService.findUserById(userId);

        Message newMessage = new Message(content, channel, user);
        messageMap.put(newMessage.getId(), newMessage);

        user.addMessage(newMessage);
        channel.addMessage(newMessage);

        return newMessage;
    }

    public List<Message> findMessagesByChannelId(UUID channelId){
        Channel channel = channelService.findChannelByChannelId(channelId);
        return channel.getChannelMessages();
    }

    public List<Message> findMessagesByUserId(UUID userId){
        User user = userService.findUserById(userId);

        return user.getMyMessages();
    }

    public List<Message> findAllMessages(){
        return new ArrayList<>(messageMap.values());
    }

    public Message findMessageById(UUID id){
        Message message = messageMap.get(id);
        if (message == null) {
            throw new IllegalArgumentException("해당 메시지가 없습니다.");
        }
        return message;
    }

    public Message updateMessage(UUID id, String newContent){
        Message targetMessage = findMessageById(id);

        targetMessage.updateContent(newContent);
        System.out.println("메시지가 수정되었습니다");

        return targetMessage;
    }

    public void deleteMessage(UUID id){
        Message targetMessage = findMessageById(id);

        // 유저 쪽 리스트에서 삭제
        if (targetMessage.getUser() != null) {
            targetMessage.getUser().getMyMessages().remove(targetMessage);
        }
        // 채널 쪽 리스트에서 삭제
        if (targetMessage.getChannel() != null) {
            targetMessage.getChannel().getChannelMessages().remove(targetMessage);
        }

        messageMap.remove(id);
        System.out.println("메시지 삭제 완료: " + targetMessage.getContent());
>>>>>>> 3a7b55e457e0d55f5042c220079e6b60cb0acc7f
    }

}
