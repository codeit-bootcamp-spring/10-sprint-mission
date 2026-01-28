package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService {
    // 필드
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;

    @Override
    public Message create(String contents, UUID userID, UUID channelID) {
        User sender = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));
        Channel channel = channelRepository.find(channelID)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelID));

        // sender가 해당 channel의 member인지 check
        if (!channel.getMembersList().contains(sender)) {
            throw new IllegalArgumentException("User is not in this channel." + channelID);
        }

        // [저장]
        Message message = new Message(contents, sender, channel);

        // [비즈니스]
        sender.addMessage(message);
        channel.addMessage(message);

        // [저장]
        userRepository.save(sender);
        channelRepository.save(channel);
        return  messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageID) {
        return messageRepository.find(messageID)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageID));
    }

    @Override
    public List<Message> findAll() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateName(UUID messageID, String contents) {
        if (messageID == null) {
            throw new IllegalArgumentException("id must not be null");
        }

        // [저장]
        Message msg = messageRepository.find(messageID)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageID));
        msg.updateContents(contents);

        UUID userID = msg.getSender().getId();
        UUID channelID = msg.getChannel().getId();

        // sender에서 반영
        User sender = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));
        for(Message m : sender.getMessageList()){
            if(m.getId().equals(messageID)){
                m.updateContents(contents);
            }
        }
        userRepository.save(sender);

        // channel에서 반영
        Channel channel = channelRepository.find(channelID)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelID));
        for(Message m : channel.getMessageList()){
            if(m.getId().equals(messageID)){
                m.updateContents(contents);
            }
        }
        channelRepository.save(channel);

        return messageRepository.save(msg);
    }

    @Override
    public void deleteMessage(UUID messageID) {
        Message msg = messageRepository.find(messageID)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageID));
        User sender = msg.getSender();
        Channel channel = msg.getChannel();

        UUID senderID = sender.getId();
        UUID channelID = channel.getId();

        // 여기 check
        sender = userRepository.find(senderID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + senderID));
        channel = channelRepository.find(channelID)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelID));

        sender.removeMessage(msg);
        channel.removeMessage(msg);

        messageRepository.deleteMessage(msg.getId());
        userRepository.save(sender);
        channelRepository.save(channel);
    }

    @Override
    public List<String> findMessagesByChannel(UUID channelID) {
        Channel channel = channelRepository.find(channelID)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelID));
        return channel.getMessageList().stream()
                .map(Message::getContents)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<String> findMessagesByUser(UUID userID) {
        User user = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));
        return user.getMessageList().stream()
                .map(Message::getContents)
                .collect(java.util.stream.Collectors.toList());
    }
}
