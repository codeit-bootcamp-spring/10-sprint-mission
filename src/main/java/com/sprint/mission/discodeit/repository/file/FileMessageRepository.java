package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.*;
import java.util.stream.IntStream;

@Repository
public class FileMessageRepository implements MessageRepository {
    private static final String FILE_PATH = "data/messages.ser";
    final List<Message> data;

    public FileMessageRepository() {
        this.data = loadMessages();
    }

    private void saveMessages(){
        File file = new File(FILE_PATH);
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this.data);
            System.out.println("직렬화 완료: messages.ser에 저장되었습니다.");
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Message> loadMessages() {
        File file = new File(FILE_PATH);
        if(!file.exists() || file.length() == 0) {
            return new ArrayList<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object data = ois.readObject();
            System.out.println("역직렬화 완료: " + data);
            return (List<Message>) data;
        } catch (IOException |ClassNotFoundException e) {
            throw new RuntimeException("메시지 데이터 로드 중 오류 발생", e);
        }
    }

    @Override
    public Message save(Message message) {
        OptionalInt indexOpt = IntStream.range(0, this.data.size())
                .filter(i -> data.get(i).getId().equals(message.getId()))
                .findFirst();
        if (indexOpt.isPresent()) {
            data.set(indexOpt.getAsInt(), message);
        } else {
            data.add(message);
        }

        saveMessages();
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return data.stream()
                .filter(message -> message.getId().equals(id))
                .findFirst();
    }


    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }


    @Override
    public void deleteById(UUID id) {
        data.removeIf(message -> message.getId().equals(id));
        saveMessages();
    }


}
