package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.util.FileIo;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Repository
public class FileMessageRepository implements MessageRepository {
    public static final Path MESSAGE_DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "messages");

    private final FileIo<Message> messageFileIo;
    private final FileIo<User> userFileIo;
    private final FileIo<Channel> channelFileIo;

    private FileMessageRepository() {
        this.messageFileIo = new FileIo<>(MESSAGE_DIRECTORY);
        messageFileIo.init();
        this.userFileIo = new FileIo<>(FileUserRepository.USER_DIRECTORY);
        userFileIo.init();
        this.channelFileIo = new FileIo<>(FileChannelRepository.CHANNEL_DIRECTORY);
        channelFileIo.init();
    }

    @Override
    public Message save(Message newMessage) {
        messageFileIo.save(newMessage.getId(), newMessage);

        // channel과 user의 messageList에 현재 message를 add
        newMessage.getChannel().addMessage(newMessage);
        channelFileIo.save(newMessage.getChannel().getId(), newMessage.getChannel());

        newMessage.getSender().addMessage(newMessage);
        userFileIo.save(newMessage.getSender().getId(), newMessage.getSender());

        return newMessage;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return messageFileIo.load().stream()
            .filter(u -> u.getId().equals(id))
            .findFirst();
    }

    @Override
    public void delete(UUID id) {
        try {
            messageFileIo.delete(id);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
