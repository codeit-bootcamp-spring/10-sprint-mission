package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

@Repository
public class FileChannelRepository implements ChannelRepository {
    private static final String FILE_PATH = "data/channels.ser";
    final List<Channel> data;

    public FileChannelRepository() {
        this.data = loadChannels();
    }
    private void saveChannels(){
        File file = new File(FILE_PATH);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.data);
            System.out.println("직렬화 완료: channels.ser에 저장되었습니다.");
        } catch (Exception e) {
            throw new RuntimeException("채널 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Channel> loadChannels() {
        File file = new File(FILE_PATH);
        if(!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object data = ois.readObject();
            System.out.println("역직렬화 완료: " + data);
            return (List<Channel>) data;
        } catch (IOException |ClassNotFoundException e) {
            throw new RuntimeException("메시지 데이터 로드 중 오류 발생", e);
        }
    }

    @Override
    public Channel save(Channel channel) {
        OptionalInt indexOpt = IntStream.range(0, this.data.size())
                .filter(i -> data.get(i).getId().equals(channel.getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            data.set(indexOpt.getAsInt(), channel);
        } else {
            data.add(channel);
        }

        saveChannels();
        return channel;
    }

    @Override
    public Optional<Channel> findById(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst();
    }


    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data);
    }


    @Override
    public void deleteById(UUID id) {
        data.removeIf(channel -> channel.getId().equals(id));
        saveChannels();
    }
}
