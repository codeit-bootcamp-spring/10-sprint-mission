//package com.sprint.mission.discodeit.repository.jcf;
//
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.UUID;
//
//public class JCFMessageRepository implements MessageRepository {
//    private final HashMap<UUID, Message> data;
//
//    public JCFMessageRepository() {
//        this.data = new HashMap<>();
//    }
//
//    @Override
//    public void save(Message message) {
//        data.put(message.getId(), message);
//    }
//
//    @Override
//    public void delete(UUID messageId) {
//        this.data.remove(messageId);
//    }
//
//    @Override
//    public List<Message> loadAll() {
//        return new ArrayList<>(this.data.values());
//    }
//
//}
