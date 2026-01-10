package com.sprint.mission.descodeit.service.jcf;

import com.sprint.mission.descodeit.entity.Channel;
import com.sprint.mission.descodeit.entity.Message;
import com.sprint.mission.descodeit.entity.User;
import com.sprint.mission.descodeit.service.ChannelService;
import com.sprint.mission.descodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private Map<UUID, Message> data;
    private ChannelService channelService;

    public JCFMessageService(){
        this.data = new HashMap<>();
        this.channelService = new JCFChannelService();
    }

    @Override
    public void create(Message message) {
        data.put(message.getId(), message);
    }

    @Override
    public Message findMessages(UUID MessageID) {
        System.out.println(data.get(MessageID));
        return data.get(MessageID);
    }

    @Override
    public Set<UUID> findAllMessages() {
        System.out.println("[메세지 전체 조회]");
        for(UUID id : data.keySet()){
            System.out.println(data.get(id));
        }
        return data.keySet();
    }

    @Override
    public void update(UUID messageID, String newText) {
        data.get(messageID).updateMessage(newText);
    }

    @Override
    public void delete(UUID messageID) {
        Message message = data.get(messageID);
        User user = message.getUser();
        user.getMessageList().remove(message);
        data.remove(messageID);
    }
}
