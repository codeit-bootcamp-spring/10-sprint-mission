//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.MessageService;
//
//import java.util.*;
//
//public class JCFMessageService implements MessageService {
//    private final HashMap<UUID, Message> data;
//    private JCFChannelService channelService;
//    private JCFUserService userService;
//
//    public JCFMessageService() {
//        this.data = new HashMap<>();
//        this.channelService = new JCFChannelService();
//        this.userService = new JCFUserService();
//    }
//    public JCFMessageService(JCFChannelService channelService, JCFUserService userService) {
//        this.data = new HashMap<>();
//        this.channelService = channelService;
//        this.userService = userService;
//    }
//
//    @Override
//    public Message create(String msg, UUID userId, UUID channelId) {
//        User user = userService.find(userId);
//        Channel channel = channelService.findById(channelId);
//        Message message = new Message(msg, user, channel);
//        data.put(message.getId(), message);
//        return message;
//    }
//
//    @Override
//    public Message findById(UUID id) {
//        Message message = Optional.ofNullable(data.get(id)).orElseThrow(() -> new NoSuchElementException());
//        return message;
//    }
//
//    @Override
//    public List<Message> findAll() {
//        return new ArrayList<>(this.data.values());
//    }
//
//    @Override
//    public Message updateMessageData(UUID id, String messageData) throws NoSuchElementException{
//        this.findById(id).updateText(messageData);
//        return this.findById(id);
//    }
//
//    @Override
//    public void delete(UUID id) throws NoSuchElementException{
//        this.findById(id);
//        this.data.remove(id);
//    }
//
//    // 특정 유저가 발행한 메시지 리스트 조회
//    @Override
//    public List<Message> readUserMessageList(UUID userId) {
//        User user = userService.find(userId);
//        return user.getMessageList();
//    }
//
//    // 특정 채널의 발행된 메시지 목록 조회
//    @Override
//    public List<Message> readChannelMessageList(UUID channelId) {
//        Channel channel = channelService.findById(channelId);
//        return channel.getMessagesList();
//    }
//}
