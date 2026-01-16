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

public class JCFMessageService implements MessageService {
    List<Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService){
        data = new ArrayList<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    public Message createMessage(String content, UUID senderId, UUID channelId){
        User senderUser = userService.findById(senderId);
        Channel channel = channelService.findId(channelId);
        Message message = new Message(content, senderUser, channel);
        data.add(message);
        System.out.println("메세지 생성이 완료되었습니다.");
        return message;
    }

    public Message findId(UUID massageId){
        return data.stream()
                .filter(message -> message.getId().equals(massageId))
                .findFirst()
                .orElse(null);
    }

    public List<Message> findAll(){
        return data;
    }

    public List<Message> findMessagesById(UUID userId){
        return data.stream()
                .filter(message -> message.getUser().getId().equals(userId))
                .toList();
    }

    public List<Message> findMessagesByChannel(UUID channelId){
        return data.stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    // 유저가 작성한 메세지 중 특정 채널에서의 메세지들
    public List<Message> findMessagesByUserAndChannel(UUID userId, UUID channelId){
        List<Message> messages = findMessagesById(userId);
        return messages.stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }


    public Message update(UUID massageId, String content){
        Message foundMsg = findId(massageId);
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("내용이 비어있거나 공백입니다.");
        }
        return foundMsg;
    }

    public void delete(UUID massageId){
        Message target = findId(massageId);
        data.remove(target);
    }

}
