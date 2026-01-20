package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.Validator;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private Map<UUID, Channel> data = new HashMap<UUID, Channel>();
    private MessageService messageService;

    public FileChannelService() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./channels.ser"))) {
            data = (Map<UUID, Channel>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
            data = new HashMap<>();
        }
        saveData();
    }

    @Override
    public Channel createChannel(String channelName) {
        loadData();
        Validator.validateNotNull(channelName, "생성하고자하는 채널의 채널명이 null일 수 없음");
        Validator.validateNotBlank(channelName, "생성하고자하는 채널의 채널명이 빈문자열일 수 없음");
        Channel channel = new Channel(channelName.trim());
        data.put(channel.getId(), channel);
        saveData();
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        loadData();
        Channel channel = data.get(id);
        if (channel == null) {
            throw new IllegalStateException("해당 id의 채널을 찾을 수 없음");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        loadData();
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel updateById(UUID id, String newChannelName) {
        loadData();
        Validator.validateNotNull(newChannelName, "업데이트하고자 하는 채널의 채널명이 null일 수 없음");
        Validator.validateNotBlank(newChannelName, "업데이트하고자 하는 채널의 채널명이 빈 문자열일 수 없음");
        Channel targetChannel = findById(id);
        targetChannel.setChannelName(newChannelName.trim());
        saveData();
        return targetChannel;
    }

    @Override
    public void deleteById(UUID id) {
        loadData();
        Channel channel = findById(id);
        // 채널에 참여 중인 유저 리스트
        List<User> users = channel.getJoinedUsers().stream().toList();
        // 채널에 남겨진 메시지 리스트
        List<Message> messages = channel.getMessageList().stream().toList();
        // 유저 객체의 채널 리스트에서 채널 삭제
        for (User user : users) {
            user.leaveChannel(channel);
        }
        // 메시지 삭제
        for (Message message : messages) {
            messageService.deleteById(message.getId());
            message.getUser().removeMessage(message, channel);
        }
        data.remove(id);
        saveData();
    }

    @Override
    public List<Channel> getChannelsByUserId(UUID userId) {
        loadData();
        return data.values().stream()
                .filter(channel ->
                        channel.getJoinedUsers().stream()
                                .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    public void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("./channels.ser"))) {
            data = (Map<UUID, Channel>) ois.readObject();
        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("./channels.ser"))){
            oos.writeObject(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
