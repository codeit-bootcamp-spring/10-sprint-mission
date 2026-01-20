package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.*;

import java.util.*;
import com.sprint.mission.discodeit.utils.*;

public class JCFChannelService implements ChannelService {
    private final ChannelRepository channelRepo;
    // private final Map<UUID, Channel> data;


    public JCFChannelService(ChannelRepository channelRepo) {
        this.channelRepo = channelRepo;
        //this.data = new HashMap<>();
    }

    //생성
    @Override
    public Channel createChannel(String channelName) {

        // null 허용 안함.
        Validation.notBlank(channelName, "채널 이름");
        // 중복 이름 검사
        Validation.noDuplicate(
                channelRepo.findAll(),
                ch->ch.getChannelName().equals(channelName),
                "이미 존재하는 채널명입니다." + channelName
        );
//        if (data.values().stream()
//                .anyMatch(ch -> ch.getChannelName().equals(channelName))) {
//            throw new IllegalStateException("이미 존재하는 채널명입니다." + channelName);
//        }
        Channel channel = new Channel(channelName);
        channelRepo.save(channel);
        //data.put(channel.getId(), channel);
        return channel;
    }


    //조회
    // 채널 목록 가져오기
    @Override
    public List<Channel> getChannelAll() {
        return channelRepo.findAll();
        //return new ArrayList<>(data.values());
    }
    //id로 채널 조회 후 없으면 예외
    @Override
    public Channel findChannelById(UUID id) {
        Channel channel = channelRepo.findById(id); //data.get(id);
        if (channel == null) {
            throw new NoSuchElementException("해당 채널이 존재하지 않습니다: " + id);
        }
        return channel;
    }
    // 채널명으로 조회
    @Override
    public Channel getChannelByName(String channelName) {
        return channelRepo.findAll().stream()
                .filter(ch -> ch.getChannelName().equals(channelName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("채널이 존재하지 않습니다: " + channelName));

    }


    // 갱신
    // 채널 업데이트
    @Override
    public Channel updateChannel(UUID uuid, String newName) {
        Channel existing = findChannelById(uuid);
        // 만약 변경하려는 이름이 이미 존재한다면,
        Validation.noDuplicate(
                channelRepo.findAll(),//data.values(),
                ch->ch.getChannelName().equals(newName),
                "이미 존재하는 채널명입니다." + newName
        );
//        if (data.values().stream()
//                .anyMatch(ch -> ch.getChannelName().equals(newName))) {
//            throw new IllegalArgumentException("이미 존재하는 채널명니다." + newName);
//        }
        existing.update(newName);
        channelRepo.save(existing);
        return existing;
    }


    //삭제
    @Override
    public void deleteChannel(UUID uuid) {
        findChannelById(uuid);
        channelRepo.delete(uuid);
        //data.remove(uuid);
    }

}


