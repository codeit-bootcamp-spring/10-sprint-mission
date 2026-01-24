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
                );

        if (canDelete) {
            // 1) messages.dat에서 삭제
            Set<Message> messages = findAll();
            messages.remove(deletedMessage);
            repository.fileSave(messages);

            // 2) channels.dat 업데이트는 "항상 messages.dat 기준으로 재구성"
            UUID channelId = deletedMessage.getChannel().getId();

            List<Message> channelMessages = messages.stream()
                    .filter(m -> m.getChannel() != null && channelId.equals(m.getChannel().getId()))
                    .toList();

            FileChannelService channelService = new FileChannelService();

            // roles는 deletedMessage 안의 channel(복사본) 말고, 채널 파일에서 최신으로 가져오기
            Channel latestChannel = channelService.find(channelId);

            channelService.update(channelId, latestChannel.getRoles(), channelMessages);
        } else {
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

        // 1) 메시지 전체 저장
        repository.fileSave(messages);

        // 2) 채널 업데이트는 "message.getChannel().getMessages()" 쓰지 말고
        //    전체 메시지에서 채널 메시지를 다시 구성해서 넣기
        UUID channelId = message.getChannel().getId();

        List<Message> channelMessages = messages.stream()
                .filter(m -> m.getChannel() != null && channelId.equals(m.getChannel().getId()))
                .toList();

        FileChannelService channelService = new FileChannelService();

        // roles도 message 안의 channel에서 가져오면 복사본일 수 있어서,
        // 가능하면 채널을 다시 조회해서 roles를 가져오는게 더 안전
        channelService.update(channelId, message.getChannel().getRoles(), channelMessages);

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
