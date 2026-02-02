package com.sprint.mission.discodeit.service.basic;
import com.sprint.mission.discodeit.dto.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.utils.Validation;
import com.sprint.mission.discodeit.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService{
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;

    //DTO 조립 클래스
    private final MessageMapper messageMapper;


    // 메세지 생성
    @Override
    public MessageResponse createMessage(MessageCreateRequest request) {
        request.validate();

        // ChatCoordinator 의 기능 추가/확장
        User user = userRepository.findById(request.senderId())
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 유저가 없습니다: " + request.senderId()));
        Channel channel = channelRepository.findById(request.channelId())
                .orElseThrow(() -> new NoSuchElementException("해당 ID의 채널이 없습니다: " + request.channelId()));


        Message message = new Message(request.content(), request.senderId(), request.channelId());

        // attachment 있으면, 저장 후 id 추가
        if (request.attachments() != null && !request.attachments().isEmpty()) {
            for (BinaryContentCreateRequest a : request.attachments()) {
                BinaryContent bc = new BinaryContent(a.fileName(), a.contentType(), a.bytes());
                binaryContentRepository.save(bc);
                message.addAttachment(bc.getId()); // Message 내부 attachmentIds에 추가
            }
        }

        // 저장소 저장
        messageRepository.save(message);

        if (user.getMessageIds() != null && !user.getMessageIds().contains(message.getId())) {
            user.getMessageIds().add(message.getId());
            userRepository.save(user);
        }
        if (channel.getMessageIds() != null && !channel.getMessageIds().contains(message.getId())) {
            channel.getMessageIds().add(message.getId());
            channelRepository.save(channel);
        }
        return messageMapper.toResponse(message);
    }



    // 단건 조회
    @Override
    public MessageResponse findMessageResponseByID(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(()->new NoSuchElementException("메세지가 없습니다: " + id));
        return messageMapper.toResponse(message);
    }


    // 전체 조회( 다시보기///)
    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId){
        if(channelId ==null) throw new IllegalArgumentException("channelId는  null일수 없다: " + channelId);
        return messageRepository.findAll().stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(messageMapper::toResponse)
                .toList();
    }


    @Override
    public MessageResponse updateMessage(MessageUpdateRequest request) {
        Validation.notBlank(request.newContent(), "메세지 내용");
        Message msg = messageRepository.findById(request.meesageId())
                .orElseThrow(() -> new NoSuchElementException("수정할 메세지가 존재하지 않습니다: " + request.meesageId()));

        msg.update(request.newContent());
        messageRepository.save(msg);
        return messageMapper.toResponse(msg);
    }

    // 삭제 후 첨부파일 삭제
    @Override
    public void deleteMessage(UUID id) {
        if (id == null) throw new IllegalArgumentException("messageID는 null이 될 수 없습니다.: " + id);

        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("삭제할 메세지가 존재하지 않습니다."));

        if (message.getAttachmentIds() != null) {
            for (UUID binaryContentId : message.getAttachmentIds()) {
                binaryContentRepository.delete(binaryContentId);
            }
        }

        // sender/channel에서 messageId 제거!!!
        User sender = userRepository.findById(message.getSenderId())
                .orElseThrow(() -> new NoSuchElementException("보낸 유저가 없습니다: " + message.getSenderId()));
        Channel channel = channelRepository.findById(message.getChannelId())
                .orElseThrow(() -> new NoSuchElementException("채널이 없습니다: " + message.getChannelId()));

        if (sender.getMessageIds() != null) {
            sender.getMessageIds().remove(id);
            userRepository.save(sender);
        }
        if (channel.getMessageIds() != null) {
            channel.getMessageIds().remove(id);
            channelRepository.save(channel);
        }
        messageRepository.delete(id);
    }




    // ChatCoordinator 가능 이식
//    @Override
//    public List<MessageResponse> getMessageResponsesBySenderId(UUID senderId) {
//        return messageRepository.findAll().stream()
//                .map(this::assemble)
//                .toList();
//    }
//    @Override
//    public List<MessageResponse> getMessageResponsesInChannel(UUID channelId) {
//        return messageRepository.findAll().stream()
//                .filter(m -> m.getChannelId().equals(channelId))
//                .map(this::assemble)
//                .toList();
//    }






}
