package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.message.MessageCreateDTO;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("messageService")
public class BasicMessageService implements MessageService {
    private MessageRepository messageRepository;
    private ChannelRepository channelRepository;
    private UserRepository userRepository;
    private BinaryContentRepository binaryContentRepository;
//    private BasicUserService userService;
//    private BasicChannelService channelService;


    public BasicMessageService(MessageRepository messageRepository, ChannelRepository channelRepository, UserRepository userRepository, BinaryContentRepository binaryContentRepository) {
        this.messageRepository = messageRepository;
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        this.binaryContentRepository = binaryContentRepository;
    }

    @Override
    public Message create(MessageCreateDTO messageCreateDTO) {
        Message message;
        if (messageCreateDTO.attachmentIds().isEmpty()) {
            message = new Message(messageCreateDTO.channelId(),
                    messageCreateDTO.userId(),
                    messageCreateDTO.msg());
        } else {
            message = new Message(messageCreateDTO.channelId(),
                    messageCreateDTO.userId(),
                    messageCreateDTO.msg(),
                    messageCreateDTO.attachmentIds());
        }
        this.messageRepository.save(message);
        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        return this.messageRepository.loadById(messageId);
    }

    @Override
    public List<Message> findAll() {
        return this.messageRepository.loadAll();
    }

    public List<Message> findallByChannelId(UUID channelId) {
        List<Message> messages = new ArrayList<>();

        for (Message message : this.messageRepository.loadAll()) {
            if (message.getChannelId().equals(channelId)) {
                messages.add(message);
            }
        }

        return messages;
    }

    @Override
    public Message updateMessageData(MessageUpdateDTO messageUpdateDTO) {
        this.findById(messageUpdateDTO.messageId()).updateText(messageUpdateDTO.msg());

        return this.findById(messageUpdateDTO.messageId());
    }

    @Override
    public void delete(UUID messageId) {
        for (UUID attachMent : this.findById(messageId).getAttachmentIds()) {
            this.binaryContentRepository.delete(attachMent);
        }
        this.messageRepository.delete(messageId);
    }

    // 특정 유저가 발행한 메시지 리스트 조회
    @Override
    public List<UUID> readUserMessageList(UUID userId) {
        User user = userRepository.loadById(userId);
        return user.getMessageList();
    }

    // 특정 채널의 발행된 메시지 목록 조회
    @Override
    public List<UUID> readChannelMessageList(UUID channelId) {
        Channel channel = channelRepository.loadById(channelId);
        return channel.getMessagesList();
    }
}
