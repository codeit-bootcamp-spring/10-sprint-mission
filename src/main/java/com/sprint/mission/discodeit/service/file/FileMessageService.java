//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.MessageService;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.UUID;
//
//public class FileMessageService implements MessageService {
//    private List<Message> data;
//    private FileChannelService channelService;
//    private FileUserService userService;
//
//    public FileMessageService() {
//        this.data = new ArrayList<>();
//        this.channelService = new FileChannelService();
//        this.userService = new FileUserService();
//    }
//    public FileMessageService(FileChannelService channelService, FileUserService userService) {
//        this.data = new ArrayList<>();
//        this.channelService = channelService;
//        this.userService = userService;
//    }
//
//    // 직렬화 - 저장 로직
//    public void serialize(List<Message> messages) {
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("messages.ser"))) {
//            oos.writeObject(messages);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 역직렬화 - 저장 로직
//    public List<Message> deserialize() {
//        List<Message> newMessage = List.of();
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("messages.ser"))) {
//            newMessage = (List<Message>) ois.readObject();
////            System.out.println(newMessage);
//        } catch (IOException | ClassNotFoundException e) {
//            System.out.println("역직렬화가 안됨");
//        }
//        return newMessage;
//    }
//
//    @Override
//    public Message create(String msg, UUID userId, UUID channelId) {
//        User user = userService.findById(userId);
//        Channel channel = channelService.findById(channelId);
//        Message message = new Message(msg, user, channel);
//        this.data.add(message);
//        serialize(this.data);
//        return message;
//    }
//
//    @Override
//    public Message findById(UUID id) {
//        for (Message message : deserialize()) {
//            if (message.getId().equals(id)) {
//                return message;
//            }
//        }
//        throw new NoSuchElementException();
//    }
//
//    @Override
//    public List<Message> findAll() {
//        return deserialize();
//    }
//
//    @Override
//    public Message updateMessageData(UUID id, String messageData) {
//        this.data = deserialize();
//        for (Message message : this.data) {
//            if (message.getId().equals(id)) {
//                message.updateText(messageData);
//                serialize(this.data);
//                return message;
//            }
//        }
//        throw new NoSuchElementException();
//    }
//
//    @Override
//    public void delete(UUID id) {
//        this.data = deserialize();
//        for (Message message : this.data) {
//            if (message.getId().equals(id)) {
//                this.data.remove(message);
//                serialize(this.data);
//                break;
//            }
//        }
//    }
//
//    // 특정 유저가 발행한 메시지 리스트 조회
//    @Override
//    public List<Message> readUserMessageList(UUID userId) {
//        User user = userService.findById(userId);
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
