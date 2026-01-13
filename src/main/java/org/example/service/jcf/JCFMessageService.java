package org.example.service.jcf;

import org.example.entity.Channel;
import org.example.entity.Message;
import org.example.entity.User;
import org.example.service.ChannelService;
import org.example.service.MessageService;
import org.example.service.UserService;

// findbyid로 해도 되는거 아님???
// 예외처리 해야
// 생성자에서 객체 선언하는거 이해.
// create 잘 작성한건지??

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    private final Map<UUID, Message> data;
    private final UserService userService;
    private final ChannelService channelService;

    JCFMessageService(UserService userService, ChannelService channelService){  //생성자에 왜 이코드가 필요한지,
        this.data = new HashMap<>();
        this.userService = userService;
        this.channelService = channelService;
    }


    @Override
    public Message create(String content, UUID senderId, UUID channelId) { // 엔티티에서는 필드를 객체로 선언. 인터페이스에서는 UUID를 받게함. 근데 Message 생성자 부분에서는 객체를 받게끔 했음. 여기는 객체를 넣어야 하나?
        User sender = userService.findById(senderId);
        Channel channel = channelService.findById(channelId);
//        User sender = JFCUserService.findById(senderId);
        Message message = new Message(content,sender,channel);
        data.put(message.getId(),message);
        return message;
    }

    @Override
    public Message findById(UUID id){
        return data.get(id); // 그냥 리턴하면 안되나?
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findByChannel(UUID channelId) {
        return data.values().stream()
                .filter(message -> !message.isDeletedAt())  // 삭제된 것 제외
                .filter(message -> message.getChannel().getId().equals(channelId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Message> findBySender(UUID senderId) {
        return data.values().stream()
                .filter(message -> !message.isDeletedAt())  // 삭제된 것 제외
                .filter(message -> message.getSender().getId().equals(senderId))
                .collect(Collectors.toList());
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = findById(id);

        message.setContent(content);
        message.setEditedAt(true);
        return message;
    }

    @Override
    public void softDelete(UUID id) {
        Message message = findById(id); // findbyid로 해도 되는거 아님???
        message.setDeletedAt(true);
    }

    @Override
    public void hardDelete(UUID id) {
        data.remove(id);
    }

}
