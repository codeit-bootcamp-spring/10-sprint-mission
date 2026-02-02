package com.sprint.mission.discodeit.service.file;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import com.sprint.mission.discodeit.dto.MessagePatchDTO;
import com.sprint.mission.discodeit.dto.MessagePostDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FileIo;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class FileMessageService implements MessageService {
	public static final Path MESSAGE_DIRECTORY = Paths.get(System.getProperty("user.dir"), "data", "messages");

    private static MessageService instance;
    private UserService userService;
    private ChannelService channelService;

    private FileIo<Message> messageFileIo;
    private FileIo<User> userFileIo;
    private FileIo<Channel> channelFileIo;

	private FileMessageService(@Value("${discodeit.repository.file-directory}") String directory) {
		this.messageFileIo = new FileIo<>(Paths.get(directory + Message.class.getSimpleName().toLowerCase()));
		messageFileIo.init();
		this.userFileIo = new FileIo<>(Paths.get(directory + User.class.getSimpleName().toLowerCase()));
		userFileIo.init();
		this.channelFileIo = new FileIo<>(Paths.get(directory + Channel.class.getSimpleName().toLowerCase()));
		channelFileIo.init();

        this.userService = FileUserService.getInstance();
        this.channelService = FileChannelService.getInstance();
    }

    public static MessageService getInstance() {
        if (instance == null) instance = new FileMessageService();
        return instance;
    }

    @Override
    public Message create(UUID userId, String text, UUID channelId) {
        // 메시지를 생성 전, 유저가 해당 채널에 속해있는지 확인한다.
        Channel channel = channelService.findById(channelId);
        User user = userService.findById(userId);

        if (user.getChannelList().stream()
            .noneMatch(ch -> ch.getId().equals(channelId))) {
            throw new IllegalArgumentException(
                user.getUserName() + "님은 " + channel.getName() + " 채널에 속해있지 않아 메시지를 보낼 수 없습니다."
            );
        }

        List<Message> messages = messageFileIo.load();
        Message newMessage = new Message(user, text, channel);
        messageFileIo.save(newMessage.getId(), newMessage);

        // channel과 user의 messageList에 현재 message를 add
        channel.addMessage(newMessage);
        channelFileIo.save(channelId, channel);

        user.addMessage(newMessage);
        userFileIo.save(userId, user);


        return newMessage;
    }

    @Override
    public Message findById(UUID id) {
        return messageFileIo.load().stream()
            .filter(u -> u.getId().equals(id))
            .findFirst()
            .orElseThrow(
                () -> new NoSuchElementException("id가 " + id + "인 메시지를 찾을 수 없습니다.")
            );
    }

    @Override
    public List<Message> findByUser(UUID userId) {
        return userService.findById(userId).getMessageList();
    }

    @Override
    public List<Message> findByChannel(UUID channelId) {
        return channelService.findById(channelId).getMessageList();
    }

    @Override
    public Message updateById(UUID messageId, String text) {
        Message updateMessage = this.findById(messageId);

        updateMessage.updateText(text);
        messageFileIo.save(messageId, updateMessage);

        return updateMessage;
    }

    @Override
    public void delete(UUID messageId) {
        try {
            messageFileIo.delete(messageId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
