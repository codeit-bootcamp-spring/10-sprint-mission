package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private final String FILE_PATH = "channel.dat";
    // 객체 불러오기
    private List<Channel> loadData() {
        File file = new File(FILE_PATH);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    // 객체 저장하기
    private void saveData(List<Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("channel.dat"))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Channel createChannel(String channelName){
        List<Channel> data = loadData();
        if (data.stream().anyMatch(channel -> channel.getChannelName().equals(channelName))) {
            throw new IllegalArgumentException("같은 이름의 채널이 존재합니다. " + channelName);
        }
        Channel channel = new Channel(channelName);
        data = loadData();
        data.add(channel);
        saveData(data);
        System.out.println(channel.getChannelName() + "채널 생성이 완료되었습니다.");
        return channel;
    }

    public Channel findId(UUID channelID){
        List<Channel> data = loadData();
        return data.stream()
                .filter(channel -> channel.getId().equals(channelID))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 채널은 존재하지 않습니다."));
    }

    public Channel findName(String name){
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("내용이 비어있거나 공백입니다.");
        }
        List<Channel> data = loadData();
        return data.stream()
                .filter(channel -> channel.getChannelName().equals(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("해당 채널은 존재하지 않습니다."));
    }

    public List<Channel> findAll(){
        return loadData();
    }


    public List<Channel> findChannels(UUID userId){
        List<Channel> data = loadData();
        return data.stream()
                .filter(channel -> channel.getUserList().stream().anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    public Channel update(UUID channelId, String channelName){
        if (channelName == null || channelName.trim().isEmpty()) {
            throw new IllegalArgumentException("내용이 비어있거나 공백입니다.");
        }
        List<Channel> data = loadData();
        Channel foundChannel = findId(channelId);
        Optional.ofNullable(channelName)
                .filter(name -> !name.trim().isEmpty())
                .ifPresent(foundChannel::setChannelName);
        saveData(data);
        return foundChannel;
    }

    public void delete(UUID channelId){
        Channel target = findId(channelId);
        List<Channel> data = loadData();
        data.remove(target);
        saveData(data);
    }

    @Override
    public void deleteAll() {
        List<Channel> data = new ArrayList<>();
        saveData(data);
        System.out.println("모든 채널 데이터를 초기화했습니다.");
    }
}
