package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private final File file = new File("channels.dat");
    private Map<UUID, Channel> channelMap;
    private final UserService userService;

    public FileChannelService(UserService userService){
        this.userService = userService;
        if (file.exists()) {
            load();
        } else {
            this.channelMap = new HashMap<>();
        }
    }

    // 파일 읽기
    @SuppressWarnings("unchecked")
    private void load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.channelMap = (Map<UUID, Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            this.channelMap = new HashMap<>();
        }
    }

    // 파일 쓰기
    private void saveFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.channelMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Channel createChannel(String channelName) {
        Channel channel = new Channel(channelName);
        channelMap.put(channel.getId(), channel);
        saveFile();
        System.out.println(channel.getChannelName() + "채널 생성이 완료되었습니다.");
        return channel;
    }

    @Override
    public Channel findChannelByChannelId(UUID id){
        Channel channel = channelMap.get(id);
        if (channel == null) {
            throw new IllegalArgumentException("해당 채널이 없습니다.");
        }
        return channel;
    }

    @Override
    public List<Channel> findChannelByUserId(UUID userID){
        User user = userService.findUserById(userID);
        return new ArrayList<>(user.getMyChannels());
    }

    @Override
    public List<Channel> findAllChannels(){
        return new ArrayList<>(channelMap.values());
    }

    @Override
    public void deleteChannel(UUID id){
        Channel targetChannel = findChannelByChannelId(id);

        targetChannel.getParticipants()
                     .forEach(user -> user.getMyChannels().remove(targetChannel));
        channelMap.remove(id);
        saveFile();
    }

    public Channel updateChannel(UUID id, String channelName){
        Channel targetChannel = findChannelByChannelId(id);
        targetChannel.updateChannelInfo(channelName);
        saveFile();
        return targetChannel;
    }

    @Override
    public void joinChannel(UUID userID, UUID channelID) {
        Channel targetChannel = findChannelByChannelId(channelID);
        User targetUser = userService.findUserById(userID);

        targetChannel.getParticipants().stream()
                     .filter(participant -> participant.getId().equals(targetUser.getId()))
                     .findAny()
                     .ifPresent(participant -> {
                         throw new IllegalArgumentException("이미 채널에 참여중인 사용자입니다.");
                     });

        targetChannel.getParticipants().add(targetUser);
        targetUser.getMyChannels().add(targetChannel);
        System.out.println(targetUser.getUsername() + "님이 "
                + targetChannel.getChannelName() + " 채널에 입장했습니다.");

        saveFile();
    }

    @Override
    public void leaveChannel(UUID userID, UUID channelID) {
        Channel targetChannel = findChannelByChannelId(channelID);
        User targetUser = userService.findUserById(userID);

        if (!targetChannel.getParticipants().contains(targetUser)) {
            throw new IllegalArgumentException("채널에 참여중이지 않습니다.");
        }

        targetChannel.getParticipants().remove(targetUser);
        targetUser.getMyChannels().remove(targetChannel);

        saveFile();
    }
}