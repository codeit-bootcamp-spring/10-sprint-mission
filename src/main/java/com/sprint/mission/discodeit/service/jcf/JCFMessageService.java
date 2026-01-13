package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }


    @Override
    public Message send(UUID senderId, UUID channelId, String content) {
        validateContent(content);
        User sender =  userService.getUserById(senderId);
        Channel channel = channelService.getChannelByIdAndMemberId(channelId, senderId);
        Message message = new Message(sender,channel, content);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message updateMessage(UUID senderId, UUID id, String content) {
        validateContent(content);
        checkSender(id,senderId);
        Message message = getMessageById(id);
        message.setContent(content);
        return message;
    }

    @Override
    public List<Message> getMessagesByChannelIdAndMemberId(UUID channelId, UUID memberId) {
        Channel channel = channelService.getChannelByIdAndMemberId(channelId,memberId);
        List<Message> messages
                = data.values()
                    .stream()
                    .filter(e-> e.getChannel().getId().equals(channel.getId()))
                .sorted(Comparator
                        .comparing(Message::getCreatedAt,Comparator.reverseOrder())//시간순
                        .thenComparing(m->m.getSequence())//같은 시간이면 시퀀스로 정렬
                        )
                .toList();
        return messages;
    }

    private Message getMessageById(UUID id) {
        validateMessage(id);
        Message message = data.get(id);
        return message;
    }

    @Override
    public void delete(UUID id, UUID senderId) {
        checkSender(id,senderId);
        data.remove(id);
    }

    private void validateMessage(UUID messageId) {
        boolean exitsMessege = data.containsKey(messageId);
        if(!exitsMessege){
            throw new NoSuchElementException("해당 메세지 없음: "+messageId);
        }
    }
    private void checkSender(UUID messageId, UUID senderId) {
        Message message = getMessageById(messageId);
        channelService.getChannelByIdAndMemberId(message.getChannel().getId(), senderId);
        if(!message.getSender().getId().equals(senderId)){
            throw new IllegalArgumentException("작성자가 아님: 사용자ID-"+senderId);
        }
    }
    private void validateContent(String content) {
        if(content == null || content.isEmpty()){
            throw new IllegalArgumentException("비어있는 메세지");
        }
    }
}
