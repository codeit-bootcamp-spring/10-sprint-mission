package com.sprint.mission.discodeit.repository.file;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;


@Repository

public class FileChannelRepository implements ChannelRepository {
    private static final String FILE_PATH = "channels.dat";
    private Map<UUID, Channel> data;

    public FileChannelRepository() {
        this.data = loadFromFile();
    }
    @Override
    public void save(Channel channel) {
        if(channel == null) throw new IllegalArgumentException("채널이 null일 수 없습니다.");
        data.put(channel.getId(), channel);
        saveToFile();
    }
    @Override
    public void delete(UUID id) {
        if(id == null) throw new IllegalArgumentException("채널 ID가 null일 수 없습니다.");
        data.remove(id);
    }
    @Override
    public Optional<Channel> findById(UUID id){
        if(id==null) throw new IllegalArgumentException("채널 ID가 null 일 수 없습니다.");
        return Optional.ofNullable(data.get(id));
    }
    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }





    private void saveToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
            oos.writeObject(data);
        } catch (IOException e) {
            throw new RuntimeException("Channel 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
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
