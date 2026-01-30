//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//
//import java.util.*;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//public class JCFMessageRepository implements MessageRepository {
//    private final Map<UUID, Message> data;
//
//    public JCFMessageRepository() {
//        this.data = new HashMap<>();
//    }
//
//    @Override
//    public Message save(Message message) {
//        data.put(message.getId(), message);
//        return message;
//    }
//
//    @Override
//    public void saveAll(List<Message> messages) {
//        this.data.clear();
//        Map<UUID, Message> map = messages.stream()
//                .collect(Collectors.toMap(msg -> msg.getId(), Function.identity()));
//        this.data.putAll(map);
//    }
//
//    @Override
//    public Optional<Message> findById(UUID id) {
//        return Optional.ofNullable(data.get(id));
//    }
//
//    @Override
//    public List<Message> findAll() {
//        return new ArrayList<>(data.values());
//    }
//
//    @Override
//    public void delete(UUID id) {
//        data.remove(id);
//    }
//
//    @Override
//    public void clear() {
//        this.data.clear();
//    }
//}
