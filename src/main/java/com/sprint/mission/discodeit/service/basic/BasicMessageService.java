package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.jcf.ChannelService;
import com.sprint.mission.discodeit.service.jcf.MessageService;
import com.sprint.mission.discodeit.service.jcf.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class BasicMessageService implements MessageService {
    private final MessageRepository messageRepository;
    private UserService userService;
    private ChannelService channelService;

    public BasicMessageService(MessageRepository messageRepository){
        this.messageRepository = Objects.requireNonNull(messageRepository, "메세지 저장소가 유효하지 않음.");
    }

    public void setChannelService(ChannelService channelService){
        this.channelService = channelService;
    }

    public void setUserService(UserService userService){
        this.userService = userService;
    }


    @Override
    public Message createMessage(String context, UUID channelID, String userID) {
        Objects.requireNonNull(context, "유효하지 않은 매개변수입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");

        // 채널/유저가 각 서비스 data 리스트에 있는지 검증합니다.
        // 존재하지 않으면 IllegalStateException을 MessageHelper(호출자)로 던집니다.
        Channel channel = channelService.readChannel(channelID);
        User user = userService.readUser(userID);

        // 매개변수로 받은 채널과 유저가 종속관계인지 확인합니다. (유저가 해당 채널에 가입되어 있는지?)
        if(!channel.getUsers().contains(user)){
            throw new IllegalStateException("채널에 해당 유저가 존재하지 않습니다.");
        }

        Message message = new Message(context, channel, user); // 검증이 마치게 되면 도메인 모델 생성.
        messageRepository.save(message);
        return message;
    }

    @Override
    public Message readMessage(UUID uuid) {
        Objects.requireNonNull(uuid, "유효하지 않은 식별자");
        return messageRepository.findByID(uuid);
    }

    @Override
    public List<Message> readMessagebyUser(String userID) {
        Objects.requireNonNull(userID, "유효하지 않은 유저ID");

        return messageRepository.findAll()
                .stream()
                .filter(m -> userID.equals(m.getUser().getUserId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> readMessagebyChannel(UUID channelID) {
        Objects.requireNonNull(channelID, "유효하지 않은 채널ID");


        return messageRepository.findAll()
                .stream()
                .filter(m -> channelID.equals(m.getChannel().getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Message updateMessage(UUID uuid, String context) {
        Objects.requireNonNull(uuid, "유효하지 않은 식별자");
        Objects.requireNonNull(context, "유효하지 않은 내용");

        Message message = readMessage(uuid);
        message.updateContext(context);
        messageRepository.save(message);
        return message;
    }

    @Override
    public void deleteMessage(UUID uuid) {
        Objects.requireNonNull(uuid,"유효하지 않은 식별자");

        messageRepository.delete(messageRepository.findByID(uuid));
    }

    @Override
    public ArrayList<Message> readAllMessage() {
        return (ArrayList<Message>) List.copyOf(messageRepository.findAll());
    }
}
