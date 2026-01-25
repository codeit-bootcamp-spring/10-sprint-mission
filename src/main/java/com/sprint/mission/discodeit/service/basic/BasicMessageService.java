package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.PermissionLevel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.RoleRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.Set;
import java.util.UUID;

public class BasicMessageService implements MessageService {
    private MessageRepository messageRepository = FileMessageRepository.getInstance();
    private UserRepository userRepository;
    private ChannelRepository channelRepository;
    private RoleRepository roleRepository;

    public BasicMessageService(MessageRepository messageRepository
            , UserRepository userRepository
            , ChannelRepository channelRepository
            , RoleRepository roleRepository)
    {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.channelRepository = channelRepository;
        this.roleRepository = roleRepository;

    }

    @Override
    public Message find(UUID id) {
        return messageRepository.fileLoad(id);
    }

    @Override
    public Set<Message> findAll() {
        return messageRepository.fileLoadAll();
    }

    @Override
    public Message create(UUID userID, String msg, UUID channelID) {
        User user = userRepository.fileLoad(userID);
        Channel channel = channelRepository.fileLoad(channelID);

        Message message = new Message(userID, msg, channelID);
        Set<Message> usersInFile = findAll();
        usersInFile.add(message);
        messageRepository.fileSave(usersInFile);

        return message;
    }

    @Override
    public void delete(UUID messageID, UUID userID) {
        Message deletedMessage = find(messageID);// 삭제 대상 메시지
        Channel channel = channelRepository.fileLoad(deletedMessage.getChannelID());

        boolean canDelete = userID.equals(deletedMessage.getUserID()) //삭제하려 시도하는 유저가 보낸 유저거나
                || channelRepository.fileLoad(deletedMessage.getChannelID())//관리자일 경우
                .getRolesID().stream()
                .map(roleRepository::fileLoad)
                .anyMatch(
                        r->r.getUserID().equals(userID)
                                && r.getRoleName().equals(PermissionLevel.ADMIN)
                );

        if (canDelete) {
            //채널에서 이 메시지의 아이디 삭제
            channel.DeleteMessageInChannel(messageID);

            Set<Channel> channels = channelRepository.fileLoadAll();
            channels.removeIf(ch -> ch.getId().equals(channel.getId()));
            channels.add(channel);
            channelRepository.fileSave(channels);

            //messages.dat에서 삭제
            Set<Message> messages = findAll();
            messages.remove(deletedMessage);
            messageRepository.fileSave(messages);

        }
        else{
            throw new RuntimeException("User not allowed to delete message");
        }
    }

    @Override
    public Message update(UUID id, String msg) {
        Set<Message> messages = findAll();

        Message message = messages.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Message not found: id = " + id));

        message.updateMessage(msg);

        // 메시지 전체 저장
        messageRepository.fileSave(messages);

        return message;
    }

    public Message update(Message message){
        Set<Message> messages = findAll();
        messages.removeIf(c -> c.getId().equals(message.getId()));
        messages.add(message);
        messageRepository.fileSave(messages);
        return message;
    }

}
