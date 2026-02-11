/*
package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.utils.Validation;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService, Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FILE_PATH = "channels.dat";

    private Map<UUID, Channel> data;

    public FileChannelService() {
        this.data = loadFromFile();
    }

    @Override
    public Channel createChannel(String channelName) {
        Validation.notBlank(channelName, "채널 이름");
        Validation.noDuplicate(
                data.values(),
                ch -> ch.getChannelName().equals(channelName),
                "이미 존재하는 채널명입니다: " + channelName
        );

        Channel channel = new Channel(channelName);
        data.put(channel.getId(), channel);
        saveToFile();
        return channel;
    }

    @Override
    public List<Channel> getChannelAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel findChannelById(UUID id) {
        Channel ch = data.get(id);
        if (ch == null) {
            throw new NoSuchElementException("해당 ID의 채널이 존재하지 않습니다: " + id);
        }
        return ch;
    }

    @Override
    public Channel updateChannel(UUID id, String newName) {
        Channel existing = findChannelById(id);
        Validation.noDuplicate(
                data.values(),
                ch -> ch.getChannelName().equals(newName),
                "이미 존재하는 채널명입니다: " + newName
        );
        existing.update(newName);
        saveToFile();
        return existing;
    }

    @Override
    public void deleteChannel(UUID id) {
        findChannelById(id);
        data.remove(id);
        saveToFile();
    }

    @Override
    public Channel getChannelByName(String channelName) {
        return data.values().stream()
                .filter(ch -> ch.getChannelName().equals(channelName))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 채널이 존재하지 않습니다: " + channelName));
    }

    //  파일 저장
    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("채널 저장 중 오류 발생", e);
        }
    }

    //  파일 로드
    private Map<UUID, Channel> loadFromFile() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Map<UUID, Channel>) ois.readObject();
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}
*/