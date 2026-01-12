package com.sprint.mission.discodeit.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private JCFUserService userService;
    private JCFChannelService channelService;

    public JCFMessageService(JCFUserService userService, JCFChannelService channelService) {
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(Message message) {

        if((userService.read(message.getSender().getId())) == null){
            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
        }
        if ((channelService.read(message.getChannelId())) == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message read(UUID id) {
        if(!data.containsKey(id)){
            throw new NoSuchElementException("조회 실패 : 해당 ID의 메시지를 찾을 수 없습니다.");
        }
        return data.get(id);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(Message message) {
        if (!data.containsKey(message.getId())) {
            throw new NoSuchElementException("수정 실패 : 존재하지 않는 메시지 ID입니다.");
        }
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public void delete(UUID id) {
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("삭제 실패 : 존재하지 않는 메시지 ID입니다.");
        }
        data.remove(id);
    }

}
