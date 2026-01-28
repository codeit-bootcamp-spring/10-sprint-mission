package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private static MessageService instance;
    private final List<Message> data;

    private final UserService userService;
    private final ChannelService channelService;

    private JCFMessageService() {
        this.data = new ArrayList<Message>();
        this.userService = JCFUserService.getInstance();
        this.channelService = JCFChannelService.getInstance();
    }

    public static MessageService getInstance() {
        if (instance == null) instance = new JCFMessageService();
        return instance;
    }

    // data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.
    @Override
    public Message create(UUID userId, String text, UUID channelId) throws RuntimeException {
        // 메시지를 생성 전, 유저가 해당 채널에 속해있는지 확인한다.
        Channel channel = channelService.findById(channelId);
        User user = userService.findById(userId);

        if (user.getChannelList().stream()
            .noneMatch(ch -> ch.getId().equals(channelId))) {
            throw new IllegalArgumentException(
                user.getUserName() + "님은 " + channel.getName() + " 채널에 속해있지 않아 메시지를 보낼 수 없습니다."
            );
        }

        Message newMessage = new Message(user, text, channel);
        data.add(newMessage);

        // channel과 user의 messageList에 현재 message를 add
        channel.addMessage(newMessage);
        user.addMessage(newMessage);

        return newMessage;
    }

    @Override
    public Message findById(UUID id) {
        return data.stream()
            .filter(message -> message.getId().equals(id))
            .findFirst()
            .orElseThrow(
                () -> new NoSuchElementException("id가 " + id + "인 메시지는 존재하지 않습니다.")
            );
    }

    @Override
    public List<Message> findByUser(UUID userId) {
        // 해당 id를 갖는 user 객체의 메시지 리스트를 반환하도록 변경
        return userService.findById(userId).getMessageList();

    }


    @Override
    public List<Message> findByChannel(UUID channelId) {
        // 해당 id를 갖는 channel 객체의 메시지 리스트를 반환하도록 변경
        return channelService.findById(channelId).getMessageList();
    }

    @Override
    public Message updateById(UUID messageId, String text) {
        Message message = data.stream()
            .filter(msg -> msg.getId().equals(messageId))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException("id가 " + messageId + "인 메시지는 존재하지 않습니다."));

        message.updateText(text);

        return message;
    }

    @Override
    public void delete(UUID messageId) {
        if (!data.removeIf(message -> message.getId().equals(messageId)))
            throw new NoSuchElementException("id가 " + messageId + "인 메시지는 존재하지 않습니다.");
    }
}
