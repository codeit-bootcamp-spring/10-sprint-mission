package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;


import java.util.*;

public class JCFChannelService implements ChannelService {
    List<Channel> data = new ArrayList<>();

    @Override
    public Channel createChannel(String name, String content, Channel.CHANNEL_TYPE type) {
        // Objects.requireNonNull은 해당 매개변수가 null일 경우 바로 예외를 던진다고 합니다.
        Objects.requireNonNull(name, "name은 null일수 없습니다.");
        Objects.requireNonNull(content, "content는 null일수 없습니다.");
        Objects.requireNonNull(type, "type은 null일수 없습니다.");

        if(data.stream().anyMatch(c -> name.equals(c.getName()))){
            throw new IllegalStateException("이미 존재하는 채널 이름입니다.");
        }


        Channel channel = new Channel(type, name, content);
        data.add(channel);
        return channel;
    }

    @Override
    public void deleteChannel(UUID uuid) {
        Objects.requireNonNull(uuid, "해당 매개변수가 유효하지 않습니다.");

        Channel channel = CheckValidation.readEntity(data,uuid,()->new IllegalStateException("채널이 존재하지 않습니다."));

        data.remove(channel);

    }

    @Override
    public Channel readChannel(UUID id){
        Objects.requireNonNull(id, "유효하지 않은 매개변수입니다.");

        return CheckValidation.readEntity(data,id,() -> new IllegalStateException("채널이 존재하지 않습니다."));
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

        return channel;
    }

    public void userAdd(User user, UUID channelID){
        Objects.requireNonNull(user, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        Channel channel = readChannel(channelID);
        channel.getUsers().add(user);

    }

    @Override
    public void userKick(User user, UUID channelID) {
        Objects.requireNonNull(user, "유효하지 않은 유저입니다.");
        Objects.requireNonNull(channelID, "유효하지 않은 채널입니다.");

        Channel channel = readChannel(channelID);

        channel.getUsers().remove(user);
        user.getChannelList().remove(channel);

    }


    @Override
    public ArrayList<Channel> readAllChannels() {
//        System.out.println("-".repeat(20) + " 전체 조회 " + "-".repeat(20));
//        data.forEach(System.out::println);
//        System.out.println("-".repeat(50));
        return (ArrayList<Channel>) data;
    }
}
