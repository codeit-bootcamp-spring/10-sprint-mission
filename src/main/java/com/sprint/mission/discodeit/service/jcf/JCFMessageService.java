package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.UUID;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JCFMessageService implements MessageService{
    private final Map<UUID, Message> data = new HashMap<>();;
    private final UserService userService;
    private final ChannelService channelService;


    public JCFMessageService(UserService userService, ChannelService channelService){
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void create(Message message) {
        validateCreateMessage(message);
        data.put(message.getId(), message);
    }

    @Override
    public Message readById(UUID id){
        return data.get(id);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void update(Message message) {
        if (data.containsKey(message.getId())) {
            data.put(message.getId(), message);
        }
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }

    // 검증
    private void validateCreateMessage(Message message) {

        // 유저 존재 확인
        userService.validateUserStatus(message.getUser().getId());

        // 채널 존재 확인
        channelService.validateChannelStatus(message.getChannel().getId());

        // 채널 멤버 확인
        if (!message.getChannel().isMember(message.getUser())) {
            throw new IllegalArgumentException("실패: 채널 멤버만 메시지를 작성할 수 있음");
        }
    }

}
