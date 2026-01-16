package com.sprint.mission.service.jcf;

import com.sprint.mission.dto.SendMsgVerificationRequest;
import com.sprint.mission.entity.Message;
import com.sprint.mission.service.MessageService;
import com.sprint.mission.service.validation.MessageServiceValidator;

import java.util.Objects;
import java.util.UUID;

public class JCFMessageService extends JCFBaseService<Message> implements MessageService {
    private static MessageService instance;
    private final MessageServiceValidator validator;

    private JCFMessageService(MessageServiceValidator validator) {
        super();
        this.validator = validator;
    }

    public static MessageService getInstance() {
        return Objects.requireNonNull(instance);
    }

    public static MessageService getInstance(MessageServiceValidator validator) {
        if (Objects.isNull(instance)) {
            instance = new JCFMessageService(validator);
        }
        return instance;
    }

    @Override
    public Message create(UUID userId, UUID channelId, String content) {
        validator.validateIdExist(
                SendMsgVerificationRequest.of(userId, channelId)
        );
        Message message = new Message(userId, channelId, content);
        getData().put(message.getId(), message);
        return message;
    }
}
