package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.io.*;
import java.util.*;

public class FileMessageRepository implements MessageRepository {
    private Map<UUID,Message> data;

    public FileMessageRepository(){
        this.data = load();
    }

    @Override
    public Message save(Message message) {
        this.data = load();
        data.put(message.getId(),message);
        persist();
        return message;
    }

    @Override
    public Message findById(UUID messageId) {
        this.data = load();
        Message message = data.get(messageId);
        return message;
    }

    @Override
    public List<Message> findAll() {
        this.data = load();
        return new ArrayList<>(data.values());
    }

    @Override
    public void delete(UUID messageId) {
        this.data = load();
        data.remove(messageId);
        persist();

    }

    //CREATE 객체 직렬화
    public void persist(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("message.ser"))){
            oos.writeObject(data);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Map<UUID, Message> load(){
        File file = new File("message.ser");

        //파일이 없을때 error 방지
        if (!file.exists()) {

            return new HashMap<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream("message.ser"))){

            return (Map<UUID, Message>) ois.readObject();

        }catch (Exception e){
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
