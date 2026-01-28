package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.jcf.UserService;
import com.sprint.mission.discodeit.utils.CheckValidation;
import com.sprint.mission.discodeit.utils.SaveLoadUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.*;

public class JCFChannelRepository implements ChannelRepository {

    List<Channel> data = new ArrayList<>();

    @Override
    public void save(Channel channel) {
        Objects.requireNonNull(channel, "채널이 유효하지 않습니다.");
        data.add(channel);
    }

    @Override
    public Channel findByID(UUID uuid) {
        Objects.requireNonNull(uuid, "유효하지 않은 uuid 입니다.");

        return data
                .stream()
                .filter(c -> uuid.equals(c.getId()))
                .findFirst()
                .orElseThrow(()->new IllegalStateException("존재하지 않음"));
    }

    @Override
    public List<Channel> findAll() {
        return this.data;
    }


    public List<Channel> load() {
        return this.data;
    }

    @Override
    public Channel delete(Channel channel) {
        data.remove(channel);
        return channel;
    }
}
