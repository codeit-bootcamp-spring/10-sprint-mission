package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private static final String FILE_PATH = "data/channels.ser";
    final List<Channel> data;
    private final UserService userService;

    public FileChannelService(UserService userService) {
        this.data = loadChannels();
        this.userService = userService;
    }

    private void saveChannels(){
        File file = new File(FILE_PATH);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.data);
            System.out.println("직렬화 완료: channels.ser에 저장되었습니다.");
        } catch (Exception e) {
            throw new RuntimeException("채널 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Channel> loadChannels() {
        File file = new File(FILE_PATH);
        if(!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object data = ois.readObject();
            System.out.println("역직렬화 완료: " + data);
            return (List<Channel>) data;
        } catch (IOException |ClassNotFoundException e) {
            throw new RuntimeException("메시지 데이터 로드 중 오류 발생", e);
        }
    }

    @Override
    public Channel createChannel(ChannelType type, String channelName, String channelDescription) {
        Validators.validationChannel(type, channelName, channelDescription);
        Channel channel = new Channel(type, channelName, channelDescription);
        this.data.add(channel);
        saveChannels();
        return channel;
    }

    @Override
    public Channel readChannel(UUID id) {
        return validateExistenceChannel(id);
    }

    @Override
    public List<Channel> readAllChannel() {
        return this.data;
    }

    @Override
    public Channel updateChannel(UUID id, ChannelType type, String channelName, String channelDescription) {
        Channel channel = validateExistenceChannel(id);

        if (type == null && channelName == null && channelDescription == null) {
            saveChannels();
            return channel;
        }

        Optional.ofNullable(type).ifPresent(t -> {Validators.requireNonNull(t, "type");
            channel.updateChannelType(t);
        });
        Optional.ofNullable(channelName)
                .ifPresent(name -> {Validators.requireNotBlank(name, "channelName");
                    channel.updateChannelName(name);
                });
        Optional.ofNullable(channelDescription).ifPresent(des -> {
            Validators.requireNotBlank(des, "channelDescription");
            channel.updateChannelDescription(des);
        });

        saveChannels();
        return channel;
    }



    @Override
    public void deleteChannel(UUID id) {
        Channel channel = readChannel(id);
        this.data.remove(channel);
        saveChannels();
    }

    @Override
    public void joinChannel(UUID channelId, User user) {
        Channel channel = readChannel(channelId);
        User persistedUser = userService.readUser(user.getId());

        boolean alreadyJoined = channel.getJoinedUsers().stream()
                .anyMatch(u -> persistedUser.getId().equals(u.getId()));

        if (alreadyJoined) {
            throw new IllegalArgumentException("이미 참가한 유저입니다.");
        }

        channel.getJoinedUsers().add(persistedUser);
        persistedUser.getJoinedChannels().add(channel);
        saveChannels();
        userService.updateUser(persistedUser.getId(), null, null);
    }

    @Override
    public void leaveChannel(UUID channelId, User user) {
        Channel channel = readChannel(channelId);
        User persistedUser = userService.readUser(user.getId());

        boolean alreadyLeaved = channel.getJoinedUsers().stream()
                .noneMatch(u -> persistedUser.getId().equals(u.getId()));

        if (alreadyLeaved) {
            throw new IllegalArgumentException("채널에 속해 있지 않은 유저입니다.");
        }

        channel.getJoinedUsers().removeIf(u -> persistedUser.getId().equals(u.getId()));
        persistedUser.getJoinedChannels().removeIf(c -> channelId.equals(c.getId()));
        saveChannels();
        userService.updateUser(persistedUser.getId(), null, null);
    }

    @Override
    public List<Channel> readChannelsByUser(UUID userId) {
        return this.data.stream()
                .filter(ch -> ch.getJoinedUsers().stream()
                        .anyMatch(u -> userId.equals(u.getId())))
                .toList();
    }


    private Channel validateExistenceChannel(UUID id) {
        Validators.requireNonNull(id, "id는 null이 될 수 없습니다.");
        return this.data.stream()
                .filter(channel -> id.equals(channel.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("채널 id가 존재하지 않습니다."));
    }
}