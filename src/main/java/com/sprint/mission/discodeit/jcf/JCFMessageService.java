//package com.sprint.mission.discodeit.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//
//public class JCFMessageService implements MessageService {
//    private final Map<UUID, Message> data;
//    private JCFUserService userService;
//    private JCFChannelService channelService;
//
//    public JCFMessageService(JCFUserService userService, JCFChannelService channelService) {
//        this.data = new HashMap<>();
//        this.userService = userService;
//        this.channelService = channelService;
//    }
//
//    @Override
//    public Message create(Message message) {
//
//        if((userService.read(message.getSenderId())) == null){
//            throw new IllegalArgumentException("존재하지 않는 유저입니다.");
//        }
//        if ((channelService.read(message.getChannelId())) == null) {
//            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
//        }
//        data.put(message.getId(), message);
//        return message;
//    }
//
//    @Override
//    public Message read(UUID id) {
//        return data.get(id);
//    }
//
//    @Override
//    public List<Message> readAll() {
//        return new ArrayList<>(data.values());
//    }
//
//    @Override
//    public Message update(Message message) {
//        data.put(message.getId(), message);
//        return message;
//    }
//
//    @Override
//    public void delete(UUID uuid) {
//        data.remove(uuid);
//    }
//}
