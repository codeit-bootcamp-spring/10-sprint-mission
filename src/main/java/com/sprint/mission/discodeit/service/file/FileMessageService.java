package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

public class FileMessageService implements MessageService {
    private final MessageRepository fileMessageRepository;

    private final FileUserService fileUserService;
    private final FileChannelService fileChannelService;

    public FileMessageService(FileMessageRepository fileMessageRepository, FileUserService fileUserService, FileChannelService fileChannelService) {
        this.fileMessageRepository = fileMessageRepository;
        this.fileUserService = fileUserService;
        this.fileChannelService = fileChannelService;
    }

    // 메시지 생성
    @Override
    public Message createMessage(String message, UUID userId, UUID channelId, MessageType type) {
        User sender = fileUserService.searchUser(userId);
        Channel targetChannel = fileChannelService.searchChannel(channelId);

        Message newMessage = new Message(message, sender, targetChannel, type);
        fileMessageRepository.save(newMessage);

        sender.addMessage(newMessage);              // 사용자 메시지 목록에 메시지 추가
        fileUserService.updateUser(sender);

        targetChannel.addMessage(newMessage);       // 채널 메시지 목록에 메시지 추가
        fileChannelService.updateChannel(targetChannel);

        return newMessage;
    }

    // 메시지 단건 조회
    @Override
    public Message searchMessage(UUID targetMessageId) {
        return fileMessageRepository.findById(targetMessageId)
                .orElseThrow(() -> new IllegalArgumentException("해당 메시지가 존재하지 않습니다."));
    }

    // 메시지 전체 조회
    @Override
    public List<Message> searchMessageAll() {
        return fileMessageRepository.findAll();
    }

    // 특정 유저가 발행한 메시지 목록 조회
    public List<Message> searchMessagesByUserId(UUID targetUserId) {
        fileUserService.searchUser(targetUserId);

        List<Message> messages = searchMessageAll();               // 함수가 실행된 시점에서 가장 최신 메시지 목록

        return messages.stream()
                .filter(message -> message.getUser().getId().equals(targetUserId))
                .toList();
    }

    // 특정 채널에서 발행된 메시지 목록 조회
    public List<Message> searchMessagesByChannelId(UUID targetChannelId) {
        fileChannelService.searchChannel(targetChannelId);

        List<Message> allMessages = searchMessageAll();

        return allMessages.stream()
                .filter(message -> message.getChannel().getId().equals(targetChannelId))
                .toList();
    }

    // 메시지 수정
    @Override
    public Message updateMessage(UUID targetMessageId, String newMessage) {
        Message targetMessage = searchMessage(targetMessageId);

        // 메시지 필드 변경
        Optional.ofNullable(newMessage)
                .ifPresent(message -> {
                    validateString(message, "[메시지 변경 실패] 올바른 메시지 형식이 아닙니다.");
                    validateDuplicateValue(targetMessage.getMessage(), message, "[메시지 변경 실패] 이전 메시지와 동일합니다.");
                    targetMessage.updateMessage(newMessage);
                });

        fileMessageRepository.save(targetMessage);
        return targetMessage;
    }

    // 파일 내 메시지 수정 (덮어쓰기)
    public void updateMessage(Message targetMessage) {
        fileMessageRepository.save(targetMessage);
    }

    // 메시지 삭제
    @Override
    public void deleteMessage(UUID targetMessageId) {
        Message targetMessage = searchMessage(targetMessageId);

        User targetUser = fileUserService.searchUser(targetMessage.getUser().getId());                          // 사용자 내 메시지 목록 연쇄 삭제
        targetUser.getMessages().removeIf(message -> message.getId().equals(targetMessage.getId()));
        fileUserService.updateUser(targetUser);

        Channel targetChannel = fileChannelService.searchChannel(targetMessage.getChannel().getId());           // 채널 내 메시지 목록 연쇄 삭제
        targetChannel.getMessages().removeIf(message -> message.getId().equals(targetMessage.getId()));
        fileChannelService.updateChannel(targetChannel);

        fileMessageRepository.delete(targetMessage);
    }
}