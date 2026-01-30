package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageInfoDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ClearMemory;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BasicMessageService implements MessageService, ClearMemory {
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final UserStatusRepository userStatusRepository;
    private final MessageMapper messageMapper;
    private final BinaryContentRepository binaryContentRepository;

    @Override
    public MessageInfoDto create(MessageCreateDto messageCreateDto) {
        userRepository.findById(messageCreateDto.senderId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 사용자가 없습니다."));
        Channel channel = channelRepository.findById(messageCreateDto.channelId())
                .orElseThrow(() -> new IllegalArgumentException("일치하는 채널이 없습니다."));

        // 메시지 생성(첨부파일 없이)

        if (!messageCreateDto.attachmentIds().isEmpty()) {   // 첨부파일 있을 때
            if (messageCreateDto.attachmentIds().stream()   // 첨부파일이 유효할 때
                    .allMatch(id -> binaryContentRepository.findById(id).isPresent())) {
            }
            else{   // 첨부파일 유효 X
                throw new IllegalArgumentException("해당 파일이 없습니다.");
            }
        }
        Message message = new Message(messageCreateDto.senderId(), messageCreateDto.channelId(), messageCreateDto.content(), messageCreateDto.attachmentIds());

        // 채널에 메시지 추가
        channel.addMessage(message.getId());
        channelRepository.save(channel);

        // 사용자 활동 시간 갱신
        userStatusRepository.findByUserId(messageCreateDto.senderId())
                .ifPresent(UserStatus::updateLastActiveTime);

        messageRepository.save(message);
        return messageMapper.toMessageInfoDto(message);
    }

    @Override
    public Message findById(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메시지 ID입니다."));

        return message;
    }

    @Override
    public List<Message> readAll() {
        return messageRepository.readAll();
    }

    @Override
    public Message update(UUID messageId, String newContent) {
        Message message = findById(messageId);
        message.updateContent(newContent);
        messageRepository.save(message);
        return message;
    }

    @Override
    public List<Message> searchMessage(UUID channelId, String keyword) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 없습니다."));

        return readAll().stream()
                .filter(msg -> msg.getChannelId().equals(channelId))
                .filter(msg -> msg.getContent().contains(keyword))
                .toList();
    }

    //    @Override
//    public UUID sendDirectMessage(UUID senderId, UUID receiverId, String content) {
//        Channel dmChannel = getOrCreateDMChannel(senderId, receiverId);
//
//        User sender = userRepository.findById(senderId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 없습니다."));
//
//        Message message = new Message(sender.getId(), dmChannel.getId(), content);
//
//        dmChannel.addMessage(message.getId());
//        messageRepository.save(message);
//        return dmChannel.getId();
//    }
//    private Channel getOrCreateDMChannel(UUID user1Id, UUID user2Id) {
//        User user1 = userRepository.findById(user1Id);
//        User user2 = userRepository.findById(user2Id);
//
//        return channelService.readAll().stream()
//                .filter(c -> c.getIsPrivate() == IsPrivate.PRIVATE)
//                .filter(c -> c.getUsers().size() == 2)
//                .filter(c -> c.getUsers().stream().anyMatch(u -> u.getId().equals(user2Id)))
//                .findFirst()
//                .orElseGet(() -> {
//                    Channel newDmChannel = channelService.create("DM - " + user1.getName() + "-" + user2.getName(), IsPrivate.PRIVATE, user1.getId());
//                    newDmChannel.addUserId(user2);
//                    channelRepository.save(newDmChannel);
//                    return newDmChannel;
//                });
//    }
    @Override
    public void delete(UUID id) {
        messageRepository.delete(id);
    }

    @Override
    public void clear() {
        messageRepository.clear();
    }

}
