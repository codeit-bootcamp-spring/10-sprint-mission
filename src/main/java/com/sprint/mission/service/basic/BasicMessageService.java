package com.sprint.mission.service.basic;

import com.sprint.mission.entity.Channel;
import com.sprint.mission.entity.EntityType;
import com.sprint.mission.entity.Message;
import com.sprint.mission.entity.User;
import com.sprint.mission.repository.MessageRepository;
import com.sprint.mission.service.MessageService;

public class BasicMessageService extends BasicBaseService<Message, MessageRepository> implements MessageService {
    public BasicMessageService(MessageRepository repository) {
        super(repository, EntityType.MESSAGE);
    }

    @Override
    public Message create(User user, Channel channel, String content) {
        Message message = new Message(user, channel, content);
        repository.write(message);
        return message;
    }
}
