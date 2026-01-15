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
    private final UserService userService;
    private final ChannelService channelService;

    public JCFMessageService(UserService userService,ChannelService channelService){
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message create(String content, UUID userId, UUID channelId){
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);
        Message message = new Message(content,user,channel);

        data.put(message.getId(),message);
        user.getMessageList().add(message);
        return message;

    }

    @Override
    public Message findById(UUID id){
        if(data.get(id) == null){
            throw  new IllegalArgumentException("메시지가 존재하지 않습니다.");
        }
        return data.get(id);
    }

    //특정 User의 모든 메세지 목록
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }


    public List<Message> findByUser(UUID userId){
        User user = userService.findById(userId);
        return user.getMessageList();
    }

    @Override
    public Message update(UUID id,String content){
        Message message = findById(id);
        message.setContent(content);
        return message;
    }

    @Override
    public Message delete(UUID id) {
        Message message = findById(id);
        data.remove(id);
        return message;
    }


}
