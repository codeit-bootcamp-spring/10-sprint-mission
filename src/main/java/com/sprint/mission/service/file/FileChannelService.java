package com.sprint.mission.service.file;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.User;
import com.sprint.mission.service.ChannelService;
import com.sprint.mission.service.UserService;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FileChannelService extends BaseFileService implements ChannelService {
    private final UserService userService;

    public FileChannelService(Path directory, UserService userService) {
        super(directory);
        this.userService = userService;
    }

    @Override
    public Channel create(UUID userID, String name) {
        Map<UUID, Channel> data = loadData();

        validateNotDuplicatedChannelName(data, name);

        User owner = userService.findById(userID);
        Channel channel = new Channel(owner, name);

        owner.joinChannel(channel);

        Path filePath = getFilePath(channel);
        save(filePath, channel);

        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Map<UUID, Channel> data = loadData();
        return getChannelOrThrow(data, id);
    }

    @Override
    public List<User> findByChannelId(UUID channelId) {
        return List.copyOf(findById(channelId).getUsers());
    }

    @Override
    public List<Channel> findAll() {
        return List.copyOf(loadData().values());
    }

    @Override
    public Channel update(UUID id, String name) {
        Channel channel = findById(id);
        channel.updateName(name);

        Path filePath = getFilePath(channel);
        save(filePath, channel);

        return channel;
    }

    @Override
    public void deleteById(UUID id) {
        Channel channel = findById(id);

        Path filePath = getFilePath(channel);
        delete(filePath);
    }

    @Override
    public Channel joinChannel(UUID userID, UUID channelId) {
        User user = userService.findById(userID);
        Channel channel = findById(channelId);
        bidirectionalJoin(channel, user);

        save(getFilePath(channel), channel);
        return channel;
    }

    @Override
    public Channel leaveChannel(UUID userID, UUID channelId) {
        User user = userService.findById(userID);
        Channel channel = findById(channelId);
        bidirectionalLeave(channel, user);

        save(getFilePath(channel), channel);
        return channel;
    }

    private void validateNotDuplicatedChannelName(Map<UUID, Channel> data, String name) {
        boolean isDuplicatedChannelName = data.values().stream()
                .anyMatch(channel -> channel.getName().equals(name));
        if (isDuplicatedChannelName) {
            throw new IllegalArgumentException("이미 존재하는 채널명 입니다. 다시 시도해 주세요");
        }
    }

    private Map<UUID, Channel> loadData() {
        return load(Channel::getId);
    }

    private Path getFilePath(Channel channel) {
        return directory.resolve(channel.getId().toString().concat(".ser"));
    }

    private void bidirectionalJoin(Channel channel, User owner) {
        channel.joinUser(owner);
        owner.joinChannel(channel);
    }

    private void bidirectionalLeave(Channel channel, User user) {
        user.leaveChannel(channel);
        channel.leaveUser(user);
    }

    private Channel getChannelOrThrow(Map<UUID, Channel> data, UUID id) {
        if (!data.containsKey(id)) {
            throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
        }
        return data.get(id);
    }
}
