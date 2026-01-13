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
    public Message create(String content, User user, Channel channel){
        Message message = new Message(content,user,channel);
        if(userService.findById(user) == null){
            throw new IllegalArgumentException("존재하지 않는 사용자 입니다.");
        }
        System.out.println(channelService.findById(channel));
        if(channelService.findById(channel) == null){
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }
        data.put(message.getId(),message);
        return message;

    }

    @Override
    public Message findById(Message message){
        if(data.get(message.getId()) == null){
            throw  new IllegalArgumentException("메시지가 존재하지 않습니다.");
        }
        return data.get(message.getId());
    }

    //특정 User의 모든 메세지 목록
    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Message update(Message message,String content){
        Message messages = data.get(message.getId());
        if(messages == null){
            throw new IllegalArgumentException("수정할 메시지가 없습니다.");
        }
        messages.setContent(content);
        return messages;
    }

    @Override
    public void delete(Message message) {
        if(data.get(message.getId()) == null){
            throw new IllegalArgumentException("삭제할 메시지가 존재하지 않습니다.");

        }
        data.remove(message.getId());
    }


}
