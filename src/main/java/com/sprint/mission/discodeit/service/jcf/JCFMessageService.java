package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.utils.CheckValidation;

import java.util.*;

public class JCFMessageService implements MessageService {

    List<Message> data = new ArrayList<>();
    ChannelService channelService;
    UserService userService;
    MessageRepository messageRepository;


    // 메시지 서비스 생성자입니다.
    // 메시지라는 객체 자체가 유저와 채널에 의존적인 객체이기 때문에,
    // 메시지 서비스 생성 시 원활한 상호작용과 관계 관리를 위해
    // 채널 서비스, 유저 서비스를 주입받아야 합니다.
    public JCFMessageService(MessageRepository messageRepository, ChannelService jcfChannel, UserService jcfUser){
        this.messageRepository = messageRepository;
        this.channelService = jcfChannel;
        this.userService = jcfUser;
    }

    // 메시지를 생성합니다.
    @Override
    public Message createMessage(String context, UUID channelID, String userID) {
        Objects.requireNonNull(context, "유효하지 않은 매개변수입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");

        // 채널/유저가 각 서비스 data 리스트에 있는지 검증합니다.
        // 존재하지 않으면 IllegalStateException을 MessageHelper(호출자)로 던집니다.
        Channel channel = channelService.readAllChannels().stream()
                .filter(c -> channelID.equals(c.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("채널이 존재하지 않습니다."));

        User user = userService.readAllUsers().stream()
                .filter(u -> userID.equals(u.getUserId()))
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

    // 메세지의 uuid를 식별자 삼아서 읽고 메시지를 리턴합니다.
    public Message readMessage(UUID uuid){
          return CheckValidation.readEntity(data, uuid, () -> new IllegalStateException("유효하지 않은 객체입니다."));
    }

    // 유저ID를 식별자 삼아서 해당 유저가 작성한 메시지를 리스트로 리턴합니다.
    public List<Message> readMessagebyUser(String userID){
        User user = userService.readUser(userID);
        List<Message> messageList = new ArrayList<>(); // 리턴할 리스트를 초기화

        // 메시지 서비스의 data에서 입력받은 유저ID와 일치하는 메시지를 찾고 messageList에 삽입.
        data.stream().filter(
                        m -> user.getUserId().equals(m.getUser().getUserId()))
                .forEach(messageList::add);

        System.out.println(messageList);
        return messageList;
    }

    // 채널의 UUID를 식별자로 사용하여 해당 채널에서 발행된 메세지를 List로 리턴합니다.
    public List<Message> readMessagebyChannel(UUID channelID){
        Channel channel = channelService.readChannel(channelID);
        List<Message> messageList = new ArrayList<>(); // 결과물을 담을 리스트

        data.stream().filter(
                m-> channel.getId().equals(m.getChannel().getId()))
                .forEach(messageList::add);
        System.out.println(messageList);
        return messageList;
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

    @Override
    public void save(Message message) {
        messageRepository.save(message);
    }

    @Override
    public void setUserService(UserService userService) {

    }

    @Override
    public void setChannelService(ChannelService channelService) {

    }


}
