//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class FileMessageService implements MessageService {
//    private final UserService userService;
//    private final ChannelService channelService;
//
//    private static final String FILE_PATH = "messages.dat";
//    private final List<Message> messages;
//
//    public FileMessageService(UserService userService, ChannelService channelService) {
//        this.userService = userService;
//        this.channelService = channelService;
//        this.messages = loadFromFile();
//    }
//
//    @Override
//    public void save(Message message) {
//        saveToFile();
//    }
//
//    @Override
//    public Message createPublicChannel(UUID authorId, UUID channelId, String content) {
//        User user = userService.findById(authorId);
//        Channel channel = channelService.findById(channelId);
//
//        if (!channel.getMemberIds().contains(user)) {
//            throw new IllegalStateException("해당 채널에 속해 있지 않으므로 메세지를 보낼 수 없습니다.");
//        }
//
//        Message message = new Message(user, channel, content);
//        messages.add(message);
//        saveToFile();
//        return message;
//    }
//
//    @Override
//    public Message findById(UUID uuid) {
//        return messages.stream()
//                .filter(m -> m.getId().equals(uuid))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 메세지 입니다. ID: " + uuid));
//    }
//
//    @Override
//    public List<Message> findAll() {
//        return new ArrayList<>(messages);
//    }
//
//    @Override
//    public List<Message> findMessagesByChannelId(UUID channelId) {
//        return channelService.findById(channelId).getMessages();
//    }
//
//    @Override
//    public List<Message> findMessagesByUserId(UUID authorId) {
//        return userService.findById(authorId).getMessages();
//    }
//
//    @Override
//    public Message update(UUID id, String content) {
//        Message updateContent = findById(id);
//        updateContent.updateMessage(content);
//        saveToFile();
//        return updateContent;
//    }
//
//    @Override
//    public void delete(UUID messageId) {
//        Message message = findById(messageId);
//        if (message.getUser() != null) message.getUser().removeMessage(message);
//        if (message.getChannel() != null) message.getChannel().removeMessage(message);
//
//        // 실제 데이터 파기
//        messages.remove(message);
//        saveToFile();
//    }
//
//    @Override
//    public void deleteMessagesByUserId(UUID authorId) {
//        findMessagesByUserId(authorId).forEach(m-> delete(m.getId()));
//    }
//
//    @Override
//    public void deleteMessagesByChannelId(UUID channelId) {
//        findMessagesByChannelId(channelId).forEach(m-> delete(m.getId()));
//    }
//
//    // 직렬화
//    private void saveToFile() {
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
//            oos.writeObject(messages);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 역직렬화
//    @SuppressWarnings("unchecked")
//    private List<Message> loadFromFile() {
//        File file = new File(FILE_PATH);
//        if (!file.exists()) {
//            return new ArrayList<>();
//        }
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
//            return (List<Message>) ois.readObject();
//        } catch (IOException | ClassNotFoundException e) {
//            return new ArrayList<>();
//        }
//    }
//}
