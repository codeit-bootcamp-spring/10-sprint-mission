package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.PermissionLevel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class JCFMessageService implements MessageService {
    private static JCFMessageService instance = null;
    private JCFMessageService(){}
    public static JCFMessageService getInstance(){
        if(instance == null){
            instance = new JCFMessageService();
        }
        return instance;
    }

    Set<Message> messages = new HashSet<>();

    @Override
    public Message find(UUID id) {
        return messages.stream()
                .filter(message -> id.equals(message.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Message not found: id = " + id));
    }

    @Override
    public Set<Message> findAll() {
        Set<Message> newMessages = new HashSet<>();
        newMessages.addAll(messages);
        return newMessages;
    }

    @Override
    public Message create(UUID userID, String msg, UUID channelID) {
        User user = JCFUserService.getInstance().find(userID);
        Channel channel = JCFChannelService.getInstance().find(channelID);

        Message newMessage = new Message(user, msg, channel);
        messages.add(newMessage);
        return newMessage;
    }

    @Override
    public void delete(UUID messageID, UUID userID) {
        Message deletedMessage = find(messageID);// 삭제 대상 메시지
        User user = JCFUserService.getInstance().find(userID); // 삭제를 시도하는 유저
        boolean canDelete = user.equals(deletedMessage.getUser()) //삭제하려 시도하는 유저가 보낸 유저거나
                || deletedMessage.getChannel()
                .getRoles().stream()
                .anyMatch(
                        r->r.getUsers().equals(user)
                        && r.getRoleName().equals(PermissionLevel.ADMIN)
                ); //관리자인 경우

        if(canDelete){
            deletedMessage.getChannel().getMessages().remove(deletedMessage);
            messages.remove(deletedMessage);
        }
        else{
            throw new RuntimeException("User not allowed to delete message");
        }

    }

    @Override
    public Message update(UUID id, String msg) {
        Message message= this.find(id);
        message.updateMessage(msg);
        return message;
    }
}
