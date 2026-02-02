package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.CreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
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
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public Message create(CreateRequest request) {
        User sender = userRepository.find(request.userID())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.userID()));
        Channel channel = channelRepository.find(request.channelId());

        // sender가 해당 channel의 member인지 check
        if (!channel.getMembersList().contains(sender)) {
            throw new IllegalArgumentException("User is not in this channel." + request.channelId());
        }

        // 첨부파일, 생성
        List<UUID> attachments = new ArrayList<>();
        if(request.attachments() != null){
            for(BinaryContentCreateRequest req : request.attachments()){
                BinaryContent attachment = new BinaryContent(req.bytes(), req.contentType());
                binaryContentRepository.save(attachment);

                attachments.add(attachment.getId());
            }
        }

        // messsage 생성
        Message message = new Message(request.content(), sender, channel, attachments);

        // sender ,channel에 추가
        sender.addMessage(message);
        channel.addMessage(message);

        // [저장]
        userRepository.save(sender);
        channelRepository.save(channel);
        return  messageRepository.save(message);
    }

    @Override
    public MessageResponse find(UUID messageID) {
        Message msg = messageRepository.find(messageID)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageID));
        return new MessageResponse(
                msg.getId(),
                msg.getContents(),
                msg.getSender().getId(),
                msg.getChannel().getId(), msg.getAttachmentIDs()
        );
    }

    @Override
    public List<MessageResponse> findAllByUserID() {
        return messageRepository.findAll().stream()
                .map(msg -> new MessageResponse(
                        msg.getId(),
                        msg.getContents(),
                        msg.getSender().getId(),
                        msg.getChannel().getId(),
                        msg.getAttachmentIDs()
                )).toList();
    }

    @Override
    public MessageResponse update(MessageUpdateRequest request) {
        // [저장]
        Message msg = messageRepository.find(request.messageID())
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + request.messageID()));

        // content update
        if (request.content() == null) {
            msg.updateContents(request.content());

        }

        // attachment 업데이트
        if (request.attachments() != null) {
            for (BinaryContentCreateRequest req : request.attachments()) {
                BinaryContent attachment = new BinaryContent(req.bytes(), req.contentType());
                binaryContentRepository.save(attachment);
                msg.addAttachment(attachment.getId());
            }
        }

        UUID userID = msg.getSender().getId();
        UUID channelID = msg.getChannel().getId();

        // sender에서 반영
        User sender = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));
        for(Message m : sender.getMessageList()){
            if(m.getId().equals(msg.getId())){
                m.updateContents(request.content());
            }
        }
        userRepository.save(sender);

        // channel에서 반영
        Channel channel = channelRepository.find(channelID);
        for(Message m : channel.getMessageList()){
            if(m.getId().equals(request.messageID())){
                m.updateContents(request.content());
            }
        }
        channelRepository.save(channel);
        messageRepository.save(msg);
        return new MessageResponse(
                msg.getId(),
                msg.getContents(),
                msg.getSender().getId(),
                msg.getChannel().getId(),
                msg.getAttachmentIDs()
        );
    }

    @Override
    public void deleteMessage(UUID messageID) {
        Message msg = messageRepository.find(messageID)
                .orElseThrow(() -> new IllegalArgumentException("Message not found: " + messageID));

        User sender = msg.getSender();
        Channel channel = msg.getChannel();

        UUID senderID = sender.getId();
        UUID channelID = channel.getId();

        // 첨부 파일 삭제
        for (UUID attachmentID : msg.getAttachmentIDs()) {
            binaryContentRepository.delete(attachmentID);
        }

        // 여기 check
        sender = userRepository.find(senderID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + senderID));
        channel = channelRepository.find(channelID);

        // sender, channel에서 msg 삭제
        sender.removeMessage(msg);
        channel.removeMessage(msg);

        messageRepository.deleteMessage(msg.getId());
        userRepository.save(sender);
        channelRepository.save(channel);
    }

    @Override
    public List<MessageResponse> findMessagesByChannel(UUID channelID) {
        Channel channel = channelRepository.find(channelID);
        return channel.getMessageList().stream()
                .map(msg -> new MessageResponse(
                        msg.getId(),
                        msg.getContents(),
                        msg.getSender().getId(),
                        msg.getChannel().getId(),
                        msg.getAttachmentIDs()
                )).toList();
    }

    @Override
    public List<MessageResponse> findMessagesByUser(UUID userID) {
        User user = userRepository.find(userID)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));

        return user.getMessageList().stream()
                .map(msg -> new MessageResponse(
                        msg.getId(),
                        msg.getContents(),
                        msg.getSender().getId(),
                        msg.getChannel().getId(),
                        msg.getAttachmentIDs()
                )).toList();
    }
}
