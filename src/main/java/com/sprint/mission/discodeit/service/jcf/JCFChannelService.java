package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;

    // 생성자
    public JCFChannelService() {
        this.data = new HashMap<>();
    }
    public JCFChannelService(Channel channel) {
        this.data = new HashMap<>();
        data.put(channel.getId(), channel);
    }

    @Override
    public Channel create(String channelName) {
        Channel channel = new Channel(channelName);
        this.data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Optional<Channel> read(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public ArrayList<Channel> readAll() {
        return new ArrayList<>(this.data.values());
    }

    @Override
    public Channel updateChannelname(UUID id, String name) {
        try {
            this.data.get(id).updateChannelName(name);
            return this.data.get(id);
        } catch (NoSuchElementException e) {
            System.out.println("변경하고자 하는 데이터가 없습니다.");
        } catch (Exception e) {
            System.out.println("잘못된 응답입니다.");
        }
        return null;
    }

    @Override
    public void delete(UUID id) {
        try {
            data.remove(id);
        } catch (NoSuchElementException e) {
            System.out.println("삭제할 데이터가 존재하지 않습니다.");
        } catch (Exception e) {
            System.out.println("데이터가 존재하지 않습니다.");
        }
    }
}
