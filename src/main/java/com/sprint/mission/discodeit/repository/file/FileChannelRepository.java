package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.utils.SaveLoadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileChannelRepository implements ChannelRepository {
    private static final String path = "channel.dat";
    private final List<Channel> data;

    public FileChannelRepository() {
        this.data = new ArrayList<>();
        load();
    }

    private void persist(){
        SaveLoadUtil.save(data,path);
    }

    @Override
    public void save(Channel channel) {
        Objects.requireNonNull(channel, "유효하지 않은 채널입력.");

        if(data
                .stream()
                .noneMatch(c -> channel.getId().equals(c.getId()))){
            throw new IllegalStateException("중복되는 채널입니다.");
        }

        data.add(channel);

        persist();
    }

    @Override
    public Channel findByID(UUID uuid) {
        Objects.requireNonNull(uuid, "유효하지 않은 식별자.");

        return data
                .stream()
                .filter(c -> uuid.equals(c.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalStateException("존재하지 않는 채널입니다.")
                );
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(this.data);
    }

    @Override
    public List<Channel> load() {
        this.data.clear();
        this.data.addAll(SaveLoadUtil.load(path));
        return this.data;
    }

    @Override
    public Channel delete(Channel channel) {
        Objects.requireNonNull(channel, "유효하지 않은 채널");

        if(data
                .stream()
                .noneMatch(c -> channel.getId().equals(c.getId()))){
            throw new IllegalStateException("해당 채널은 존재하지 않습니다.");
        }

        data.remove(channel);
        persist();

        return channel;
    }
}
