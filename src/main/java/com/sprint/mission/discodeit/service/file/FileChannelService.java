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

    // [저장] (직렬화)
    void saveData() {
        init();

        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(storeFile.toFile()))) {

            oos.writeObject(channelData);

        } catch (IOException e) {

            throw new RuntimeException("Data save failed." + e.getMessage());

        }
    }

    // [저장] (역직렬화)
    private void loadData() {
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

    // [비즈니스] 생성
    @Override
    public Channel create(String name) {
        loadData();
        // 객체 생성
        Channel channel = new Channel(name);
        // [저장]
        channelData.add(channel);
        // [저장] 직렬화 후 데이터 저장
        saveData();
        return channel;
    }
    // [비즈니스] 단일 조회
    @Override
    public Channel find(UUID id) {
        // [저장]
        loadData();
        // [저장]
        return channelData.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));
    }

    // [비즈니스] 전체 조회
    @Override
    public List<Channel> findAll() {
        // [저장]
        loadData();
        // [저장]
        return channelData;
    }

    // [비즈니스] 수정
    @Override
    public Channel updateName(UUID channelID, String name) {
        loadData();
        // [저장]
        Channel channel = find(channelID);
        // [비즈니스]
        channel.updateName(name);

        saveData();
        return channel;
    }
    @Override
    public void update(){
        saveData();
    }

    // Channel 자체 삭제
    @Override
    public void deleteChannel(UUID channelId) {
        if (fileMessageService == null || fileUserService == null) {
            throw new IllegalStateException("Services are not set.");
        }

        // [저장] 여기서 find가 load를 또 하면 안 됨 (find는 메모리에서만 찾게 바꾸는 게 베스트)
        Channel channel = find(channelId);

        // [비즈니스] 유저들에서 채널 제거
        // 참여자만 돌고 싶으면 channel.getMembersList()를 쓰되, 복사본으로
        List<User> users = new ArrayList<>(fileUserService.findAll());
        for (User user : users) {
            user.leaveChannel(channel);
        }
        // [저장] 유저 저장
        fileUserService.update(); // update() 말고 saveData() 같이 명확한 이름 권장

        // [비즈니스 + 저장] 채널에 속한 메시지 삭제
        // deleteMessage가 내부에서 message list를 수정할 수 있으니, 먼저 id만 뽑아놓고 지움
        List<UUID> messageIds = fileMessageService.findAll().stream()
                .filter(m -> m.getChannel().getId().equals(channelId))
                .map(Message::getId)
                .toList();

        // [저장]
        for (UUID messageId : messageIds) {
            fileMessageService.deleteMessage(messageId);
        }
        fileMessageService.update();

        // [저장] 채널 삭제 후 저장
        channelData.remove(channel);
        saveData();
    }

    @Override
    public void joinChannel(UUID userID, UUID channelID) {
        if (fileUserService == null) {
            throw new IllegalStateException("UserService is not set. Call setUserService() before using create().");
        }
        // [저장]
        Channel channel = find(channelID);
        User user = fileUserService.find(userID);

        if (channel.getMembersList().contains(user)) {
            throw new IllegalArgumentException("User is already in this channel." + channelID);
        }

        // [비즈니스]
        channel.addMember(user); // channelService loadData로 반영

        // [비즈니스]
        user.joinChannel(channel); // userService에서 어떻게 반영하지?

        fileUserService.update();

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
        // [저장] 최신화
        Channel channel = find(channelID);
        User user = fileUserService.find(userID);

        if (!channel.getMembersList().contains(user)) {
            throw new IllegalArgumentException("User is not in this channel." + channelID);
        }

        // [비즈니스] user에서 channel 삭제
        user.leaveChannel(channel); // 여기서도 user 직렬화 변경사항 저장해야하나

        // [비즈니스] channel에서 user 삭제 -> channelService 자체에서 saveData()
        channel.removeMember(user);

        // [비즈니스] user가 보낸 messageList 중 해당 channel에 관한 것 삭제해줘야 함
        List<Message> messageList = new ArrayList<>(user.getMessageList());

        messageList.stream()
                .filter(msg -> msg.getChannel().equals(channel))
                .forEach(msg -> fileMessageService.deleteMessage(msg.getId())); // messageService 내부에서 saveData 동작


        fileUserService.update();
        fileMessageService.update();
        saveData();
    }

    // Channel 안 모든 User 조회
    @Override
    public List<String> findMembers(UUID channelID) {
        // [저장]
        Channel channel = find(channelID);
        return channel.getMembersList().stream()
                .map(User::getName)
                .collect(Collectors.toList());
    }
}
