package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDTO;
import com.sprint.mission.discodeit.dto.message.MessageCreateDTO;
import com.sprint.mission.discodeit.dto.message.MessageDTO;
import com.sprint.mission.discodeit.dto.message.MessageUpdateDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
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
    public MessageDTO create(MessageCreateDTO messageCreateDTO) {
        Message message;
        if (messageCreateDTO.attachments().isEmpty()) {
            message = new Message(messageCreateDTO.channelId(),
                    messageCreateDTO.userId(),
                    messageCreateDTO.msg());
        } else {
            List<UUID> attachmentList = new ArrayList<>();
            for (BinaryContentDTO binaryContentDTO : messageCreateDTO.attachments()) {
                BinaryContent attachment =  new BinaryContent(binaryContentDTO.fileName(),
                    binaryContentDTO.fileType(),
                    binaryContentDTO.bytes());
                attachmentList.add(attachment.getId());
                this.binaryContentRepository.save(attachment);
            }
            message = new Message(messageCreateDTO.channelId(),
                    messageCreateDTO.userId(),
                    messageCreateDTO.msg(),
                    attachmentList);
        }
        this.messageRepository.save(message);

        return createMessageDTO(message);
    }

    @Override
    public MessageDTO findById(UUID messageId) {
        Message message = this.messageRepository.loadById(messageId);
        return createMessageDTO(message);
    }

    @Override
    public List<MessageDTO> findAll() {
        List<MessageDTO> messageDTOList = new ArrayList<>();

        for (Message message : this.messageRepository.loadAll()) {
            MessageDTO messageDTO = createMessageDTO(message);
            messageDTOList.add(messageDTO);
        }

        return messageDTOList;
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
    public MessageDTO updateMessageData(MessageUpdateDTO messageUpdateDTO) {
        this.messageRepository.loadById(messageUpdateDTO.messageId()).updateText(messageUpdateDTO.msg());

        return this.findById(messageUpdateDTO.messageId());
    }

    @Override
    public void delete(UUID messageId) {
        for (UUID attachMent : this.messageRepository.loadById(messageId).getAttachmentIds()) {
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

    public MessageDTO createMessageDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getMsg(),
                message.getUserId(),
                message.getChannelId(),
                message.getAttachmentIds()
        );
    }
}
