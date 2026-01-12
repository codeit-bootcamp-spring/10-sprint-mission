package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.MessageType;
import com.sprint.mission.discodeit.entity.User;

import java.util.ArrayList;
import java.util.UUID;

public interface MessageService {
    public Message createMessage(String message, User userId, Channel channelId, MessageType type);
    public Message searchMessage(UUID targetMessageId);
    public ArrayList<Message> searchMessageAll();
    public void updateMessage(UUID targetMessageId, String newMessage);
    public void deleteMessage(UUID targetMessageId);
}
