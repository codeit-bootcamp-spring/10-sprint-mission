package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private static final String FILE_PATH = "data/messages.ser";
    private final FileMessageRepository fileMessageRepository;
    private final FileChannelRepository fileChannelRepository;
    private final FileUserRepository fileUserRepository;


    public FileMessageService(FileMessageRepository fileMessageRepository, FileChannelRepository fileChannelRepository, FileUserRepository fileUserRepository) {
        this.fileMessageRepository = fileMessageRepository;
        this.fileChannelRepository = fileChannelRepository;
        this.fileUserRepository = fileUserRepository;

    }

    @Override
    public Message createMessage(String content, UUID channelId, UUID userId) {
        Validators.validationMessage(content);
        Channel channel = fileChannelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
        User user = fileUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));

        Message message = new Message(content, channel, user);

        channel.getMessages().add(message);
        user.getMessages().add(message);

        fileMessageRepository.save(message);
        fileChannelRepository.save(channel);
        fileUserRepository.save(user);

        return message;
    }

    @Override
    public Message readMessage(UUID id) {
        return validateExistenceMessage(id);
    }

    @Override
    public List<Message> readAllMessage() {
        return fileMessageRepository.findAll();
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        Message message = validateExistenceMessage(id);
        Optional.ofNullable(content)
                .ifPresent(cont -> {Validators.requireNotBlank(cont, "content");
                    message.updateContent(content);
                });

        fileMessageRepository.save(message);
        return message;
    }

    public void deleteMessage(UUID messageId) {
        Message message = validateExistenceMessage(messageId);

        UUID channelId = message.getChannel().getId();
        UUID userId = message.getUser().getId();

        Channel channel = fileChannelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
        User user = fileUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));

        channel.getMessages().removeIf(m -> m.getId().equals(messageId));
        user.getMessages().removeIf(m -> m.getId().equals(messageId));

        fileMessageRepository.deleteById(messageId);

        fileChannelRepository.save(channel);
        fileUserRepository.save(user);
    }

    public List<Message> readMessagesByChannel(UUID channelId) {
        Channel channel = fileChannelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
        return channel.getMessages();
    }

    public List<Message> readMessagesByUser(UUID userId) {
        User user = fileUserRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));
        return user.getMessages();
    }
    private Message validateExistenceMessage(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return fileMessageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메세지 id는 존재하지 않습니다."));
    }

}
