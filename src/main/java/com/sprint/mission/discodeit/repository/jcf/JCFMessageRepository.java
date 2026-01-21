package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.utils.CheckValidation;
import com.sprint.mission.discodeit.utils.SaveLoadUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {

    private List<Message> data = new ArrayList<>();

    @Override
    public void save(Message message) {
        data.add(message);
    }

    @Override
    public Message findByID(UUID uuid) {
        Objects.requireNonNull(uuid,"유효하지 않은 매개변수입니다.");

        return CheckValidation.readEntity(data,uuid,()->new IllegalStateException("존재하지 않는 메시지입니다."));
    }

    @Override
    public List<Message> findAll() {
        return this.data;
    }

    @Override
    public List<Message> load() {
        return this.data;
    }

    @Override
    public Message delete(Message message) {
        data.remove(message);
        return message;
    }
}
