//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.IsPrivate;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class JCFMessageService implements MessageService {
//    private final UserService userService;
//    private final ChannelService channelService;
//    private final MessageRepository messageRespotory;
//
//    public JCFMessageService(UserService userService, ChannelService channelService, MessageRepository messageRespotory) {
//        this.userService = userService;
//        this.channelService = channelService;
//        this.messageRespotory = messageRespotory;
//    }
//
//    @Override
//    public Message create(UUID userId, UUID channelId, String bytes) {
//        User user = userService.findById(userId);
//        Channel channel = channelService.findById(channelId);
//        Message message = new Message(user, channel, bytes);
//        channel.addMessage(message);    // 채널에 메시지 추가
//        return messageRespotory.save(message);
//    }
//
//    @Override
//    public Message findById(UUID id) {
//        Message message = messageRespotory.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("실패 : 존재하지 않는 메시지 ID입니다."));
//        return message;
//    }
//
//    @Override
//    public List<Message> readAll() {
//        return messageRespotory.readAll();
//    }
//
//    @Override
//    public Message update(UUID messageId, String newContent) {
//        Message message = findById(messageId);
//        message.updateContent(newContent);
//        return messageRespotory.save(message);
//    }
//
//    @Override
//    public List<Message> searchMessage(UUID channelId, String msg) {
//        Channel channel = channelService.findById(channelId);
//        List<Message> messages = messageRespotory.readAll().stream()
//                .filter(m -> m.getChannelId().equals(channelId))
//                .filter(m -> m.getContent().contains(msg))
//                .collect(Collectors.toList());
//
//        return messages;
//    }
//    @Override
//    public UUID sendDirectMessage(UUID senderId, UUID receiverId, String bytes) {
//        Channel dmChannel = getOrCreateDMChannel(senderId, receiverId);
//
//        User sender = userService.findById(senderId);
//        Message message = new Message(sender, dmChannel, bytes);
//
//        dmChannel.addMessage(message);
//        messageRespotory.save(message);
//        return dmChannel.getId();
//    }
//
//    private Channel getOrCreateDMChannel(UUID user1Id, UUID user2Id) {
//        User user1 = userService.findById(user1Id);
//        User user2 = userService.findById(user2Id);
//
//        return user1.getChannels().stream()
//                .filter(c -> c.getIsPrivate() == IsPrivate.PRIVATE)
//                .filter(c -> c.getUsers().size() == 2)
//                .filter(c -> c.getUsers().stream().anyMatch(u -> u.equals(user2)))
//                .findFirst()
//                .orElseGet(() -> {
//                    Channel newDmChannel = channelService.create("DM - " + user1.getName() + "-" + user2.getName(), IsPrivate.PRIVATE, user1.getId());
//                    newDmChannel.addUser(user2);
//                    return newDmChannel;
//                });
//    }
//
//    @Override
//    public void delete(UUID id) {
//        messageRespotory.delete(id);
//    }
//
//}
