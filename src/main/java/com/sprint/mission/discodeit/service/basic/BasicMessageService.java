package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.UserResponse;
import com.sprint.mission.discodeit.dto.message.CreateMessageRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.UpdateMessageRequest;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.status.BinaryContentRepository;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("basicMessageService") // Bean 이름 명시
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final AuthService authService;

    /*
     기존 BasicMessageService 코드는 주석처리하고 고도화된 BasicMessageService를 새로 작성함.
     */

    @Override
    public MessageResponse create(CreateMessageRequest request) {
        // 1. 채널 존재 확인
        if(!channelRepository.existsById(request.getChannelId())) {
          throw new NoSuchElementException("Channel not found with id" + request.getChannelId());
        }

        // 2. 작성자 존재 확인
        var author = userRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new NoSuchElementException("Author not found with id" + request.getAuthorId()));

        // 3. 첨부파일 존재 확인
        List<UUID> validAttatchmentIds = new ArrayList<>();
        if(request.getAttachmentIds() != null&&request.getAttachmentIds().isEmpty()){
            for (UUID attachmentId:request.getAttachmentIds()) {
                if(binaryContentRepository.existsById(attachmentId)){
                    validAttatchmentIds.add(attachmentId);
                } else {
                    throw new IllegalArgumentException("Attachment not found" + attachmentId);
                }
            }
        }
        //4. Message 생성 및 저장
        Message message = new Message(
            request.getContent(),
            request.getChannelId(),
            request.getAuthorId(),
            validAttatchmentIds
        );
        messageRepository.save(message);

        //5. MessageResponse 반환
        UserResponse authorResponse = UserResponse.from(author);
        return MessageResponse.from(message,authorResponse,validAttatchmentIds);
    }


//    @Override
//    public Message create(String content, UUID channelId, UUID authorId) {
//        if (!channelRepository.existsById(channelId)) {
//            throw new NoSuchElementException("Channel not found with id " + channelId);
//        }
//        if (!userRepository.existsById(authorId)) {
//            throw new NoSuchElementException("Author not found with id " + authorId);
//        }
//
//        Message message = new Message(content, channelId, authorId);
//        return messageRepository.save(message);
//    }

    @Override
    public MessageResponse find(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        var author = userRepository.findById(message.getAuthorId())
                .orElseThrow(() -> new NoSuchElementException("Author not found with id" + message.getAuthorId()));

        UserResponse authorResponse = UserResponse.from(author);
        return MessageResponse.from(message,authorResponse,message.getAttachmentIds());
    }

    @Override
    public List<MessageResponse> findAllByChannelId(UUID channelId) {
        // 채널 존재 확인
        if(!channelRepository.existsById(channelId)){
            throw new NoSuchElementException("Channel not found with id" + channelId);
        }

        // 해당 채널의 메시지만 필터링
        return messageRepository.findAll().stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(message -> {
                    var author = userRepository.findById(message.getAuthorId())
                            .orElseThrow(() -> new NoSuchElementException("Author not found with id" + message.getAuthorId()));
                    UserResponse authorResponse = UserResponse.from(author);
                    return MessageResponse.from(message,authorResponse,message.getAttachmentIds());
                }).collect(Collectors.toList());
    }

    @Override
    public MessageResponse update(UpdateMessageRequest request) {
        Message message = messageRepository.findById(request.getId())
                .orElseThrow(() -> new NoSuchElementException("Message with id " + request.getId() + " not found"));
        // 내용 수정
        message.update(request.getContent());
        messageRepository.save(message);

        // MessageResponse 반환
        var author = userRepository.findById(message.getAuthorId())
                .orElseThrow(() -> new NoSuchElementException("Author not found with id " + message.getAuthorId()));
        UserResponse authorResponse = UserResponse.from(author);
        return MessageResponse.from(message, authorResponse, message.getAttachmentIds());


    }

    public void delete(UUID messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));

        // 1. 연관된 첨부파일(BinaryContent) 삭제
        if (message.getAttachmentIds() != null && !message.getAttachmentIds().isEmpty()) {
            for (UUID attachmentId : message.getAttachmentIds()) {
                if (binaryContentRepository.existsById(attachmentId)) {
                    binaryContentRepository.deleteById(attachmentId);
                }
            }
        }
        // 2. 메시지 삭제
        messageRepository.deleteById(messageId);
    }
}
