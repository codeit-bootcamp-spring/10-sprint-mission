//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.exception.MessageNotFoundException;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.io.*;
//import java.util.*;
//
//
//public class FileMessageService implements MessageService {
//    private static final String FILE_PATH = "messages.dat";
//
//    private final UserService userService;
//    private final ChannelService channelService;
//
//    public FileMessageService(UserService userService, ChannelService channelService) {
//        this.userService = userService;
//        this.channelService = channelService;
//    }
//
//    // ======= 영속화 =======
//    private Map<UUID, Message> load() {
//        File file = new File(FILE_PATH);
//        if (!file.exists()) return new LinkedHashMap<>();
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
//            return (Map<UUID, Message>) ois.readObject();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    private void save(Map<UUID, Message> messages) {
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
//            oos.writeObject(messages);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    // ======= CRUD =======
//    @Override
//    public Message createMessage(UUID userId, UUID channelId, String content) {
//        // 1. 유저와 채널 객체 조회 (비즈니스 검증)
//        User sender = userService.findUser(userId);
//        Channel channel = channelService.findChannel(channelId);
//
//        Map<UUID, Message> messages = load();
//        Message message = new Message(sender, channel, content);
//        messages.put(message.getId(), message);
//        save(messages);
//        return message;
//    }
//
//    @Override
//    public Message findMessage(UUID messageId) {
//        Message message = load().get(messageId);
//        if (message == null) throw new MessageNotFoundException();
//        return message;
//    }
//
//    @Override
//    public List<Message> findAllMessage() {
//        return new ArrayList<>(load().values());
//    }
//
//    @Override
//    public List<Message> findAllByUserMessage(UUID userId) {
//        userService.findUser(userId);
//        List<Message> result = load().values().stream()
//                .filter(m -> m.getSender().getId().equals(userId))
//                .toList();
//        if (result.isEmpty()) throw new MessageNotFoundException();
//        return result;
//    }
//
//    @Override
//    public List<Message> findAllByChannelMessage(UUID channelId) {
//        channelService.findChannel(channelId);
//        List<Message> result = load().values().stream()
//                .filter(m -> m.getChannel().getId().equals(channelId))
//                .toList();
//        return result;
//    }
//
//    @Override
//    public Message updateMessage(UUID messageId, String newContent) {
//        Map<UUID, Message> messages = load();
//        Message message = messages.get(messageId);
//        if (message == null) throw new MessageNotFoundException();
//
//        message.updateContent(newContent);
//        save(messages);
//        return message;
//    }
//
//    @Override
//    public void deleteMessage(UUID messageId) {
//        Map<UUID, Message> messages = load();
//        if (messages.remove(messageId) == null) throw new MessageNotFoundException();
//        save(messages);
//    }
//}
