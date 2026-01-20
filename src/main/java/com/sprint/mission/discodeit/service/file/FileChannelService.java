package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.ChannelService;
import com.sprint.mission.discodeit.service.jcf.UserService;
import com.sprint.mission.discodeit.utils.CheckValidation;
import com.sprint.mission.discodeit.utils.SaveLoadUtil;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    List<Channel> data = new ArrayList<>();
    private static final String path = "channel.dat";

    private UserService userService;

    // 생성자
    public FileChannelService(){
        this.data = load();
        this.userService = null;
    }

    // 유저 서비스를 주입
    @Override
    public void setUserService(UserService userService){
        this.userService = userService;
    }

    // 파일로부터 채널 데이터를 읽어옴.
    private List<Channel> load(){
        return SaveLoadUtil.load(path);
    }

    // data를 최신화함.
    public void reload(){
        this.data = load();
    }

    // data 객체를 파일에 저장
    public void save() {
        SaveLoadUtil.save(data,path);
    }

    @Override
    public Channel createChannel(String name, String content, Channel.CHANNEL_TYPE type) {
        // 매개변수 검증.
        Objects.requireNonNull(name, "name은 null일수 없습니다.");
        Objects.requireNonNull(content, "content는 null일수 없습니다.");
        Objects.requireNonNull(type, "type은 null일수 없습니다.");

        reload(); // data 조회 전 data를 최신화한다.

        // 같은 이름을 가진 채널이 존재하면 해당 채널을 리턴
        if(data.stream().anyMatch(c -> name.equals(c.getName()))) {
            System.out.println("이미 파일에 존재하는 채널입니다!");
            return data.stream().filter(c -> name.equals(c.getName())).findFirst().orElseThrow(() -> new IllegalStateException("유효하지 않은 동작"));
        }

        // 입력받은 매개변수를 사용하여 채널 생성자 호출 (채널 객체 생성)
        Channel channel = new Channel(type, name, content);

        // 채널서비스 data 리스트에 추가.
        data.add(channel);

        // 파일에 변경된 data를 저장한다.
        save();

        return channel;
    }


    @Override
    public Channel readChannel(UUID uuid) {
        reload(); // data에서 읽기 전, data를 최신화한다.
        Objects.requireNonNull(uuid, "유효하지 않은 매개변수입니다.");
        return CheckValidation.readEntity(data,uuid,() -> new IllegalStateException("채널이 존재하지 않습니다."));
    }

    @Override
    public Channel updateChannel(UUID uuid, String name, String content, Channel.CHANNEL_TYPE type) {
        Objects.requireNonNull(uuid, "유효하지 않은 식별자 ID입니다.");
        Channel channel = readChannel(uuid);

        Optional.ofNullable(name)
                .ifPresent(channel::updateName);
        Optional.ofNullable(content)
                .ifPresent(channel::updateContent);
        Optional.ofNullable(type)
                .ifPresent(channel::updateType);

        save();

        return channel;
    }

    @Override
    public void deleteChannel(UUID uuid) {
        Objects.requireNonNull(uuid, "해당 매개변수가 유효하지 않습니다.");
        Channel channel = readChannel(uuid);

        // 삭제하고자 하는 채널의 유저리스트를 초기화.
        channel.getUsers().clear();
        data.remove(channel);

        save();
    }


    @Override
    public Set<Channel> readChannelsbyUser(String userID) {
        User user = userService.readUser(userID);
        return user.getChannelList();
    }

    // 채널과 유저 사이의 관계 성립 (참가)
    public void userJoin(String userID, UUID channelID){
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        Channel channel = readChannel(channelID); // 타겟 채널을 읽어온다.
        User user = userService.readUser(userID); // 주입한 userService를 통해 타겟 유저를 읽어온다.


        if(user.getChannelList()
                .stream()
                .anyMatch(c -> channelID.equals(c.getId()))){
                System.out.println("해당 유저는 이미 해당 채널에 가입되어 있습니다.");
                return;
        }

        user.getChannelList().add(channel); // 해당 유저 객체의 채널리스트에 타겟 채널을 추가한다.
        channel.getUsers().add(user); // 타겟 채널의 유저리스트에 타겟 유저를 추가한다.
        userService.save(); // FileUserService을 통해 유저 파일 저장.
        save(); // 채널 객체 파일에 저장

    }

    // 채널과 유저 사이의 관계 파기 (탈퇴)
    @Override
    public void userLeave(String userID, UUID channelID) {
        Objects.requireNonNull(userID, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        Channel channel = readChannel(channelID);
        User user = userService.readUser(userID);
        channel.getUsers().remove(user);
        user.getChannelList().remove(channel);
        userService.save(); // 변경된 유저 사항 영속화. (파일 저장)
        save(); // 채널 객체 파일에 저장

    }

    @Override
    public void deleteChannelbyName(String name) {
        Objects.requireNonNull(name, "유효하지 않은 매개변수입니다.");

        reload();

        Channel channel = data
                .stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .orElseThrow(()-> new IllegalStateException("존재하지 않는 채널"));

        UUID channelID = channel.getId();

        userService.readAllUsers()
                .forEach(u -> u.getChannelList().removeIf(ch -> ch.getId().equals(channelID)));

        userService.save();
        data.removeIf(ch -> channelID.equals(ch.getId()));
        save();

    }

    // data 리스트 내 존재하는 모든 채널들을 리턴
    @Override
    public ArrayList<Channel> readAllChannels() {
//        System.out.println("-".repeat(20) + " 전체 조회 " + "-".repeat(20));
//        data.forEach(System.out::println);
//        System.out.println("-".repeat(50));
        reload();
        System.out.println(data);
        return (ArrayList<Channel>) data;
    }
}
