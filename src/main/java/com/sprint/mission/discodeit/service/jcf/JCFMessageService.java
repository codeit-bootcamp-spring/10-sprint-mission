//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.BaseEntity;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//
//public class JCFMessageService implements MessageService {
//    private final Map<UUID, Message> data;
//    private final UserService userService;
//    private final ChannelService channelService;
//
//    public JCFMessageService(UserService userService, ChannelService channelService) {
//        data = new HashMap<>();
//        this.userService = userService;
//        this.channelService = channelService;
//    }
//
//    @Override
//    public Message createMessage(UUID channelId, UUID userId, String message) {
//        Channel channel = channelService.getChannel(channelId);
//        User user = userService.getUser(userId);
//
//        // 참여여부 확인
//        if (channel.getParticipants().stream().noneMatch(u -> Objects.equals(u.getId(), user.getId()))) {
//            throw new IllegalStateException("채널에 소속되지 않은 유저입니다.");
//        }
//
//        Message msg = new Message(channel, user, message);
//        data.put(msg.getId(), msg);
//        channel.addMessage(msg);
//        user.addMessageHistory(msg);
//
//        return msg;
//    }
//
//    @Override
//    public Message getMessage(UUID uuid) {
//        return findAllMessages().stream()
//                .filter(m -> Objects.equals(m.getId(), uuid)).findFirst()
//                .orElseThrow(() -> new IllegalStateException("존재하지 않는 메시지입니다"));
//    }
//
//    @Override
//    public List<Message> findMessagesByChannelId(UUID uuid) {
//        Channel channel = channelService.getChannel(uuid);
//        return channel.getMessages();
//    }
//
//    @Override
//    public List<Message> findAllMessages() {
//        return data.values().stream()
//                .sorted(Comparator.comparing(BaseEntity::getCreatedAt))
//                .toList();
//    }
//
//    @Override
//    public Message updateMessage(UUID uuid, String newMessage) {
//        Message msg = getMessage(uuid);
//
//        Optional.ofNullable(newMessage).ifPresent(msg::updateMessage);
//        msg.updateUpdatedAt();
//
//        return msg;
//    }
//
//    @Override
//    public Message updateMessage(Message newMessage) {
//        return updateMessage(newMessage.getId(), null);
//    }
//
//    @Override
//    public void deleteMessage(UUID uuid) {
//        Message msg = getMessage(uuid);
//        Channel channel = msg.getChannel();
//        User user = msg.getUser();
//
//        user.removeMessageHistory(msg);
//        channel.removeMessage(msg);
//        data.remove(uuid);
//    }
//}
