package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private final List<Message> messages =  new ArrayList<>();

    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void create(UUID userId, UUID channelId, String content) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Message message = new Message(user, channel, content);
        messages.add(message);
    }

    @Override
    public void create(Message entity) {
        throw new UnsupportedOperationException("Message는 유저와 채널 ID를 통한 생성을 권장합니다.");
    }

    @Override
    public Message findById(UUID uuid) {
        return messages.stream()
                .filter(m -> m.getId().equals(uuid))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지 입니다. ID: " + uuid));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(messages);
    }

    @Override
    public void update(UUID uuid, Message entity) {
        Message message = findById(uuid);
    }

    @Override
    public void delete(UUID uuid) {
        Message message = findById(uuid);
        messages.remove(message);
    }
}
