package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class FileMessageService implements MessageService {
    // 필드
    private final Path basePath = Path.of("data/message");
    private final Path storeFile = basePath.resolve("message.ser");

    private Map<UUID, Message> messageData;

    private ChannelService FileChannelService;
    private UserService FileUserService;

    // 생성자
    public FileMessageService() {
        init();
        loadData();
    }

    // 디렉 체크
    private void init() {
        try {
            if (!Files.exists(basePath)) {
                Files.createDirectories(basePath);
            }
        } catch (IOException e) {
            System.out.println("Directory creation failed." + e.getMessage());
        }
    }

    // 저장 (직렬화)
    void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeFile.toFile()))) {
            oos.writeObject(messageData);
        } catch (IOException e) {
            throw new RuntimeException("Data save failed." + e.getMessage());
        }
    }

    // 로드 (역직렬화)
    private void loadData() {
        // 파일이 없으면: 첫 실행이므로 빈 리스트 유지
        if (!Files.exists(storeFile)) {
            messageData = new HashMap<>();
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeFile.toFile()))){
            messageData = (Map<UUID, Message>) ois.readObject();
        } catch (Exception e){
            throw new RuntimeException("Data load failed." + e.getMessage());
        }
    }

    // Setter
    public void setChannelService(ChannelService FileChannelService) {
        this.FileChannelService = FileChannelService;
    }

    public void setUserService(UserService FileUserService) {
        this.FileUserService = FileUserService;
    }

    //생성
    @Override
    public Message create(String contents, UUID userID, UUID channelID) {
        loadData();
        // Service 예외
        if (FileUserService == null) {
            throw new IllegalStateException("UserService is not set. Call setUserService() before using create().");
        }
        if (FileChannelService == null) {
            throw new IllegalStateException("ChannelService is not set. Call setChannelService() before using create().");
        }

        // sender, channel 존재하는 지 check , 각 서비스 내부에서 최신으로 loadData()
        User sender = FileUserService.find(userID);
        Channel channel = FileChannelService.find(channelID);

        // create
        Message msg = new Message(contents, sender, channel);
        messageData.put(msg.getId(), msg);

        // sender, channel에 message 할당 -> userService, channelService 변경사항 저장해줘야 함 ...
        sender.addMessage(msg);
        channel.addMessage(msg);


        FileUserService.update();
        FileChannelService.update();
        saveData();
        return msg;
    }

    // 조회
    @Override
    public Message find(UUID messageID) {
        loadData();
        return messageData.values().stream()
                .filter(m -> m.getId().equals(messageID)) // equals 비교
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Message Not Found: " + messageID));
    }
    // 전체 조회
    @Override
    public List<Message> findAll() {
        loadData();
        return messageData.values().stream().toList();
    }

    // 수정
    @Override
    public Message updateName(UUID messageID, String contents) {
        if (messageID == null) {
            throw new IllegalArgumentException("id must not be null");
        }
        Message msg = find(messageID);
        msg.updateContents(contents);
        saveData();
        return msg;
    }

    @Override
    public void update() {
        saveData();
    }

    // 삭제
    @Override
    public void deleteMessage(UUID messageID) {
        Message msg = find(messageID);

        UUID senderID = msg.getSender().getId();
        UUID channelID = msg.getChannel().getId();

        User sender = FileUserService.find(senderID);
        Channel channel = FileChannelService.find(channelID);

        // sender, channel의 messageList에서 msg 삭제 , 여기도 userService, channelService의 saveData...
        sender.removeMessage(msg);
        channel.removeMessage(msg);

        FileUserService.update();

        FileChannelService.update();

        // message 완전 삭제
        messageData.remove(messageID);
        saveData();
    }

    // channel 전체 메시지 조회
    public List<String> findMessagesByChannel (UUID channelID){
        loadData();
        // channelService 예외처리
        if (FileChannelService == null) {
            throw new IllegalStateException("ChannelService is not set. Call setChannelService() before using create().");
        }

        Channel channel = FileChannelService.find(channelID);
        return channel.getMessageList().stream()
                .map(Message::getContents)
                .collect(Collectors.toList());
    }

    // User 전체 메시지 조회
    public List<String> findMessagesByUser (UUID userID){
        loadData();
        // userService 예외처리
        if (FileUserService == null) {
            throw new IllegalStateException("UserService is not set. Call setUserService() before using create().");
        }

        // service 내부에서 최신으로 loadData()
        User user = FileUserService.find(userID);
        return user.getMessageList().stream()
                .map(Message::getContents)
                .collect(Collectors.toList());
    }
}
