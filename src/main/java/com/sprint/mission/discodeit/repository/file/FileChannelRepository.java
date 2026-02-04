package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Repository("channelRepository")
public class FileChannelRepository implements ChannelRepository {
    private List<Channel> data;

    public FileChannelRepository() {
        this.data = new ArrayList<>();
    }

    // 직렬화
    public void serialize(List<Channel> channels) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("channelList.ser"))) {
            oos.writeObject(channels);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 역직렬화
    public List<Channel> deserialize() {
        File file = new File("channelList.ser");
        List<Channel> newChannels = new ArrayList<>();

        if (!file.exists()) {
            return newChannels;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            newChannels = (List<Channel>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("역직렬화가 안됨");
        }
        return newChannels;
    }

    @Override
    public void save(Channel channel) {
        this.data.add(channel);
        serialize(this.data);
    }

    @Override
    public void delete(UUID channelId) {
        this.data = deserialize();
        for (Channel channel : this.data) {
            if (channel.getId().equals(channelId)) {
                this.data.remove(channel);
                serialize(this.data);
                break;
            }
        }
    }

    public Channel updateChannelname(UUID channelId, String name) {
        this.data = deserialize();
        for (Channel channel : this.data) {
            if (channel.getId().equals(channelId)) {
                channel.updateChannelName(name);
                serialize(this.data);
                return channel;
            }
        }
        throw new NoSuchElementException();
    }

    @Override
    public List<Channel> loadAll() {
        return deserialize();
    }

    @Override
    public Channel loadById(UUID channelId) {
        this.data = deserialize();
        for (Channel channel : this.data) {
            if (channel.getId().equals(channelId)) {
                return channel;
            }
        }
        throw new NoSuchElementException();
    }
}
