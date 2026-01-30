package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.*;
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

    @Override
    public Message create(MessageDto.MessageRequest request, List<BinaryContentDto.BinaryContentRequest> fileInfo) {
        // 입력값 검증
        Objects.requireNonNull(request.content(), "내용은 필수입니다.");
        if (!request.channel().getUsers().contains(request.user())) {
            throw new IllegalArgumentException("채널 멤버가 아닙니다.");
        }
        //
        List<UUID> binaryContentIds = new ArrayList<>();

        fileInfo.forEach(fileInform -> {
            BinaryContent binaryContent = new BinaryContent(fileInform);
            binaryContentRepository.save(binaryContent);
            binaryContentIds.add(binaryContent.getId());
        });


        Message message = new Message(request, binaryContentIds);
        messageRepository.save(message);
        channel.addMessage(message);
        channelService.update(channel.getId(), null, null);

        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        return messageRepository.findById(messageId)
                .orElseThrow(() -> new NoSuchElementException("메시지를 찾을 수 없습니다."));
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        Channel channel = channelService.findById(channelId);
        return channel.getMessages();
    }

    @Override
    public Message update(UUID messageId, String content) {
        Message message = findById(messageId);
        Optional.ofNullable(content).ifPresent(message::updateContent);
        messageRepository.save(message);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        Message message = findById(messageId);
        Channel channel = channelService.findById(message.getChannelId());
        channel.removeMessage(messageId);
        channelService.update(channel.getId(), null, null);
        messageRepository.delete(messageId);
    }

    @Override
    public List<Message> getMessageListByChannelId(UUID channelId) {
        return findAllByChannelId(channelId);
    }
}