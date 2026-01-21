package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Common;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileMessageService extends FileSerDe<Message> implements MessageService {
    private final String MESSAGE_DATA_DIRECTORY = "data/message";
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID channelId, UUID userId, String message) {
        Channel channel = channelService.getChannel(channelId);
        User user = userService.getUser(userId);

        // 참여여부 확인
        if (channel.getParticipants().stream().noneMatch(u -> Objects.equals(u.getId(), user.getId()))) {
            throw new IllegalStateException("채널에 소속되지 않은 유저입니다.");
        }

        Message msg = new Message(channel, user, message);
        this.save(MESSAGE_DATA_DIRECTORY, msg);
        channel.addMessage(msg);
        channelService.updateChannel(channel);
        user.addMessageHistory(msg);
        userService.updateUser(user);

        return msg;
    }

    @Override
    public Message getMessage(UUID uuid) {
        return findAllMessages().stream()
                .filter(m -> Objects.equals(m.getId(), uuid)).findFirst()
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지입니다"));
    }

    @Override
    public List<Message> findMessagesByChannelId(UUID uuid) {
        Channel channel = channelService.getChannel(uuid);
        return channel.getMessages();
    }

    @Override
    public List<Message> findAllMessages() {
        return loadAll(MESSAGE_DATA_DIRECTORY, Message.class).stream()
                .sorted(Comparator.comparingLong(Common::getCreatedAt))
                .toList();
    }

    @Override
    public Message updateMessage(UUID uuid, String newMessage) {
        Message msg = getMessage(uuid);

        Optional.ofNullable(newMessage).ifPresent(msg::updateMessage);
        msg.updateUpdatedAt();

        save(MESSAGE_DATA_DIRECTORY, msg);
        return msg;
    }

    @Override
    public void deleteMessage(UUID uuid) {
        Message msg = getMessage(uuid);
        Channel channel = channelService.getChannel(msg.getChannel().getId());
        User user = userService.getUser(msg.getUser().getId());

        user.removeMessageHistory(msg);
        userService.updateUser(user);
        channel.removeMessage(msg);
        channelService.updateChannel(channel);
        delete(MESSAGE_DATA_DIRECTORY, uuid);
    }
}
