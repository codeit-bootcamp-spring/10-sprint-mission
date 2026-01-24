package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.PermissionLevel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class FileMessageService implements MessageService {
    private FileMessageRepository repository = FileMessageRepository.getInstance();
    private FileUserService userService = new FileUserService();
    private FileChannelService channelService = new FileChannelService();
    private FileRoleService roleService = new FileRoleService();

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
        User user = userService.find(userID);
        Channel channel = channelService.find(channelID);

        Message message = new Message(userID, msg, channelID);
        Set<Message> usersInFile = findAll();
        usersInFile.add(message);
        repository.fileSave(usersInFile);

        return message;
    }

    @Override
    public void delete(UUID messageID, UUID userID) {
        Message deletedMessage = find(messageID);// 삭제 대상 메시지
        boolean canDelete = userID.equals(deletedMessage.getUserID()) //삭제하려 시도하는 유저가 보낸 유저거나
                || channelService.find(deletedMessage.getChannelID())//관리자일 경우
                .getRolesID().stream()
                .map(roleService::find)
                .anyMatch(
                        r->r.getUserID().equals(userID)
                                && r.getRoleName().equals(PermissionLevel.ADMIN)
                );

        if (canDelete) {
            //채널에서 이 메시지의 아이디 삭제
            Channel targetChannel = channelService.find(deletedMessage.getChannelID());
            targetChannel.DeleteMessageInChannel(messageID);
            channelService.update(targetChannel.getId(), targetChannel.getRolesID(), targetChannel.getMessagesID());

            //messages.dat에서 삭제
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

        Message message = messages.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Message not found: id = " + id));

        message.updateMessage(msg);

        // 메시지 전체 저장
        repository.fileSave(messages);

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
