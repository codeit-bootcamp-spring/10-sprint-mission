package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {

    private static final String ROOT_PATH = System.getProperty("user.dir") + "/data";
    private final Path dirPath;
    private final ChannelService channelService;
    private final UserService userService;

    public FileMessageService(ChannelService channelService, UserService userService) {
        this(channelService, userService, ROOT_PATH);
    }

    public FileMessageService(ChannelService channelService, UserService userService, String rootPath) {
        this.dirPath = Paths.get(rootPath, "message");
        this.channelService = channelService;
        this.userService = userService;
        init();
    }

    @Override
    public Message create(String text, UUID channelId, UUID userId) {
        if (!channelService.isUserInChannel(channelId, userId)) {
            throw new IllegalArgumentException("채널에 참여하지 않은 유저는 메시지를 보낼 수 없습니다.");
        }

        User user = userService.findUserById(userId);
        Channel channel = channelService.findChannelById(channelId);

        Message message = new Message(text, user, channel);

        save(message);
        return message;
    }

    //특정 채널에 특정 유저가 쓴 메세지 리스트 반환
    @Override
    public List<Message> findMessagesByUserAndChannel(UUID channelId, UUID userId) {
        return load().stream().filter(message -> message.getUser().getId().equals(userId))
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    //특정 채널에 발행된 메시지 리스트 조회
    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {
        return load().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    //특정 사용자의 발행한 메시지 리스트 조회
    @Override
    public List<Message> findMessagesByUser(UUID userId) {
        return load().stream()
                .filter(message -> message.getUser().getId().equals(userId))
                .toList();
    }

    //모든 채널의 모든 메세지리스트 반환
    @Override
    public List<Message> findAllMessages() {
        return load();
    }

    @Override
    public Message findMessageById(UUID messageId) {
        return load().stream().filter(message -> message.getId().equals(messageId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지 아이디입니다."));
    }

    @Override
    public Message update(UUID messageId, String text) {
        Message message = findMessageById(messageId);
        message.update(text);
        save(message);
        return message;
    }

    @Override
    public void delete(UUID messageId) {
        File file = new File(dirPath.toFile(), messageId.toString() + ".ser");
        if (file.exists()) {
            file.delete();
        }
    }

    private void init() {
        if (!Files.exists(dirPath)) {
            try {
                Files.createDirectories(dirPath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void save(Message message) {
        File file = new File(dirPath.toFile(), message.getId().toString() + ".ser");
        try (
                FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Message> load() {
        if(!Files.exists(dirPath)) {
            return new ArrayList<>();
        }
        try {
            List<Message> list = Files.list(dirPath)
                    .map(path -> {
                        try (
                                FileInputStream fis = new FileInputStream(path.toFile());
                                ObjectInputStream ois = new ObjectInputStream(fis)
                        ) {
                            Object data = ois.readObject();
                            return (Message) data;
                        } catch (IOException | ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .collect(Collectors.toList());
            return list;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
