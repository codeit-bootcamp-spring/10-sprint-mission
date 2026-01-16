package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final List<Channel> data;

    public JCFChannelService() {
        this.data = new ArrayList<>();
    }

    @Override
    public Channel create(String name) {
        Channel channel = new Channel(name);
        data.add(channel);
        return channel;
    }

    @Override
    public Channel read(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst()
                .orElseThrow( () -> new NotFoundException("해당 ID의 채널을 찾을 수 없습니다"));
    }

    @Override
    public List<Channel> readAll() {
        return new ArrayList<>(data);
    }

    @Override
    public List<Channel> getChannelsByUser(UUID userId) {
        // UserService와의 순환 참조를 방지하기 위해 내부에서 userService.read()를 직접 호출하지 않음.
        // 따라서 본 메서드 호출 전, DiscordService 등 상위 레이어에서 유저 존재 여부를 먼저 검증하는 것을 권장함.
        return data.stream()
                .filter(channel -> channel.getUserList().stream()
                        .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = read(id);
        return channel.update(name);
    }

    @Override
    public void delete(UUID id) {
        Channel channel = read(id);
        data.remove(channel);
    }

    @Override
    public void deleteUserInChannels(UUID userId) {
        getChannelsByUser(userId)
                .forEach(channel ->
                        channel.getUserList().removeIf(user -> user.getId().equals(userId)));
        }
    }

