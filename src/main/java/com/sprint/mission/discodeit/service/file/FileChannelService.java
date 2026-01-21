package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
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
        channel.getUsers().forEach(u -> {
            u.getChannelList().remove(channel);
            userService.save(u);
        });

        channel.getUsers().clear();

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


        user.getChannelList().add(channel); // 유저의 채널 리스트에 해당 채널 추가

        channel.getUsers().add(user); // 채널의 유저 리스트에 해당 유저 추가

        userService.save(user); // 유저 서비스를 통해 유저 영속화
        save(); // 채널 영속화
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

        user.getChannelList().remove(channel); // 해당 유저의 채널 리스트에서 해당 채널을 삭제
        channel.getUsers().remove(user); // 해당 채널의 유저 리스트에서 해당 유저를 삭제

        userService.save(user); // 유저 서비스를 통해 수정된 유저를 영속화
        save(); // 채널 영속화
    }

    @Override
    public void deleteChannelbyName(String name) {
        Objects.requireNonNull(name, "유효하지 않은 채널 이름입니다.");

        Channel channel = data.stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 채널은 존재하지 않습니다."));

        deleteChannel(channel.getId()); // deleteChannel 메소드 내에서 관계 정리 및 영속화 동작함.
    }

    // 서비스의 data를 path 경로에 영속화하는 메소드
    public void save(){
        SaveLoadUtil.save(data,path);
    }

    @Override
    public void save(Channel channel) {
        // 파일 서비스에서는 구현 불필요?
        // jcf에서 사용.
    }


}
