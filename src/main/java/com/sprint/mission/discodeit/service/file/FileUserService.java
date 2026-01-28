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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FileUserService implements UserService {
    // field
    // 필드
    private final Path basePath = Path.of("data/user");
    private final Path storeFile = basePath.resolve("user.ser");

    private List<User> userData;
    private MessageService messageService;
    private ChannelService channelService;

    // constructor
    public FileUserService() {
        init();
        loadData();
    }

    // 디렉토리 체크
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

            oos.writeObject(userData);

        } catch (IOException e) {

            throw new RuntimeException("Data save failed." + e.getMessage());

        }
    }

    // 로드 (역직렬화)
    void loadData() {
        // 파일이 없으면: 첫 실행이므로 빈 리스트 유지
        if (!Files.exists(storeFile)) {
            userData = new ArrayList<>();
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeFile.toFile()))){
            userData = (List<User>) ois.readObject();
        } catch (Exception e){
            throw new RuntimeException("Data load failed." + e.getMessage());
        }
    }

    // Setter
    @Override
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void setChannelService(ChannelService channelService) {this.channelService = channelService;}

    // User 등록
    @Override
    public User create(String name) {
        loadData();
        User user = new User(name);
        this.userData.add(user);
        saveData();
        return user;
    }

    // 단건 조회
    @Override
    public User find(UUID userID){
        loadData();
        return userData.stream()
                .filter(user -> user.getId().equals(userID))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userID));
    }

    // 다건 조회
    @Override
    public List<User> findAll(){
        loadData();
        return userData;
    }

    // User 수정
    @Override
    public User updateName(UUID id, String name){
        User user = find(id);
        user.updateName(name);
        saveData();
        return user;
    }

    @Override
    public void update(){
        saveData();
}

    // User 삭제
    @Override
    public void deleteUser(UUID userID){
        if (messageService == null) {
            throw new IllegalStateException("MessageService is not set in JCFUserService");
        }

        User user = find(userID);

        // User가 보낸 Message 삭제 , messageService 내부에서 loadData() 및 saveData()
        List<Message> messageList = new ArrayList<>(user.getMessageList());
        messageList.forEach(message -> messageService.deleteMessage(message.getId()));

        // Channel에서 User 탈퇴 및 User가 가입한 channel에서 User 탈퇴 , channelService 내부에서 loadData() 및 saveData()
        List<Channel> channels = new ArrayList<>(user.getChannels());
        channels.forEach(channel -> {
            channelService.leaveChannel(user.getId(), channel.getId()); // channelService 내부에서 변경사항 저장
            user.leaveChannel(channel);
        });

        // userData에서 user 완전 삭제
        userData.remove(user);
        saveData();
    }

    // User가 가입한 전체 Channel 조회
    @Override
    public List<String> findJoinedChannels(UUID userID){
        User user = find(userID);
        return user.getChannels().stream()
                .map(Channel::getName)
                .collect(Collectors.toList());
    }

}
