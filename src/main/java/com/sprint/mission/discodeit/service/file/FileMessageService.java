package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private final String FILE_PATH = "message.dat";
    private final UserService userService;
    private final ChannelService channelService;

    public FileMessageService(UserService fileUserService, ChannelService fileChannelService){
        this.userService = fileUserService;
        this.channelService = fileChannelService;
    }

    private List<Message> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Message>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    // 객체 저장하기
    private void saveData(List<Message> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Message.dat"))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public Message createMessage(String content, UUID senderId, UUID channelId){
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("내용이 비어있거나 공백입니다.");
        }
        List<Message> data = loadData();
        User senderUser = userService.findById(senderId);
        Channel channel = channelService.findId(channelId);

        Message message = new Message(content, senderUser, channel);
        data.add(message);

        saveData(data);
        System.out.println("메세지 생성이 완료되었습니다.");
        return message;
    }

    public Message findId(UUID massageId){
        List<Message> data = loadData();
        return data.stream()
                .filter(message -> message.getId().equals(massageId))
                .findFirst()
                .orElse(null);
    }

    public List<Message> findAll(){
        return loadData();
    }

    public List<Message> findMessagesById(UUID userId){
        List<Message> data = loadData();
        return data.stream()
                .filter(message -> message.getUser().getId().equals(userId))
                .toList();
    }

    public List<Message> findMessagesByChannel(UUID channelId){
        List<Message> data = loadData();
        return data.stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    // 유저가 작성한 메세지 중 특정 채널에서의 메세지들
    public List<Message> findMessagesByUserAndChannel(UUID userId, UUID channelId){
        List<Message> messages = findMessagesById(userId);
        return messages.stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }


    public Message update(UUID massageId, String content){
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("내용이 비어있거나 공백입니다.");
        }
        List<Message> data = loadData();
        Message foundMsg = findId(massageId);
        data = loadData();
        data.add(foundMsg);
        saveData(data);
        return foundMsg;
    }


    public void delete(UUID massageId){
        Message target = findId(massageId);
        List<Message> data = loadData();
        data.remove(target);
        saveData(data);
    }
    @Override
    public void deleteAll() {
        List<Message> data = new ArrayList<>();
        saveData(data);
        System.out.println("모든 메시지 데이터를 초기화했습니다.");
    }
}
