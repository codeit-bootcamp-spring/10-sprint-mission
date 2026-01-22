package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.util.FileUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateDuplicateValue;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateString;

public class FileMessageService implements MessageService {
    private final Path directory = Paths.get(System.getProperty("user.dir"), "data", "messages");              // 경로 설정
    private final UserService fileUserService;
    private final ChannelService fileChannelService;

    public FileMessageService(FileUserService fileUserService, FileChannelService fileChannelService) {
        FileUtil.init(directory);

        this.fileUserService = fileUserService;
        this.fileChannelService = fileChannelService;
    }

    // 메시지 생성
    @Override
    public Message createMessage(String message, UUID userId, UUID channelId, MessageType type) {
        User sender = fileUserService.searchUser(userId);
        Channel targetChannel = fileChannelService.searchChannel(channelId);

        Message newMessage = new Message(message, sender, targetChannel, type);
        FileUtil.save(directory.resolve(newMessage.getId() + ".ser"), newMessage);

        sender.addMessage(newMessage);
        fileUserService.updateUser(sender.getId(), sender);

        targetChannel.addMessage(newMessage);
        fileChannelService.updateChannel(targetChannel.getId(), targetChannel);

        return newMessage;
    }

    // 메시지 단건 조회
    @Override
    public Message searchMessage(UUID targetMessageId) {
        return FileUtil.loadSingle(directory.resolve(targetMessageId + ".ser"));
    }

    // 메시지 전체 조회
    @Override
    public List<Message> searchMessageAll() {
        return FileUtil.load(directory);
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

        FileUtil.save(directory.resolve(targetMessageId + ".ser"), targetMessage);
        return targetMessage;
    }

    @Override
    public void updateMessage(UUID channelId, Channel channel) {}

    // 파일 내 메시지 수정 (덮어쓰기)
    public void updateMessage(UUID targetMessageId, Message targetMessage) {
        FileUtil.save(directory.resolve(targetMessageId + ".ser"), targetMessage);
    }

    // 메시지 삭제
    @Override
    public void deleteMessage(UUID targetMessageId) {
        Message targetMessage = searchMessage(targetMessageId);

        // 사용자 내 메시지 목록 연쇄 삭제
        User targetUser = fileUserService.searchUser(targetMessage.getUser().getId());
        targetUser.getMessages().removeIf(message -> message.getId().equals(targetMessage.getId()));
        fileUserService.updateUser(targetUser.getId(), targetUser);

        // 채널 내 메시지 목록 연쇄 삭제
        Channel targetChannel = fileChannelService.searchChannel(targetMessage.getChannel().getId());
        targetChannel.getMessages().removeIf(message -> message.getId().equals(targetMessage.getId()));
        fileChannelService.updateChannel(targetChannel.getId(), targetChannel);

        try {
            Files.deleteIfExists(directory.resolve(targetMessageId + ".ser"));
        } catch (IOException e) {
            throw new RuntimeException("[삭제 실패] 시스템 오류가 발생했습니다." + e);
        }
    }
}