package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.helper.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final BinaryContentRepository binaryContentRepository;
    private final EntityFinder entityFinder;

    @Override
    public MessageDto.MessageResponse create(MessageDto.MessageRequest request, List<BinaryContentDto.BinaryContentRequest> fileInfo) {
        // 입력값 검증
        User user = entityFinder.getUser(request.userId());
        Channel channel = entityFinder.getChannel(request.channelId());
        Objects.requireNonNull(request.content(), "내용은 필수입니다.");
        if (!channel.getUsers().contains(user)) {
            throw new IllegalArgumentException("채널 멤버가 아닙니다.");
        }
        // 여러 개의 첨부파일 구현
        List<UUID> binaryContentIds = new ArrayList<>();
        // binaryContent에 정보 입력 후 레포에 저장
        fileInfo.forEach(fileInform -> {
            BinaryContent binaryContent = new BinaryContent(fileInform);
            binaryContentRepository.save(binaryContent);
            binaryContentIds.add(binaryContent.getId());
        });

        // 메세지 객체 생성
        Message message = new Message(request, binaryContentIds);
        messageRepository.save(message);
        channel.addMessage(message);

        return MessageDto.MessageResponse.from(message);
    }

    @Override
    public MessageDto.MessageResponse findById(UUID messageId) {
        Objects.requireNonNull(messageId, "메세지 Id가 유효하지 않습니다.");
        Message message = entityFinder.getMessage(messageId);

        return MessageDto.MessageResponse.from(message);
    }

    @Override
    public List<MessageDto.MessageResponse> findAllByChannelId(UUID channelId) {
        Channel channel = entityFinder.getChannel(channelId);
        return channel.getMessages().stream().map(MessageDto.MessageResponse::from).toList();
    }

    @Override
    public MessageDto.MessageResponse update(UUID messageId, String content, List<UUID> fileInfo) {
        Message message = entityFinder.getMessage(messageId);
        Optional.ofNullable(content).ifPresent(message::updateContent);
        Optional.ofNullable(fileInfo).ifPresent(message::updateBinaryContentIds);
        messageRepository.save(message);
        return MessageDto.MessageResponse.from(message);
    }

    @Override
    public void delete(UUID messageId) {
        // 입력값 검증
        Message message = entityFinder.getMessage(messageId);
        // 채널의 메세지 리스트에서 메세지 삭제
        entityFinder.getChannel(message.getChannelId()).removeMessage(messageId);
        // 연결된 binaryContent 삭제
        message.getBinaryContentIds().forEach(binaryContentRepository::deleteById);
        // 메세지 삭제
        messageRepository.delete(messageId);
    }
}