package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.*;

import java.util.*;
import com.sprint.mission.discodeit.utils.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    //인터페이스 객체 생성시 새로운 해쉬맵 할당
    public JCFChannelService() {
        this.data = new HashMap<>();
    }


    @Override
    public Channel createChannel(String channelName) {

        // null 허용 안함.
        Validation.notBlank(channelName, "채널 이름");
        // 중복 이름 검사
        Validation.noDuplicate(
                data.values(),
                ch->ch.getChannelName().equals(channelName),
                "이미 존재하는 채널명입니다." + channelName
        );
//        if (data.values().stream()
//                .anyMatch(ch -> ch.getChannelName().equals(channelName))) {
//            throw new IllegalStateException("이미 존재하는 채널명입니다." + channelName);
//        }
        Channel channel = new Channel(channelName);
        data.put(channel.getId(), channel);
        return channel;
    }


    @Override
    public List<Channel> getChannelAll() {
        return new ArrayList<>(data.values());
    }

    // 채널 업데이트(관리자용)
    @Override
    public Channel updateChannel(UUID uuid, String newName) {
        Channel existing = findChannelOrThrow(uuid);
        // 만약 변경하려는 이름이 이미 존재한다면,
        Validation.noDuplicate(
                data.values(),
                ch->ch.getChannelName().equals(newName),
                "이미 존재하는 채널명입니다." + newName
        );
//        if (data.values().stream()
//                .anyMatch(ch -> ch.getChannelName().equals(newName))) {
//            throw new IllegalArgumentException("이미 존재하는 채널명니다." + newName);
//        }
        existing.update(newName);
        return existing;
    }

    @Override
    public void deleteChannel(UUID uuid) {
        findChannelOrThrow(uuid);
        data.remove(uuid);
    }


    // 중복일 경우, 없는경우 예외를 던져야한다.
    @Override
    public Channel getChannelByName(String channelName) {
        return data.values().stream()
                .filter(ch -> ch.getChannelName().equals(channelName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("채널이 존재하지 않습니다: " + channelName));

    }

    @Override
    public List<Message> getMessageInChannel(UUID uuid){
        Channel channel = data.get(uuid);
        if(channel == null) {
            return Collections.emptyList();
        }
        return channel.getMessages();
    }


    //id로 조회 후 없으면 예외
    public Channel findChannelOrThrow(UUID id) {
        Channel channel = data.get(id);
        if (channel == null) {
            throw new NoSuchElementException("해당 채널이 존재하지 않습니다: " + id);
        }
        return channel;
    }
}


