package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService,ChannelService channelService){
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(Message message){
        if(userService.findById(message.getSenderId()) == null){
            throw new IllegalArgumentException("존재하지 않는 사용자 입니다.");
        }
        if(channelService.findById(message.getChannelId()) == null){
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        data.put(message.getId(),message);
        return message;

    }

    @Override
    public Message findById(UUID id){
        return data.get(id);
    }

    //특정 User의 모든 메세지 목록
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(UUID id,String content){
        Message message = data.get(id);
        if(message != null){
            message.setContent(content);
        }
        return message;
    }

    @Override
    public void delete(UUID id) {
        data.remove(id);
    }


}
