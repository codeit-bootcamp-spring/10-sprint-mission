package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;

import java.io.*;
import java.util.*;

public class FileChannelRepository implements ChannelRepository {

    private Map<UUID,Channel> data;

    public FileChannelRepository(){
        this.data = load();
    }

    @Override
    public Channel save(Channel channel) {
        this.data = load();
        data.put(channel.getId(),channel);
        persist();
        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        this.data = load();
        Channel channel = data.get(channelId);
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        this.data = load();
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID channelId) {
        this.data = load();
        data.remove(channelId);
        persist();

    }

    //CREATE 객체 직렬화
    public void persist(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("channel.ser"))){
            oos.writeObject(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Map<UUID, Channel> load(){
        File file = new File("channel.ser");

        //파일이 없을때 error 방지
        if (!file.exists()) {

            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("channel.ser"))){

            return (Map<UUID, Channel>) ois.readObject();

        }catch (Exception e){
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
