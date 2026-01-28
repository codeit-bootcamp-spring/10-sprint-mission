package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.service.UserService;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {
    private final File file = new File("channels.dat");
    private final Map<UUID, Channel> channelList;

    public FileChannelRepository(){

        channelList = new HashMap<>();
        loadFile();
    }

    private void loadFile() {
        if (!file.exists()) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Map<UUID, Channel> loaded = (Map<UUID, Channel>) ois.readObject();
            channelList.clear();
            channelList.putAll(loaded);
        } catch (Exception e) {
            throw new RuntimeException("채널 파일 로드 실패",e);
        }
    }

    private void saveFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(channelList);
        } catch (IOException e) {
            throw new RuntimeException("채널 파일 저장 실패",e);
        }
    }

    public Channel save(Channel channel) {
        channelList.put(channel.getId(),channel);
        saveFile();
        return channel;
    }

    public Channel findById(UUID channelId) {
        Channel channel = channelList.get(channelId);
        return channel;
    }

    public List<Channel> findAll() {
        return new ArrayList<>(channelList.values());
    }

    public void deleteById(UUID channelId) {
        channelList.remove(channelId);
        saveFile();
    }
}
