package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.config.FileStorageConfig;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {
    private Map<UUID, Message> messageDB = new HashMap<>();
    private final File file;

    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;

        File dataDir = FileStorageConfig.getDataDirectory();
        this.file = new File(dataDir, "messages.ser");
        if (file.exists()) load(); else persist();
    }

    @SuppressWarnings("unchecked")
    private void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.messageDB = (Map<UUID, Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("메시지 데이터 로드 중 오류" + e.getMessage());
            this.messageDB = new HashMap<>();
        }
    }

    private void persist() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.messageDB);
        } catch (IOException e) {
            System.err.println("메시지 데이터 저장 중 오류: " + e.getMessage());
        }
    }

    @Override
    public Message createMessage(String content, UUID userId, UUID channelId) {
        User sender  = userService.findUserByUserId(userId);
        Channel channel = channelService.findChannelById(channelId);

        // 주의: 파일 기반 구조에서 channel 객체는 channels.ser에서 로드된 별도의 객체일 수 있음
        // 따라서 channel.getChannelUserRoles()가 최신 상태인지 보장하기 어려울 수 있음 (추후 Repository 패턴으로 해결)
        boolean isMember = channel.getChannelUserRoles().stream()
                .anyMatch(role -> role.getUser().getId().equals(userId));

        // 일단 로직 흐름상 체크하지만, 현재 구조에서는 데이터 불일치로 실패할 가능성이 있음
        if (!isMember) {
            // 테스트 편의를 위해 임시로 주석 처리하거나, ChannelService가 최신 상태를 반환한다고 가정
            throw new IllegalArgumentException("해당 채널에 참여하지 않은 유저는 메시지를 보낼 수 없습니다.");
        }

        Message message = new Message(content, sender, channel);
        messageDB.put(message.getId(), message);

        // 메모리 상의 채널 객체에도 추가 (일관성 유지 노력)
        channel.addMessage(message);
        persist();
        return message;
    }

    @Override
    public Message findMessageById(UUID messageId) {
        Message message = messageDB.get(messageId);
        if (message == null) {
            throw new IllegalArgumentException("해당 id의 메시지가 존재하지 않습니다. (messageId: " + messageId + " )");
        }
        return message;
    }

    @Override
    public List<Message> findAllMessagesByChannelId(UUID channelId) {
        channelService.findChannelById(channelId);

        return messageDB.values().stream()
                .filter(msg -> msg.getChannel().getId().equals(channelId))
                .sorted(Comparator.comparing(Message::getCreatedAt)) // 작성 시간 순 정렬
                .collect(Collectors.toList());
    }

    @Override
    public Message updateMessage(UUID messageId, String newContent) {
        Message message = findMessageById(messageId);
        message.updateContent(newContent);
        persist();
        return message;
    }
    @Override
    public void deleteMessage(UUID messageId) {
        Message message = findMessageById(messageId);

        // 연관된 채널 객체에서도 메시지 제거 시도
        try {
            Channel channel = channelService.findChannelById(message.getChannel().getId());
            channel.removeMessage(message);
        } catch (IllegalArgumentException e) {
            // 채널이 이미 삭제되었거나 로드되지 않았을 수 있음
        }

        messageDB.remove(message.getId());

        persist();
        System.out.println("메시지 삭제 완료되었습니다. (file)");
    }

    @Override
    public void deleteAllMessagesByUserId(UUID userId) {
        List<Message> userMessages = messageDB.values().stream()
                .filter(message -> message.getSender().getId().equals(userId))
                .toList();

        for (Message msg : userMessages) {
            try {
                Channel channel = channelService.findChannelById(msg.getChannel().getId());
                channel.removeMessage(msg);
            } catch (IllegalArgumentException e) {
                // 채널이 이미 삭제되었거나 로드되지 않았을 수 있음
            }
        }
        messageDB.values().removeAll(userMessages);

        persist();
        System.out.println("해당 유저가 작성한 모든 메시지를 삭제했습니다.");
    }

    @Override
    public void deleteAllMessagesByChannelId(UUID channelId) {
        messageDB.values().removeIf(msg -> msg.getChannel().getId().equals(channelId));
        persist();
        System.out.println("채널 내 메시지 삭제 완료되었습니다. (File)");
    }
}
