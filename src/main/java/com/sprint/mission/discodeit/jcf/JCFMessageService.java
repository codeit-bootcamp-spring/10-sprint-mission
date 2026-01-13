package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.IsPrivate;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService, ChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);

        Message message = new Message(user, channel, content);
        channel.addMessage(message);    // 채널에 메시지 추가
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message findById(UUID id) {
        validateExistence(data, id);
        return data.get(id);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID messageId) {
        validateExistence(data, messageId);
        Message message = findById(messageId);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public void searchMessage(UUID channelId, String msg) {
        Channel channel = channelService.findById(channelId);
        String result = channel.getMessages().stream()
                .filter(m -> m.getContent().contains(msg))
                .map(m -> "- " + m.getContent())
                .collect(Collectors.joining("\n"));
        System.out.println("[" + channel.getName() + "] : " + msg);

        if (!result.isEmpty()) {
            System.out.println(result);
        }
        else {
            System.out.println("찾는 내용이 없습니다.");
        }
    }
    @Override
    public UUID sendDirectMessage(UUID senderId, UUID receiverId, String content) {
        Channel dmChannel = getOrCreateDMChannel(senderId, receiverId);

        User sender = userService.findById(senderId);
        Message message = new Message(sender, dmChannel, content);

        dmChannel.addMessage(message);

        return dmChannel.getId();
    }

    private Channel getOrCreateDMChannel(UUID user1Id, UUID user2Id) {
        User user1 = userService.findById(user1Id);
        User user2 = userService.findById(user2Id);

        return user1.getChannels().stream()
                .filter(c -> c.getIsPrivate() == IsPrivate.PRIVATE)
                .filter(c -> c.getUsers().size() == 2)
                .filter(c -> c.getUsers().stream().anyMatch(u -> u.equals(user2)))
                .findFirst()
                .orElseGet(() -> {
                    Channel newDmChannel = channelService.create("DM - " + user1.getName() + "-" + user2.getName(), IsPrivate.PRIVATE, user1.getId());
                    newDmChannel.addUser(user2);
                    return newDmChannel;
                });
    }



    @Override
    public void delete(UUID id) {
        validateExistence(data, id);
        data.remove(id);
    }

    private void validateExistence(Map<UUID, Message> data, UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("실패 : 존재하지 않는 메시지 ID입니다.");
        }
    }
}
