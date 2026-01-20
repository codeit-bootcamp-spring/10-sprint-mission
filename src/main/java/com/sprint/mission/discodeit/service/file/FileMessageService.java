package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.io.*;
import java.util.*;

public class FileMessageService implements MessageService {
    private final String FILE_PATH = "./messages.ser";
    private final FileUserService fileUserService = new FileUserService();
    private final FileChannelService fileChannelService = new FileChannelService();

    // 파일 쓰기 (직렬화)
    private void saveData(Map<UUID, Message> data){
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)){

            oos.writeObject(data);

        } catch (IOException e){
            System.err.println("[파일 저장 실패]: " + e.getMessage());
        }
    }

    // 파일 읽기 (역직렬화)
    private Map<UUID, Message> loadData(){
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)){

            return (Map<UUID, Message>) ois.readObject();

        } catch (IOException | ClassNotFoundException e){
            return new HashMap<>();
        }
    }


    // 메시지 생성
    @Override
    public Message create(String content, UUID userId, UUID channelId) {
        validateAccess(userId, channelId); // 권한 확인

        Map<UUID, Message> messageData = loadData();

        User author = fileUserService.findById(userId);
        Channel channel = fileChannelService.findById(channelId);

        Message newMessage = new Message(content,author, channel);
        messageData.put(newMessage.getId(), newMessage);

        author.addMessage(newMessage);
        channel.addMessage(newMessage);

        saveData(messageData);
        fileUserService.update(author);
        fileChannelService.update(channel);

        return newMessage;
    }

    // 메시지 ID로 조회
    @Override
    public Message findById(UUID id){
        Map<UUID, Message> messageData = loadData();

        Message message = messageData.get(id);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 메시지 ID입니다.");
        }
        return message;
    }

    // 메시지 전부 조회
    @Override
    public List<Message> findAll() {
        Map<UUID, Message> messageData = loadData();
        return new ArrayList<>(messageData.values());
    }

    // 메시지 수정
    @Override
    public Message update(UUID id, String content) {
        Map<UUID, Message> messageData = loadData();

        Message message = messageData.get(id);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 메시지 ID입니다.");
        }

        message.update(content); // 여기서 isEdited가 true로 변함

        saveData(messageData);
        return message;
    }

    // 메시지 삭제
    @Override
    public void delete(UUID id) {
        Map<UUID, Message> messageData = loadData();

        Message message = messageData.get(id);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 메시지 ID입니다.");
        }

        User author = message.getUser();
        Channel channel = message.getChannel();

        author.removeMessage(message);
        channel.removeMessage(message);

        messageData.remove(id);

        saveData(messageData);
        fileUserService.update(author);
        fileChannelService.update(channel);
    }

    // 메시지 고정
    @Override
    public Message togglePin(UUID id){
        Map<UUID, Message> messageData = loadData();

        Message message = messageData.get(id);
        if (message == null) {
            throw new NoSuchElementException("존재하지 않는 메시지 ID입니다.");
        }

        message.togglePin();

        saveData(messageData);
        return message;
    }

    // 권한 확인
    private void validateAccess(UUID userId, UUID channelId) {
        // 채널 멤버 확인
        User user = fileUserService.findById(userId);
        Channel channel = fileChannelService.findById(channelId);
        if (!channel.isMember(user)) {
            throw new IllegalArgumentException("채널 멤버만 접근할 수 있습니다.");
        }
    }

    // 특정 채널의 메시지 목록 조회
    @Override
    public List<Message> findAllByChannelId(UUID channelId, UUID userId) { // 특정 채널의 메시지 조회
        validateAccess(userId, channelId);
        Channel channel = fileChannelService.findById(channelId);
        return channel.getMessages();
    }
}
