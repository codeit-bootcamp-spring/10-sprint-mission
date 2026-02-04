package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.Validators;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.stream.IntStream;

@Repository
@ConditionalOnProperty(
        prefix = "discodeit.repository",
        name = "type",
        havingValue = "file"
)
public class FileMessageRepository implements MessageRepository {
    private static final String FILE_NAME = "messages.ser";
    private final Path filePath;
    final List<Message> data;

    public FileMessageRepository(
            @Value("${discodeit.repository.file-directory:.discodeit}")  String dir
    ) {
        this.filePath = Paths.get(dir, FILE_NAME);
        this.data = loadMessages();
    }

    private void saveMessages(){
        try(ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath.toFile()))) {
            oos.writeObject(this.data);
            System.out.println("직렬화 완료: messages.ser에 저장되었습니다.");
        } catch (Exception e) {
            throw new RuntimeException("메시지 데이터 저장 중 오류 발생", e);
        }
    }

    @SuppressWarnings("unchecked")
    private List<Message> loadMessages() {
        if(!filePath.toFile().exists() || filePath.toFile().length() == 0) {
            return new ArrayList<>();
        }
        try(ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath.toFile()))) {
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

    @Override
    public void deleteAllByChannelId(UUID channelId) {
        data.removeIf(message -> message.getChannelId().equals(channelId));
        saveMessages();
    }

    @Override
    public Optional<Instant> findLatestCreatedAtByChannelId(UUID channelId) {
        return data.stream()
                .filter(message -> message.getChannelId().equals(channelId))
                .map(Message::getCreatedAt)
                .max(Comparator.naturalOrder());
    }

}
