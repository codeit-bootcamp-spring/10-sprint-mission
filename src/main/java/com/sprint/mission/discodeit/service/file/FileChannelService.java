package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.IsPrivate;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.ClearMemory;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileChannelService extends AbstractFileService implements ChannelService, ClearMemory {

    private final UserService userService;

    public FileChannelService(String path, UserService userService) {
        super(path);
        this.userService = userService;
    }

    private void save(Channel channel) {
        Map<UUID, Channel> data = load();

        if(data.containsKey(channel.getId())){
            Channel existing = data.get(channel.getId());
            existing.updateName(channel.getName());
            existing.updatePrivate(channel.getIsPrivate());
            existing.updateUpdatedAt(System.currentTimeMillis());
            data.put(existing.getId(), existing);
        }
        else{
            data.put(channel.getId(), channel);
        }
        writeToFile(data);
    }

    @Override
    public Channel create(String name, IsPrivate isPrivate, UUID ownerId) {
        User user = userService.findById(ownerId);
        Channel channel = new Channel(name, isPrivate, user);
        save(channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Map<UUID, Channel> data = load();
        validateExistence(data, id);
        return data.get(id);
    }

    @Override
    public List<Channel> readAll() {
        Map<UUID, Channel> data = load();
        return List.copyOf(data.values());
    }

    @Override
    public Channel update(UUID id) {
        Map<UUID, Channel> data = load();
        validateExistence(data, id);
        Channel channel = findById(id);
        save(channel);
        return channel;
    }

    @Override
    public void delete(UUID id) {
        remove(id);
    }

    private void remove(UUID id) {
        Map<UUID, Channel> data = load();
        validateExistence(data, id);
        data.remove(id);
        writeToFile(data);
    }

    @Override
    public void clear() {
        writeToFile(new HashMap<UUID, Channel>());
    }

    private void validateExistence(Map<UUID, Channel> data, UUID id){
        if (!data.containsKey(id)) {
            throw new NoSuchElementException("실패 : 존재하지 않는 채널 ID입니다.");
        }
    }
}
