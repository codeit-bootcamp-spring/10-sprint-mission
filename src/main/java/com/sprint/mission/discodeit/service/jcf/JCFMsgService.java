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

        // 매개변수로 받은 채널과 유저가 종속관계인지 확인합니다. (유저가 해당 채널에 가입되어 있는지?)
        if(!channel.getUsers().contains(user)){
            throw new IllegalStateException("채널에 해당 유저가 존재하지 않습니다.");
        }

        Message message = new Message(context, channel, user); // 검증이 마치게 되면 도메인 모델 생성.
        data.add(message); //jcf 구현부 data 리스트에 add
        return message;
    }

    public Message readMessage(UUID uuid){
          return data.stream()
                .filter(m -> uuid.equals(m.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 메시지는 존재하지 않습니다."));
    }



    @Override
    public Message updateMessage(UUID msgID, String msgContext ) {
        Objects.requireNonNull(msgID, "유효하지 않은 식별자 ID입니다.");

        Message message = readMessage(msgID);
        message.updateContext(msgContext);
        return message;
    }

    @Override
    public void deleteMessage(UUID msgID) {
        Objects.requireNonNull(msgID, "유효하지 않은 식별자 ID입니다.");
        Message message = readMessage(msgID);
        data.remove(message);

    }

    @Override
    public ArrayList<Message> readAllMessage() {
        data.forEach(System.out::println);
        return (ArrayList<Message>) data;
    }


}
