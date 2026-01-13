package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, List<Message>> data;        // key: ChannelUuid
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID channelId, UUID userId, String message) {
        channelService.findChannel(channelId).orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다"));
        userService.findUser(userId).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다"));

        Message msg = new Message(channelId, userId, message);
        data.computeIfAbsent(channelId, k -> new ArrayList<>()).add(msg);
        return msg;
    }

    @Override
    public Optional<Message> findMessage(UUID uuid) {
        return findAllMessages().stream()
                .filter(m -> Objects.equals(m.getId(), uuid)).findFirst();
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID uuid) {
        return data.computeIfAbsent(uuid, k -> new ArrayList<>());
    }

    @Override
    public List<Message> findAllMessages() {
        return data.values().stream().flatMap(List::stream).toList();
    }

    @Override
    public Message updateMessage(UUID uuid, String newMessage) {
        Message msg = findMessage(uuid).orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지입니다"));

        if (!Objects.equals(msg.getMessage(), newMessage)) {
            msg.updateMessage(newMessage);
            msg.updateUpdatedAt();
        }

        return msg;
    }

    @Override
    public void deleteMessage(UUID uuid) {
        Message msg = findMessage(uuid).orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지입니다"));
        for (var list : data.values()) {
            if (list.remove(msg)) {
                return;
            }
        }
    }
}
