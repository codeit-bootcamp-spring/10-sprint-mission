package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.utils.CheckValidation;
import com.sprint.mission.discodeit.utils.SaveLoadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileMessageRepository implements MessageRepository {
    private static final String path = "message.dat";
    private final List<Message> data;

    public FileMessageRepository(){
        this.data = new ArrayList<>();
        load();
    }

    public void persist(){
        SaveLoadUtil.save(data,path);
    }

    @Override
    public void save(Message message) {
        Objects.requireNonNull(message, "유효하지 않은 메시지");

        if(data
                .stream()
                .anyMatch(m -> message.getId().equals(m.getId()))){
            throw new IllegalStateException("중복되는 메시지입니다.");
        }

        data.add(message);
        persist();
    }

    @Override
    public Message findByID(UUID uuid) {
        Objects.requireNonNull(uuid, "유효하지 않은 식별자.");

        return CheckValidation.readEntity(data,uuid,() -> new IllegalStateException("존재하지 않는 메시지입니다."));
    }

    @Override
    public List<Message> findAll() {
        return List.copyOf(data);
    }


    public List<Message> load() {
        List<Message> loaded = SaveLoadUtil.load(path);
        if(loaded != null){
            this.data.addAll(loaded);
        }
        return this.data;
    }

    @Override
    public Message delete(Message message) {
        Objects.requireNonNull(message, "유효하지 않은 메시지");

        if(data
                .stream()
                .noneMatch(m -> message.getId().equals(m.getId()))){
            throw new IllegalStateException("해당 메세지는 존재하지 않습니다.");
        }

        data.remove(message);
        persist();

        return message;
    }
}
