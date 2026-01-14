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

public class FileChannelService implements ChannelService, Serializable {
    // 필드
    private static final long serialVersionUID = 1L;
    private final Path basePath = Path.of("src/main/resources/channel");
    private final Path storeFile = basePath.resolve("channel.ser");

    private List<Channel> channelData;

    private transient MessageService fileMessageService;
    private transient UserService fileUserService;

    // 생성자
    public FileChannelService() {
        this.channelData = new ArrayList<>();
        // 시작 시 데이터 로드
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
    private void saveData() {
        // init 추가해보자
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeFile.toFile()))) {
            oos.writeObject(channelData);
        } catch (IOException e) {
            System.out.println("Data save failed." + e.getMessage());
        }
    }

    // 로드 (역직렬화)
    private void loadData() {
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(storeFile.toFile()))){
            channelData = (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e){
            System.out.println("Data load failed." + e.getMessage());
            channelData = new ArrayList<>();
        }
    }

    // Setter
    @Override
    public void setMessageService(MessageService fileMessageService) {
        this.fileMessageService = fileMessageService;

        if (fileMessageService instanceof FileMessageService){
            this.fileMessageService = (FileMessageService) fileMessageService;
        }
    }

    @Override
    public void setUserService(UserService fileUserService) {
        this.fileUserService = fileUserService;

        if (fileUserService instanceof FileUserService) this.fileUserService = (FileUserService) fileUserService;
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
        // 이름만 바꾼건데 저장해줘야하나?
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
        fileUserService.saveData();

        List<Message> messages = new ArrayList<>(channel.getMessageList()); // 여기도 마찬가지
        messages.forEach(message -> fileMessageService.deleteMessage(message.getId()));
        fileMessageService.saveData();


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

        channel.addMember(user);

        user.joinChannel(channel);

        // 여기서는 직렬화 어떻게 해야하지?
        saveData(); // ??
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

        // user에서 channel 삭제
        user.leaveChannel(channel); // 여기서도 user 직렬화 변경사항 저장해야하나

        // channel에서 user 삭제
        channel.removeMember(user); // 여기도 마찬가지

        // user가 보낸 messageList 중 해당 channel에 관한 것 삭제해줘야 함
        List<Message> messageList = new ArrayList<>(user.getMessageList());

        messageList.stream()
                .filter(msg -> msg.getChannel().equals(channel))
                .forEach(msg -> fileMessageService.deleteMessage(msg.getId()));

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
