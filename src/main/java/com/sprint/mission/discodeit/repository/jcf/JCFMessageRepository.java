package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;

import java.util.*;

public class JCFMessageRepository implements MessageRepository {
    private final Map<UUID, Message> data;
//    private final UserRepository userRepository;
//    private final ChannelRepository channelRepository;
    // 다른 레포지토리를 의존하면 안됨

    // 다른 레포지토리 생성자로 주입 X(다른 레포지토리를 의존하지 않도록)
    public JCFMessageRepository() {
        data = new HashMap<UUID, Message>();
    }

    @Override
    public Message save(Message message) {
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return Optional.ofNullable(data.get(id));
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public void deleteById(UUID id) {
        data.remove(id);
    }

//    @Override
//    public List<Message> getMessagesByUserId(UUID userId) {
//        return data.values().stream()
//                .filter(message -> message.getUser()!=null && message.getUser().getId().equals(userId))
//                .toList();
//    }
    // 서비스 영역으로

//    @Override
//    public List<Message> getMessagesByChannelId(UUID channelId) {
//        return data.values().stream()
//                .filter(message -> message.getChannel()!=null && message.getChannel().getId().equals(channelId))
//                .toList();
//    }
    // 서비스 영역으로

    // JCF 레포지토리에는 필요하지 않은 기능
//    @Override
//    public void loadData() {
//
//    }
//
//    @Override
//    public void saveData() {
//
//    }
}
