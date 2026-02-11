package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.MessageCreateDto;
import com.sprint.mission.discodeit.dto.MessageInfoDto;
import com.sprint.mission.discodeit.dto.MessageUpdateDto;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.service.ClearMemory;
import com.sprint.mission.discodeit.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
        validateAttachments(messageCreateDto.attachmentIds());

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

    private void validateAttachments(List<UUID> attachmentIds) {
        if (attachmentIds == null) return;   // 첨부파일 없을 때

        boolean allMatch = attachmentIds.stream()   // 첨부파일이 유효할 때
                .allMatch(id -> binaryContentRepository.findById(id).isPresent());

        if (!allMatch) {   // 첨부파일 유효 X
            throw new IllegalArgumentException("해당 파일이 없습니다.");
        }
    }

    @Override
    public MessageInfoDto findById(UUID id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메시지 ID입니다."));

        return messageMapper.toMessageInfoDto(message);
    }

    @Override
    public List<MessageInfoDto> findAllByChannelId(UUID channelId) {
        return messageRepository.findAllByChannelId(channelId)
                .stream()
                .map(messageMapper::toMessageInfoDto)
                .toList();
    }

    @Override
    public MessageInfoDto update(MessageUpdateDto messageUpdateDto) {
        Message message = messageRepository.findById(messageUpdateDto.id())
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 없습니다."));
        validateAttachments(messageUpdateDto.attachmentIds());

        // 새 리스트에 포함되지 않은 것들 삭제
        List<UUID> newIds = messageUpdateDto.attachmentIds(); // 1 2 3
        List<UUID> oldIds = message.getAttachmentIds();       // 1 2 3

        List<UUID> idsToDelete = oldIds.stream()
                .filter(id -> !newIds.contains(id))
                .toList();
        if (!idsToDelete.isEmpty()) {
            binaryContentRepository.deleteByIds(idsToDelete);
        }

        message.updateAttachmentIds(messageUpdateDto.attachmentIds());
        message.updateContent(messageUpdateDto.newContent());

        messageRepository.save(message);
        return messageMapper.toMessageInfoDto(message);
    }

    @Override
    public List<MessageInfoDto> searchMessage(UUID channelId, String keyword) {
        channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("해당 채널이 없습니다."));

        return findAllByChannelId(channelId).stream()
                .filter(msg -> msg.content().contains(keyword))
                .toList();
    }

    @Override
    public List<MessageInfoDto> getUserMessages(UUID id) {
        findById(id);
        return messageRepository.findAllByUserId(id).stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(messageMapper::toMessageInfoDto)
                .toList();
    }

    @Override
    public List<MessageInfoDto> getChannelMessages(UUID channelId) {
        findById(channelId);
        return messageRepository.findAllByChannelId(channelId).stream()
                .sorted(Comparator.comparing(Message::getCreatedAt))
                .map(messageMapper::toMessageInfoDto)
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
        Message message = messageRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 메시지가 없습니다."));
        // 첨부파일 삭제
        binaryContentRepository.deleteByIds(message.getAttachmentIds());
        // 채널에 등록된 메시지 삭제
        channelRepository.deleteMessageByMessageId(message.getChannelId(), id);
        // 메시지 자체 삭제
        messageRepository.delete(id);
    }

    @Override
    public void clear() {
        messageRepository.clear();
    }

}
