package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.jcf.ChannelService;
import com.sprint.mission.discodeit.service.jcf.UserService;
import com.sprint.mission.discodeit.utils.CheckValidation;
import com.sprint.mission.discodeit.utils.SaveLoadUtil;

import java.util.*;

public class FileChannelService implements ChannelService {
    private List<Channel> data;
    private static final String path = "channel.dat";

    private UserService userService;

    public FileChannelService(){
        this.data = SaveLoadUtil.load(path);
    }

    @Override
    public Channel createChannel(String name, String content, Channel.CHANNEL_TYPE type) {
        Objects.requireNonNull(name, "name은 null일수 없습니다.");
        Objects.requireNonNull(content, "content는 null일수 없습니다.");
        Objects.requireNonNull(type, "type은 null일수 없습니다.");

        // 같은 이름을 가진 채널이 존재하면 예외를 던짐 (중복 이름 불가)
        if(data.stream().anyMatch(c -> name.equals(c.getName()))){
            throw new IllegalStateException("이미 존재하는 채널 이름입니다.");
        }

        // 입력받은 매개변수를 사용하여 채널 생성자 호출 (채널 객체 생성)
        Channel channel = new Channel(type, name, content);

        // 채널서비스 data 리스트에 추가.
        data.add(channel);

        // 파일에 영속화.
        save();

        return channel;
    }

    @Override
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Channel readChannel(UUID uuid) {
        Objects.requireNonNull(uuid, "유효하지 않은 채널 ID입니다.");

        return CheckValidation.readEntity(
                data,
                uuid,
                () -> new IllegalStateException("해당 채널이 존재하지 않습니다.")
        );
    }

    @Override
    public Channel updateChannel(UUID uuid, String name, String desc, Channel.CHANNEL_TYPE type) {
        Objects.requireNonNull(uuid, "유효하지 않은 채널 ID입니다.");

        Channel channel = readChannel(uuid);

        if (name != null) {
            boolean duplicated = data.stream()
                    .anyMatch(c -> name.equals(c.getName()) && !uuid.equals(c.getId()));
            if (duplicated) {
                throw new IllegalStateException("이미 존재하는 채널 이름입니다.");
            }
            channel.updateName(name);
        }

        if (desc != null) channel.updateContent(desc);
        if (type != null) channel.updateType(type);

        save();
        return channel;
    }

    @Override
    public void deleteChannel(UUID uuid) {
        Objects.requireNonNull(uuid, "유효하지 않은 채널 ID입니다.");

        Channel channel = readChannel(uuid);

        // 유저-채널 관계 끊기 (양방향이면 둘 다 정리)
        // userService가 세팅되지 않았으면 최소한 채널만 삭제
        if (userService != null) {
            for (User u : new HashSet<>(channel.getUsers())) {
                u.getChannelList().remove(channel);
            }
            channel.getUsers().clear();
            userService.save(); // 유저 파일에도 반영
        }

        data.remove(channel);
        save();
    }

    @Override
    public List<Channel> readAllChannels() {
        return List.copyOf(data);
    }

    @Override
    public Set<Channel> readChannelsbyUser(String userID) {
        return Set.of();
    }

    @Override
    public void userJoin(String userID, UUID channelID) {
        Objects.requireNonNull(userID, "유효하지 않은 유저 ID입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널 ID입니다.");
        if (userService == null) {
            throw new IllegalStateException("UserService가 주입되지 않았습니다.");
        }

        Channel channel = readChannel(channelID);
        User user = userService.readUser(userID);

        // 중복 참가 방지
        if (user.getChannelList().contains(channel)) return;

        user.getChannelList().add(channel);
        channel.getUsers().add(user);

        userService.save();
        save();
    }

    @Override
    public void userLeave(String userID, UUID channelID) {
        Objects.requireNonNull(userID, "유효하지 않은 유저 ID입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널 ID입니다.");
        if (userService == null) {
            throw new IllegalStateException("UserService가 주입되지 않았습니다.");
        }

        Channel channel = readChannel(channelID);
        User user = userService.readUser(userID);

        user.getChannelList().remove(channel);
        channel.getUsers().remove(user);

        userService.save();
        save();
    }

    @Override
    public void deleteChannelbyName(String name) {
        Objects.requireNonNull(name, "유효하지 않은 채널 이름입니다.");

        Channel channel = data.stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 채널은 존재하지 않습니다."));

        deleteChannel(channel.getId());
    }

    @Override
    public void save() {
        SaveLoadUtil.save(data,path);
    }
}
