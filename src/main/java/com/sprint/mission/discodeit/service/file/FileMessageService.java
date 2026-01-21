package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.PermissionLevel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.io.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private FileMessageRepository repository = new FileMessageRepository();

    @Override
    public Message find(UUID id) {
        Set<Message> usersInFile = findAll();
        return usersInFile.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->new RuntimeException("Message not found: id = " + id));
    }

    @Override
    public Set<Message> findAll() {
        return repository.fileLoad();
    }

    @Override
    public Message create(UUID userID, String msg, UUID channelID) {
        FileUserService users = new FileUserService();
        User user = users.find(userID);

        FileChannelService channels = new FileChannelService();
        Channel channel = channels.find(channelID);

        Message message = new Message(user, msg, channel);
        Set<Message> usersInFile = findAll();
        usersInFile.add(message);
        repository.fileSave(usersInFile);

        return message;
    }

    @Override
    public void delete(UUID messageID, UUID userID) {
        Message deletedMessage = find(messageID);// 삭제 대상 메시지
        FileUserService users = new FileUserService();
        User user = users.find(userID); // 삭제를 시도하는 유저
        boolean canDelete = user.equals(deletedMessage.getUser()) //삭제하려 시도하는 유저가 보낸 유저거나
                || deletedMessage.getChannel()
                .getRoles().stream()
                .anyMatch(
                        r->r.getUsers().equals(user)
                                && r.getRoleName().equals(PermissionLevel.ADMIN)
                ); //관리자인 경우

        if(canDelete){
            deletedMessage.getChannel().getMessages().remove(deletedMessage);
            Set<Message> messages = findAll();
            messages.remove(deletedMessage);
            repository.fileSave(messages);
        }
        else{
            throw new RuntimeException("User not allowed to delete message");
        }
    }

    @Override
    public Message update(UUID id, String msg) {
        Set<Message> messages = findAll();
        Message message= messages.stream()
                .filter(m->m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Message not found: id = " + id));
        message.updateMessage(msg);
        repository.fileSave(messages);

        FileChannelService channelService = new FileChannelService();
        Channel channel = message.getChannel();

        // 채널 내의 기존 메시지 객체를 찾아 내용을 업데이트
        channel.getMessages().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .ifPresent(m -> m.updateMessage(msg));

        // 변경된 채널 정보를 파일에 저장
        channelService.update(channel.getId(), channel.getRoles(), channel.getMessages());


        return message;
    }

    public Message update(Message message){
        Set<Message> messages = findAll();
        messages.removeIf(c -> c.getId().equals(message.getId()));
        messages.add(message);
        repository.fileSave(messages);
        return message;
    }

}
