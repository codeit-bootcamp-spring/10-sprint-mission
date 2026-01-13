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
    public Message create(UUID userId, UUID channelId, String content) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        if (channel.getMemberIds().contains(userId)) {
            throw new IllegalStateException("해당 채널에 속해 있지 않으므로 메세지를 보낼 수 없습니다.");
        }

        Message message = new Message(user, channel, content);
        messages.add(message);
        return message;
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
    public Message update(UUID id, String content) {
        Message updateContent = findById(id);
        updateContent.updateMessage(content);
        return updateContent;
    }

    @Override
    public void delete(UUID uuid) {
        Message message = findById(uuid);
        messages.remove(message);
    }
}
