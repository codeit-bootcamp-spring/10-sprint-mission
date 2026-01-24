package com.sprint.mission.discodeit.repository.jcf;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JCFMessageRepository implements MessageRepository {
    private static JCFMessageRepository instance = null;
    public static JCFMessageRepository getInstance(){
        if(instance == null){
            instance = new JCFMessageRepository();
        }
        return instance;
    }
    private JCFMessageRepository(){}

    private Set<Message> messages = new HashSet<>();

    @Override
    public void fileSave(Set<Message> messages) {
        this.messages = messages;
    }

    @Override
    public Set<Message> fileLoad() {
        return messages;
    }

    @Override
    public void fileDelete(UUID id) {
        messages.removeIf(message -> message.getId().equals(id));
    }
}
