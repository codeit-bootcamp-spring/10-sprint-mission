package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final ChannelService channelService;
    private final UserService userService;
    private final List<Message> data;

    public JCFMessageService(ChannelService channelService, UserService userService) {
        this.channelService = channelService;
        this.userService = userService;
        this.data = new ArrayList<>();
    }

    @Override
    public Message create(String text, UUID channelId,  UUID userId) {
        channelService.joinChannel(channelId, userId);

        User user = userService.findUserById(userId);
        Channel channel = channelService.findChannelById(channelId);

        Message message = new Message(text, user, channel);

        data.add(message);
        return message;
    }

    //특정 채널에 특정 유저가 쓴 메세지 리스트 반환
    @Override
    public List<Message> findMessagesByUserAndChannel(UUID channelId,  UUID userId) {
        return data.stream().filter(message -> message.getUser().getId().equals(userId))
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    //특정 채널에 발행된 메시지 리스트 조회
    @Override
    public List<Message> findMessagesByChannel(UUID channelId) {
        return data.stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    //특정 사용자의 발행한 메시지 리스트 조회
    @Override
    public List<Message> findMessagesByUser(UUID userId) {
        return data.stream()
                .filter(message -> message.getUser().getId().equals(userId))
                .toList();
    }

    //모든 채널의 모든 메세지리스트 반환
    @Override
    public List<Message> findAllMessages() {
        return new ArrayList<>(data);
    }

    @Override
    public Message findMessage(UUID messageId) {
        return data.stream().filter(message -> message.getId().equals(messageId))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지 아이디입니다."));
    }

    @Override
    public Message update(UUID messageId, String text) {
        Message message = findMessage(messageId);
        message.update(text);
        return message;
    }

    @Override
    public void saveOrUpdate(Message message) {
        //동기화용 메서드
    }

    @Override
    public void delete(UUID messageId) {
        Message message = findMessage(messageId);
        data.remove(message);
    }
}
