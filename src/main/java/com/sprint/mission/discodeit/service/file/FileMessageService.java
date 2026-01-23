package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class FileMessageService extends BaseFileService<Message> implements MessageService {

    private UserService userService;
    private ChannelService channelService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    public FileMessageService(Path directory) {
        super(directory);
    }

    @Override
    public void save(Message message) {
        super.save(message);
    }

    @Override
    public Message sendMessage(UUID authorId, UUID channelId, String content) {
        User author = userService.findById(authorId);
        Channel channel = channelService.findById(channelId);

        if (!author.getChannels().contains(channel)) {
            throw new IllegalStateException("해당 채널에 가입되지 않은 유저는 메시지를 보낼 수 없습니다.");
        }

        Message message = new Message(content, author, channel);
        save(message);

        author.addMessage(message);
        userService.save(author);

        channel.addMessage(message);
        channelService.save(channel);

        return message;

    }

    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {
        Channel channel = channelService.findById(channelId);
        return channel.getMessages();
    }

    @Override
    public List<Message> findMessagesByAuthor(UUID authorId) {
        User user = userService.findById(authorId);
        return user.getMessages();
    }

    @Override
    public Message updateMessage(UUID messageId, String newContent) {
        Message message = findById(messageId);

        // 디버깅 출력 추가
        System.out.println("찾은 메시지 ID: " + message.getId());
        System.out.println("메시지에 기록된 작성자 ID: " + message.getAuthor().getId());

        if (newContent == null || newContent.isBlank()) {
            deleteMessage(messageId);
        } else {
            message.updateContent(newContent);
        }

        save(message);

        User author = userService.findById(message.getAuthor().getId());
        author.updateMessageInList(message);
        userService.save(author);

        Channel channel = channelService.findById(message.getChannel().getId());
        channel.updateMessageInList(message);
        channelService.save(channel);

        return message;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = findById(messageId);

        message.getChannel().removeMessage(message);
        channelService.save(message.getChannel());

        message.getAuthor().removeMessage(message);
        userService.save(message.getAuthor());

        try {
            Files.deleteIfExists(getFilePath(messageId));
        } catch (IOException e) {
            throw new RuntimeException("메시지 삭제 실패: " + messageId, e);
        }

    }

    @Override
    public void deleteMessagesByChannelId(UUID channelId) {
        executeBulkDelete(message -> message.getChannel().getId().equals(channelId));

    }

    @Override
    public void deleteMessagesByAuthorId(UUID authorId) {
        executeBulkDelete(message -> message.getAuthor().getId().equals(authorId));

    }

    private void executeBulkDelete(java.util.function.Predicate<Message> condition) {
        try (Stream<Path> stream = Files.list(DIRECTORY)) {
            stream.filter(path -> path.toString().endsWith(".ser"))
                    .forEach(path -> {
                        Message msg = load(path);
                        if (condition.test(msg)) {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                System.err.println("파일 일괄 삭제 중 오류: " + path);
                            }
                        }
                    });
        } catch (IOException e) {
            throw new RuntimeException("일괄 삭제 작업 실패", e);
        }
    }
}
