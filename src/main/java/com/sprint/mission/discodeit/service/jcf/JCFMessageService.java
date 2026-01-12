package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final ChannelService channelService;
    private final UserService userService;
    public JCFMessageService(JCFUserService userService, JCFChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(Channel channel, User author, String content) {
        validateMessage(channel, author, content);
        Message message = new Message(channel, author, content);
        data.put(message.getId(), message);
        return message;
    }

    private void validateMessage(Channel channel, User author, String content) {
        if(channel == null || channel.getId() == null) throw new IllegalArgumentException("채널 정보가 없습니다");
        if(author == null || author.getId() == null) throw new IllegalArgumentException("유저 정보가 없습니다");
        if(content == null || content.isBlank()) throw new IllegalArgumentException("내용을 다시 입력해주세요");
        if(channelService.getChannel(channel.getId()) == null) throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        if(userService.getUser(author.getId()) == null) throw new IllegalArgumentException("존재하지 않는 유저입니다.");
    }
    @Override
    public Message getMessage(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Message> getAllMessages() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void updateMessage(String content, UUID id) {
        Message message = data.get(id);
        message.update(content);
    }

    @Override
    public void deleteMessage(UUID id) {
        data.remove(id);
    }
}
