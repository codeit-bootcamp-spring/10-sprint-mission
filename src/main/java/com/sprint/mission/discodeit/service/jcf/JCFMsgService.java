package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MsgService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMsgService implements MsgService {

    List<Message> data = new ArrayList<>();


    @Override
    public Message createMessage(String context, Channel channel, User user) {
        if(context == null || channel == null || user == null){
            return null;
        }

        Message message = new Message(context, channel, user);
        data.add(message);
        return message;
    }

    @Override
    public List<Message> readMessageByChannel(Channel channel) {
        List<Message> msgList = new ArrayList<Message>();
        // TODO : 채널에 축적된 메세지를 List<Message> 형태로 가공 후 리턴해야 함.
        data.stream().filter(msg -> channel.equals(msg.getChannel())).forEach(msgList::add);
        return msgList;
    }

    @Override
    public List<Message> readMessageByAuthor(User user) {
        List<Message> msgList = new ArrayList<Message>();
        // TODO : 채널에 축적된 메세지를 List<Message> 형태로 가공 후 리턴해야 함.
        data.stream()
                .filter(msg->user.equals(msg.getUser()))
                .forEach(msgList::add);
        return msgList;
    }


    @Override
    public boolean updateMessage(UUID msgID, String msgContext ) {
        Optional<Message> target = data
                .stream()
                .filter(msg -> msgID.equals(msg.getId()))
                .findFirst();

        if(msgContext != null && target.isPresent()){
            target.get().updateContext(msgContext);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteMessage(UUID msgID) {
        Optional<Message> target = data
                .stream()
                .filter(msg -> msgID.equals(msg.getId()))
                .findFirst();

        target.ifPresent(message -> data.remove(message));
        return true;
    }

    @Override
    public ArrayList<Message> readAllMessage() {
        data.forEach(System.out::println);
        return (ArrayList<Message>) data;
    }
}
