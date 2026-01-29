package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.messagedto.CreateRequestDTO;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    // 메세지 생성 메소드
    // 선택적으로 첨부 파일(BinaryContent)를 여러 개 등록할 수 있다.
    @Override
    public Message create(CreateRequestDTO req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다!");
        Objects.requireNonNull(req.channelID(), "유효하지 않은 채널ID 입니다!");
        Objects.requireNonNull(req.authorID(), "유효하지 않은 사용자ID 입니다!");

        if (!channelRepository.existsById(req.channelID())) {
            throw new NoSuchElementException("Channel not found with id " + req.channelID());
        }

        if (!userRepository.existsById(req.authorID())) {
            throw new NoSuchElementException("Author not found with id " + req.authorID());
        }
        // 입력받은 DTO에서 binaryContents를 뽑아오는데... 선택적이라 null일수도 있음 -> null 허용을 위해 Optional
        List<UUID> attachmentIds = Optional.ofNullable(req.binaryContents())
                .orElse(List.of()) // 뽑았는데 null이 들어가있으면 빈 리스트를 반환?
                .stream()
                .map(bdto -> {
                    // DTO에서 BinaryContent를 만들기 위한 bdto에서 필드 값 뽑기 ->
                    // BinaryContent 생성자 호출 -> 생성된 인스턴스 아이디를 반환
                    BinaryContent saved = binaryContentRepository.save(
                            new BinaryContent(bdto.contentType(), bdto.file()));
                    return saved.getId();
                })
                .toList(); // 리스트화해서 attachmentIds에 대입

        Message message = new Message(req.content(), req.channelID(), req.authorID(), attachmentIds);
        return messageRepository.save(message);
    }

    @Override
    public Message find(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
    }

    @Override
    public List<Message> findallByChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "유효하지 않은 채널id 입니다.");

        return messageRepository.findByChannelId(channelId);
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("Message with id " + messageId + " not found"));
        message.update(newContent);
        return messageRepository.save(message);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }
        messageRepository.deleteById(messageId);
    }
}
