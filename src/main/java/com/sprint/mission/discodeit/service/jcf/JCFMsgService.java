package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MsgService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMsgService implements MsgService {

    List<Message> data = new ArrayList<>();
    ChannelService channelService;
    UserService userService;

    public JCFMsgService(ChannelService jcfChannel, UserService jcfUser){
        this.channelService = jcfChannel;
        this.userService = jcfUser;
    }

    @Override
    public Message createMessage(String context, Channel channel, User user) {
        Objects.requireNonNull(context, "유효하지 않은 매개변수입니다.");
        Objects.requireNonNull(channel, "유효하지 않은 채널입니다.");
        Objects.requireNonNull(user, "유효하지 않은 유저입니다.");

        // 채널/유저가 JCF구현부 data 리스트에 있는지 검증합니다.
        // 존재하지 않으면 IllegalStateException을 MessageHelper(호출자)로 던집니다.
        channelService.readAllChannels().stream()
                .filter(channel::equals)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("채널이 존재하지 않습니다."));

        userService.readAllUsers().stream()
                .filter(user::equals)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("유저가 존재하지 않습니다."));

        Message message = new Message(context, channel, user); // 검증이 마치게 되면 도메인 모델 생성.
        data.add(message); //jcf 구현부 data 리스트에 add
        return message;
    }

    @Override
    public List<Message> readMessageByChannel(Channel channel) {
        Objects.requireNonNull(channel, "해당 채널은 존재하지 않습니다.");
        List<Message> msgList = new ArrayList<Message>();
        // 채널에 축적된 메세지를 List<Message> 형태로 가공 후 리턴합니다.
        data.stream()
                .filter(msg -> channel.equals(msg.getChannel()))
                .forEach(msgList::add);

        System.out.println(msgList);
        return msgList;
    }

    @Override
    public List<Message> readMessageByAuthor(User user) {
        Objects.requireNonNull(user,"해당 유저는 존재하지 않습니다.");
        List<Message> msgList = new ArrayList<Message>();
        // 채널에 축적된 메세지를 List<Message> 형태로 가공 후 리턴합니다.
        data.stream()
                .filter(msg->user.equals(msg.getUser()))
                .forEach(msgList::add);

        System.out.println(msgList);
        return msgList;
    }


    @Override
    public Message updateMessage(UUID msgID, String msgContext ) {
        Objects.requireNonNull(msgID, "유효하지 않은 식별자 ID입니다.");

        Message message = data.stream()
                .filter(m -> msgID.equals(m.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지입니다."));
        message.updateContext(msgContext);
        return message;
    }

    @Override
    public Message deleteMessage(UUID msgID) {
        Objects.requireNonNull(msgID, "유효하지 않은 식별자 ID입니다.");

        Message message = data.stream()
                .filter(m -> msgID.equals(m.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지입니다."));

        data.remove(message);
        return message;
    }

    @Override
    public ArrayList<Message> readAllMessage() {
        data.forEach(System.out::println);
        return (ArrayList<Message>) data;
    }
}
