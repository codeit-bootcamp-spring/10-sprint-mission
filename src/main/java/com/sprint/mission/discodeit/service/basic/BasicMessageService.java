package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.Validators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
    private static final String FILE_PATH = "data/messages.ser";
    private final MessageRepository messageRepository;
    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;

    @Override
    public Message createMessage(String content, UUID channelId, UUID userId) {
        Validators.validationMessage(content);
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));

        Message message = new Message(content, channel, user);

        channel.getMessages().add(message);
        user.getMessages().add(message);

        messageRepository.save(message);
        channelRepository.save(channel);
        userRepository.save(user);

        return message;
    }

    @Override
    public Message readMessage(UUID id) {
        return validateExistenceMessage(id);
    }

    @Override
    public List<Message> readAllMessage() {
        return messageRepository.findAll();
    }

    @Override
    public Message updateMessage(UUID id, String content) {
        Message message = validateExistenceMessage(id);
        Optional.ofNullable(content)
                .ifPresent(cont -> {Validators.requireNotBlank(cont, "content");
                    message.updateContent(content);
                });

        messageRepository.save(message);
        return message;
    }

    public void deleteMessage(UUID messageId) {
        Message message = validateExistenceMessage(messageId);

        UUID channelId = message.getChannel().getId();
        UUID userId = message.getUser().getId();

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));

        channel.getMessages().removeIf(m -> m.getId().equals(messageId));
        user.getMessages().removeIf(m -> m.getId().equals(messageId));

        messageRepository.deleteById(messageId);

        channelRepository.save(channel);
        userRepository.save(user);
    }

    public List<Message> readMessagesByChannel(UUID channelId) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
        return channel.getMessages();
    }

    public List<Message> readMessagesByUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유저 id가 존재하지 않습니다."));
        return user.getMessages();
    }
    private Message validateExistenceMessage(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return messageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("메세지 id는 존재하지 않습니다."));
    }

}
