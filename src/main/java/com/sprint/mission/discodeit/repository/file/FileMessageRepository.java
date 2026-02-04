package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
@ConditionalOnProperty(
        name = "discodeit.repository.type",
        havingValue = "file"
)
public class FileMessageRepository extends BaseFileRepository<Message> implements MessageRepository {
    public FileMessageRepository(@Value("${discodeit.repository.file-directory}") String directory) {
        super(directory + "/messages.ser");
    }

    @Override
    public Message save(Message message){
        Map<UUID, Message> data = loadData();
        data.put(message.getId(), message);
        saveData(data);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id){
        return Optional.ofNullable(loadData().get(id));
    }

    @Override
    public List<Message> findAll(){
        return new ArrayList<>(loadData().values());
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId){
        Map<UUID, Message> data = loadData();
        return data.values().stream()
                .filter(message -> message.getChannel().getId().equals(channelId))
                .toList();
    }

    @Override
    public void deleteById(UUID id){
        Map<UUID, Message> data = loadData();
        data.remove(id);
        saveData(data);
    }

    // 특정 채널의 모든 메시지 삭제
    @Override
    public void deleteByChannelId(UUID channelId){
        Map<UUID, Message> data = loadData();

        data.entrySet().removeIf(entry ->
                entry.getValue().getChannel().getId().equals(channelId)
        );

        saveData(data);
    }
}
