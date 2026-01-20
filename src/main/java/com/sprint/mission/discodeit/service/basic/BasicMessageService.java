package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserCoordinatorService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final UserCoordinatorService userCoordinatorService;
    private final UserService userService;

    public BasicMessageService(MessageRepository messageRepository, UserCoordinatorService userCoordinatorService, UserService userService) {
        this.messageRepository = messageRepository;
        this.userCoordinatorService = userCoordinatorService;
        this.userService = userService;
    }
    @Override
    public Message sendDirectMessage(UUID senderId, UUID receiverId, String content) {
        Channel channel = userCoordinatorService.findOrCreateDirectChannelByChatterIds(senderId, receiverId);
        Message message = send(senderId, channel.getId(), content);
        return message;
    }

    @Override
    public List<Message> findDirectMessagesBySenderIdAndReceiverId(UUID senderId, UUID receiverId) {
        Channel channel = userCoordinatorService.findOrCreateDirectChannelByChatterIds(senderId, receiverId);
        return findMessagesByChannelIdAndMemberId(channel.getId(), senderId);
    }

    @Override
    public Message send(UUID senderId, UUID channelId, String content) {
        validateContent(content);
        User sender =  userService.findUserById(senderId);//사실 아래에서 이미 확인해줌
        Channel channel = userCoordinatorService.findAccessibleChannel(channelId, senderId);
        Message message = new Message(sender,channel, content);
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message updateMessage(UUID senderId, UUID id, String content) {
        validateContent(content);
        checkSender(id,senderId);
        Message message = getMessageById(id);
        message.setContent(content);
        messageRepository.update(message);
        return message;
    }

    @Override
    public List<Message> findMessagesByChannelIdAndMemberId(UUID channelId, UUID memberId) {
        Channel channel = userCoordinatorService.findAccessibleChannel(channelId,memberId);
        return messageRepository.findAllByChannelIdOrderByCreatedAtAsc(channelId);
    }

    @Override
    public void deleteByIdAndSenderId(UUID id, UUID senderId) {
        checkSender(id,senderId);
        messageRepository.deleteById(id);
    }
    private void validateContent(String content) {
        if(content == null || content.isEmpty()){
            throw new IllegalArgumentException("비어있는 메세지");
        }
    }
    private void checkSender(UUID messageId, UUID senderId) {
        Message message = getMessageById(messageId);
        userCoordinatorService.findAccessibleChannel(message.getChannel().getId(), senderId);
        if(!message.getSender().getId().equals(senderId)){
            throw new IllegalArgumentException("작성자가 아님: 사용자ID-"+senderId);
        }
    }
    private Message getMessageById(UUID id) {
        return messageRepository.findById(id).orElseThrow(()->new NoSuchElementException("유효하지 않은 메세지: "+id));
    }
}
