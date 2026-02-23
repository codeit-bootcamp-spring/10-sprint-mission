package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.entity.BinaryContent;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.mapper.MessageDTOMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageDTOMapper messageDTOMapper;

    // 메세지 생성 메소드
    // 선택적으로 첨부 파일(BinaryContent)를 여러 개 등록할 수 있다.
    @Override
    public MessageResponseDTO create(List<MultipartFile> profiles, MessageCreateRequestDTO req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다!");
        Objects.requireNonNull(req.channelID(), "유효하지 않은 채널ID 입니다!");
        Objects.requireNonNull(req.authorID(), "유효하지 않은 사용자ID 입니다!");

        if (!channelRepository.existsById(req.channelID())) {
            throw new NoSuchElementException("Channel not found with id " + req.channelID());
        }

        if (!userRepository.existsById(req.authorID())) {
            throw new NoSuchElementException("Author not found with id " + req.authorID());
        }

        List<UUID> profileIds = new ArrayList<>();

        // 입력받은 DTO에서 binaryContents를 뽑아오는데... 선택적이라 null일수도 있음 -> null 허용을 위해 Optional
//        List<UUID> attachmentIds = Optional.ofNullable(attachments)
//            .orElse(List.of()) // 뽑았는데 null이 들어가있으면 빈 리스트를 반환?
//            .stream()
//            .map(bcdto -> {
//                // DTO에서 BinaryContent를 만들기 위한 bcdto에서 필드 값 뽑기 ->
//                // BinaryContent 생성자 호출 -> 생성된 인스턴스 아이디를 반환 & 영속화도 같이 진행
//                BinaryContent saved = binaryContentRepository.save(
//                    new BinaryContent("Image", bcdto));
//                return saved.getId();
//            })
//            .toList(); // 리스트화해서 attachmentIds에 대입

        if (profiles != null) {
            for (MultipartFile profile : profiles) {
                try {
                    BinaryContent saved = binaryContentRepository.save(
                        new BinaryContent(profile.getContentType(), profile.getBytes())
                    );
                    profileIds.add(saved.getId());

                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        }

        Message message = new Message(req.content(), req.channelID(), req.authorID(), profileIds);
        Message saved = messageRepository.save(message);

        return messageDTOMapper.messageToResponseDTO(saved);
    }

    @Override
    public MessageResponseDTO find(UUID messageId) {
        Objects.requireNonNull(messageId, "유효하지 않은 메시지ID 입니다.");

        Message message = messageRepository.findById(messageId)
            .orElseThrow(
                () -> new IllegalStateException("존재하지 않는 메시지입니다.")
            );

        return messageDTOMapper.messageToResponseDTO(message);


    }

    @Override
    public List<MessageResponseDTO> findAllByChannelId(UUID channelId) {
        Objects.requireNonNull(channelId, "유효하지 않은 채널id 입니다.");

        return messageRepository.findByChannelId(channelId)
            .stream()
            .map(messageDTOMapper::messageToResponseDTO)
            .toList();
    }

    @Override
    public MessageResponseDTO update(UUID messageId, MessageUpdateRequestDto req) {
        Objects.requireNonNull(req, "유효하지 않은 요청입니다.");
        Objects.requireNonNull(messageId);
        Message message = messageRepository.findById(messageId)
            .orElseThrow(() -> new NoSuchElementException(
                "Message with id " + messageId + " not found"));

        if (req.newContent() != null) {
            message.setContent(req.newContent());
        }

        Message saved = messageRepository.save(message);
        return messageDTOMapper.messageToResponseDTO(saved);
    }

    @Override
    public void delete(UUID messageId) {
        if (!messageRepository.existsById(messageId)) {
            throw new NoSuchElementException("Message with id " + messageId + " not found");
        }

        messageRepository.findById(messageId)
            .orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지에요"))
            .getProfileIds()
            .forEach(binaryContentRepository::deleteByID);

        messageRepository.deleteById(messageId);
    }
}
