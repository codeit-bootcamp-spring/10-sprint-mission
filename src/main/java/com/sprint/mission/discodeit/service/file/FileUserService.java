package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validator;

import java.io.*;
import java.util.*;

public class FileUserService implements UserService {
    private ChannelService channelService;
    private MessageService messageService;
    private  Map<UUID, User> data;


    public FileUserService() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./users.ser"))) {
            data = (Map<UUID, User>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            data = new HashMap<>();
        }
        saveData();
    }

    @Override
    public User createUser(String userName) {
        loadData();
        Validator.validateNotNull(userName, "생성하고자 하는 유저의 이름이 null일 수 없음");
        Validator.validateNotBlank(userName, "생성하고자 하는 유저의 이름이 빈문자열일 수 없음");
        User user = new User(userName.trim());
        data.put(user.getId(), user);
        saveData();
        return user;
    }

    // 파일 데이터를 필드의 Map에 다시 할당
    // 조회시 항상 데이터를 파일에서 가져와야 필드의 Map 데이터와 일치함을 보장할 수 있음
    @Override
    public User findById(UUID id) {
        loadData();
        User user = data.get(id);
        if (user == null) {
            throw new IllegalStateException("해당 id의 사용자를 찾을 수 없음");
        }
        return user;
    }

    @Override
    public List<User> findAll() {
        loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateById(UUID id, String newUserName) {
        loadData();
        User targetUser = findById(id);
        Validator.validateNotNull(newUserName, "변경 하려는 유저의 이름이 null일 수 없음");
        Validator.validateNotBlank(newUserName, "변경 하려는 유저의 이름이 빈문자열일 수 없음");
        targetUser.setUserName(newUserName.trim());
        saveData();
        return targetUser;
    }

    @Override
    public void deleteById(UUID id) {
        loadData();
        User user = findById(id);
        // 유저가 참여중인 channel 리스트
//        List<Channel> channels = user.getChannels().stream().toList();
        List<Channel> channels = channelService.getChannelsByUserId(id);
        // 유저가 작성했던 message 리스트
//        List<Message> messages = user.getMessages().stream().toList();
        List<Message> messages = messageService.getMessagesByUserId(id);
        // 참여중인 채널의 유저리스트에서 유저를 제거
        for (Channel channel : channels) {
            channel.removeJoinedUser(user);
        }
        channelService.saveData();
        saveData();
        // 작성했던 메시지가 포함된 채널의 메시지 리스트에서 메시지를 제거
        for (Message message : messages) {
            message.getChannel().removeMessage(message);
            messageService.deleteById(message.getId());
        }
        messageService.saveData();
        channelService.saveData();
        data.remove(id);
        saveData();
    }

    @Override
    public List<User> getUsersByChannelId(UUID channelId) {
        loadData();
        return data.values()
                .stream()
                .filter(user ->
                        user.getChannels().
                                stream().
                                anyMatch(channel -> channel.getId().equals(channelId)))
                .toList();
    }

    @Override
    public void setChannelService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    @Override
    public void joinChannel(UUID userId, UUID channelId) {
        loadData();
        User user = findById(userId);
        Channel channel = channelService.findById(channelId);
        user.joinChannel(channel);
        channelService.saveData();
        saveData();
    }

    public void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./users.ser"))) {
            data = (Map<UUID, User>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./users.ser"))){
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
