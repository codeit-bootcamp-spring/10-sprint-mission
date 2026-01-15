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

public class FileChannelService implements ChannelService {
    // 필드
    private final Path basePath = Path.of("data/channel");
    private final Path storeFile = basePath.resolve("channel.ser");

    private List<Channel> channelData;

    private MessageService fileMessageService;
    private UserService fileUserService;

    // 생성자
    public FileChannelService() {
        this.channelData = new ArrayList<>();
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
        init();

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeFile.toFile()))) {

            oos.writeObject(channelData);

        } catch (IOException e) {

            throw new RuntimeException("Data save failed." + e.getMessage());

        }
    }

    // 로드 (역직렬화)
    private void loadData() {
        init();
        // 파일이 없으면: 첫 실행이므로 빈 리스트 유지
        if (!Files.exists(storeFile)) {
            channelData = new ArrayList<>();
            return;
        }

        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeFile.toFile()))){
            channelData = (List<Channel>) ois.readObject();
        } catch (Exception e){
            throw new RuntimeException("Data load failed." + e.getMessage());
        }
    }

    // Setter
    @Override
    public void setMessageService(MessageService fileMessageService) {
        this.fileMessageService = fileMessageService;
    }

    @Override
    public void setUserService(UserService fileUserService) {
        this.fileUserService = fileUserService;
    }

    // 생성
    @Override
    public Channel create(String name) {
        // 객체 생성
        Channel channel = new Channel(name);
        channelData.add(channel);
        // 직렬화 후 데이터 저장
        saveData();
        return channel;
    }
    // 단일 조회
    @Override
    public Channel find(UUID id) {

        return channelData.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));
    }

    // 전체 조회
    @Override
    public List<Channel> findAll() {

        return channelData;
    }

    // 수정
    @Override
    public Channel updateName(UUID channelID, String name) {

        Channel channel = find(channelID);
        channel.updateName(name);

        saveData();
        return channel;
    }

    // Channel 자체 삭제
    @Override
    public void deleteChannel(UUID channelID) {
        if (fileMessageService == null) {
            throw new IllegalStateException("MessageService is not set. Call setMessageService() before using create().");
        }

        Channel channel = find(channelID);

        // 여기서도 역직렬화 해줘야 하나?? 흠 ...
        List<User> members = new ArrayList<>(channel.getMembersList()); // Load Data를 해줘야 겠는데

        members.forEach(user -> user.leaveChannel(channel));
        // interface type이라 saveData 사용 수 없음 !!

        // user 저장
        if (fileUserService instanceof FileUserService fus) {
            fus.saveData();
        }

        List<Message> messages = new ArrayList<>(channel.getMessageList()); // 여기도 마찬가지
        messages.forEach(message -> fileMessageService.deleteMessage(message.getId()));
        // 여기도 마찬가지 deleteMessage 메서드 안에서 해결함.


        channelData.remove(channel);
        saveData();

    }

    @Override
    public void joinChannel(UUID userID, UUID channelID) {
        if (fileUserService == null) {
            throw new IllegalStateException("UserService is not set. Call setUserService() before using create().");
        }

        Channel channel = find(channelID);
        User user = fileUserService.find(userID);

        if (channel.getMembersList().contains(user)) {
            throw new IllegalArgumentException("User is already in this channel." + channelID);
        }

        channel.addMember(user); // channelService loadData로 반영

        user.joinChannel(channel); // userService에서 어떻게 반영하지?

        if (fileUserService instanceof FileUserService fus) {
            fus.saveData();
        }

        saveData();
    }

    @Override
    public void leaveChannel(UUID userID, UUID channelID) {
        if (fileUserService == null) {
            throw new IllegalStateException("UserService is not set. Call setUserService() before using create().");
        }
        if (fileMessageService == null) {
            throw new IllegalStateException("MessageService is not set. Call setMessageService() before using create().");
        }

        Channel channel = find(channelID);
        User user = fileUserService.find(userID);

        if (!channel.getMembersList().contains(user)) {
            throw new IllegalArgumentException("User is not in this channel." + channelID);
        }

        // 어떻게 해야지?
        // user에서 channel 삭제
        user.leaveChannel(channel); // 여기서도 user 직렬화 변경사항 저장해야하나

        if (fileUserService instanceof FileUserService fus) {
            fus.saveData();
        }

        // channel에서 user 삭제 -> channelService 자체에서 saveData()
        channel.removeMember(user);

        // user가 보낸 messageList 중 해당 channel에 관한 것 삭제해줘야 함
        List<Message> messageList = new ArrayList<>(user.getMessageList());

        messageList.stream()
                .filter(msg -> msg.getChannel().equals(channel))
                .forEach(msg -> fileMessageService.deleteMessage(msg.getId())); // messageService 내부에서 saveData 동작

        if (fileMessageService instanceof FileMessageService fms) {
            fms.saveData();
        }

        saveData();
    }

    // Channel 안 모든 User 조회
    @Override
    public List<String> findMembers(UUID channelID) {
        Channel channel = find(channelID);
        return channel.getMembersList().stream()
                .map(User::getName)
                .collect(Collectors.toList());
    }
}
