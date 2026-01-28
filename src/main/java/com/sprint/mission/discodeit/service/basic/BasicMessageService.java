package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    // 필드
    private MessageRepository messageRepository;
    private UserRepository userRepository;
    private ChannelRepository channelRepository;

    private ChannelService channelService;
    private UserService userService;

    public BasicMessageService(MessageRepository messageRepository, UserRepository userRepository, ChannelRepository channelRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
    }

    @Override
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Message create(String contents, UUID userID, UUID channelID) {
        if (userService == null) {
            throw new IllegalStateException("UserService is not set in BasicMessageService");
        }
        if (channelService == null){
            throw new IllegalStateException("ChannelService is not set in BasicMessageService");
        }

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
        User sender = userService.find(userID);
        for(Message m : sender.getMessageList()){
            if(m.getId().equals(messageID)){
                m.updateContents(contents);
            }
        }
        userRepository.save(sender);

        // channel에서 반영
        Channel channel = channelService.find(channelID);
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
        sender = userService.find(senderID);
        channel = channelService.find(channelID);

        sender.removeMessage(msg);
        channel.removeMessage(msg);

        messageRepository.deleteMessage(msg);
        userRepository.save(sender);
        channelRepository.save(channel);
    }

    @Override
    public List<String> findMessagesByChannel(UUID channelID) {
        if (channelService == null) {
            throw new IllegalStateException("ChannelService is not set in BasicMessageService");
        }
        Channel channel = channelService.find(channelID);
        return channel.getMessageList().stream()
                .map(Message::getContents)
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<String> findMessagesByUser(UUID userID) {
        if (userService == null) {
            throw new IllegalStateException("UserService is not set in BasicMessageService");
        }

        User user = userService.find(userID);
        return user.getMessageList().stream()
                .map(Message::getContents)
                .collect(java.util.stream.Collectors.toList());
    }
}
