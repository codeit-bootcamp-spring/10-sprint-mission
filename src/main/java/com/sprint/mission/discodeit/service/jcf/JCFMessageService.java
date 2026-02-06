//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//
//public class JCFMessageService implements MessageService {
//    private UserService userService;
//    private ChannelService channelService;
//    private final List<Message> data = new ArrayList<>();
//
//    public void setUserService(UserService userService) {
//        this.userService = userService;
//    }
//
//    public void setChannelService(ChannelService channelService) {
//        this.channelService = channelService;
//    }
//
//    @Override
//    public Message createMessage(String content, UUID senderId, UUID channelId) {
//        Message message = new Message(content, senderId, channelId);
//        data.add(message);
//        Channel channel = channelService.getChannel(channelId);
//        channel.addMessageId(message.getId());
//        User sender = userService.getUser(senderId);
//        sender.addMessageId(message.getId());
//        return message;
//    }
//
//    @Override
//    public Message getMessage(UUID messageId) {
//        return data.stream()
//                .filter(m -> m.getId().equals(messageId))
//                .findFirst()
//                .orElseThrow(() -> new NoSuchElementException("해당 메세지가 존재하지 않습니다."));
//    }
//
//    @Override
//    public List<Message> getAllMessages() {
//        return List.copyOf(data);
//    }
//
//    @Override
//    public List<Message> getMessagesByUserId(UUID userId) {
//        List<Message> result = new ArrayList<>();
//        userService.getUser(userId)
//                .getMessageIds()
//                .forEach(messageId -> result.add(getMessage(messageId)));
//        return result;
//    }
//
//    @Override
//    public List<Message> getMessagesByChannelId(UUID channelId) {
//        List<Message> result = new ArrayList<>();
//        channelService.getChannel(channelId)
//                .getMessageIds()
//                .forEach(messageId -> result.add(getMessage(messageId)));
//        return result;
//    }
//
//    @Override
//    public Message updateMessage(UUID messageId, String content) {
//        Message findMessage = getMessage(messageId);
//        Optional.ofNullable(content)
//                .ifPresent(findMessage::updateContent);
//        return findMessage;
//    }
//
//    @Override
//    public void deleteMessage(UUID messageId) {
//        Optional<Message> deleteMessage = data.stream()
//                .filter(message -> message.getId().equals(messageId))
//                .findAny();
//        if(deleteMessage.isEmpty()) return;
//        Message target = deleteMessage.get();
//        userService.getUser(target.getSenderId()).removeMessageId(messageId);
//        channelService.getChannel(target.getChannelId()).removeMessageId(messageId);
//        data.remove(target);
//    }
//}
