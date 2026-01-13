package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
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
    public Channel deleteChannel(String name) {
        Objects.requireNonNull(name, "해당 매개변수가 유효하지 않습니다.");

        Channel channel = data.stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(name + " 채널은 존재하지 않습니다."));

        data.remove(channel);
        return null;

    }

    @Override
    public Channel readChannel(UUID id){
        Objects.requireNonNull(id, "유효하지 않은 매개변수입니다.");
        Channel channel =  data.stream()
                .filter(c -> id.equals(c.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 채널입니다."));
        System.out.println(channel);
        return channel;
    }

    @Override
    public Channel updateChannel(UUID uuid, String name, String content, Channel.CHANNEL_TYPE type) {
        Objects.requireNonNull(uuid, "유효하지 않은 식별자 ID입니다.");

        Channel channel = data.stream()
                .filter(c -> uuid.equals(c.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("해당 식별자를 가진 채널이 존재하지 않습니다."));

        Optional.ofNullable(name)
                .ifPresent(channel::updateName);
        Optional.ofNullable(content)
                .ifPresent(channel::updateContent);
        Optional.ofNullable(type)
                .ifPresent(channel::updateType);

        return channel;
    }




    @Override
    public ArrayList<Channel> readAllChannels() {
//        System.out.println("-".repeat(20) + " 전체 조회 " + "-".repeat(20));
//        data.forEach(System.out::println);
//        System.out.println("-".repeat(50));
        return (ArrayList<Channel>) data;
    }
}
