package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;

import com.sprint.mission.discodeit.service.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> messageDB = new HashMap<>();

    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(String content, UUID userId, UUID channelId) {
        User sender = userService.findUserByUserId(userId);
        Channel channel = channelService.findChannelById(channelId);

        boolean isMember = channel.getChannelUserRoles().stream()
                .anyMatch(role -> role.getUser().getId().equals(userId));
        if (!isMember) {
            throw new IllegalArgumentException("해당 채널에 참여하지 않은 유저는 메시지를 보낼 수 없습니다.");
        }

        Message message = new Message(content, sender, channel);
        messageDB.put(message.getId(), message);
        channel.addMessage(message);
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
        return message;
    }

    @Override
    public void deleteMessage(UUID messageId) {
        Message message = findMessageById(messageId);

        Channel channel = channelService.findChannelById(message.getChannel().getId());
        channel.removeMessage(message);

        messageDB.remove(message.getId());
        System.out.println("메시지 삭제 완료되었습니다. (messageId: " + message.getId() + " )");
    }
    @Override
    public void deleteAllMessagesByUserId(UUID userId) {
        List<Message> userMessages = messageDB.values().stream()
                .filter(message -> message.getSender().getId().equals(userId))
                .toList();

        // TODO: 해당 순회 성능문제 심할 것 같음 => 다른 방법 강구 요망
        for (Message msg : userMessages) {
            try {
                Channel channel = channelService.findChannelById(msg.getChannel().getId());
                channel.removeMessage(msg);
            } catch (IllegalArgumentException e) {
                // 이미 채널이 삭제된 경우 무시하고 진행
                // TODO: 다른 예외 상황 발생할 수 있는 지 확인 필요
                // 유저 삭제 -> 유저가 참여하고 있는 채널 관계 삭제 -> 해당 채널 내 삭제하려는 유저가 작성한 메시지 삭제 => 이런 흐름으로 전면 교체한 것도 로직상 고려 필요
            }
        }
        messageDB.values().removeAll(userMessages);
        System.out.println("[5] 해당 유저가 작성한 모든 메시지를 삭제했습니다. 삭제된 메시지 수: " + userMessages.size()
                + "\n\tusername: " + userService.findUserByUserId(userId).getUsername() );
    }
    @Override
    public void deleteAllMessagesByChannelId(UUID channelId) {
        messageDB.values().removeIf(msg -> msg.getChannel().getId().equals(channelId));
        System.out.println("\t\t[1] 채널 내 모든 메시지 삭제 완료. (채널 삭제 사전 작업 1)" +
                "\n\t\t\t channelId: " + channelId
        );
    }
}