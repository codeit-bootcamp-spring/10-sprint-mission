package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserCoordinatorService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class FileMessageService implements MessageService {
    private final FileBasicService<Message> messageData;
    private final UserService userService;
    private final UserCoordinatorService userCoordinatorService;

    public FileMessageService(UserService userService, UserCoordinatorService userCoordinatorService, FileBasicService<Message> messageData) {
        this.messageData = messageData;
        this.userService = userService;
        this.userCoordinatorService = userCoordinatorService;
    }


    @Override
    public Message sendDirectMessage(UUID senderId, UUID receiverId, String content) {
        /*
        1. 송수신자가 포함되어 있는 다이렉트 채널 가져오기
            - 유저 유효성 확인
            - 없으면 생성
        2. 채널에 메세지 보내기- 아래의 샌드 메서드 활용
         */
        Channel channel = userCoordinatorService.findOrCreateDirectChannelByChatterIds(senderId, receiverId);
        Message message = send(senderId, channel.getId(), content);
        return message;
    }

    @Override
    public List<Message> findDirectMessagesBySenderIdAndReceiverId(UUID senderId, UUID receiverId) {
        Channel channel = userCoordinatorService.findOrCreateDirectChannelByChatterIds(senderId, receiverId);
        return findMessagesByChannelIdAndMemberId(channel.getId(), senderId);
    }


    @Override
    public Message send(UUID senderId, UUID channelId, String content) {
        validateContent(content);
        User sender =  userService.findUserById(senderId);//사실 아래에서 이미 확인해줌
        Channel channel = userCoordinatorService.findAccessibleChannel(channelId, senderId);
        Message message = new Message(sender,channel, content);
        messageData.put(message.getId(), message);//단방향이라서 다른 파일 저장 필요없음
        return message;
    }

    @Override
    public Message updateMessage(UUID senderId, UUID id, String content) {
        validateContent(content);
        checkSender(id,senderId);
        Message message = getMessageById(id);
        message.setContent(content);
        messageData.put(message.getId(), message);//단방향이라서 다른 파일 저장 필요없음
        return message;
    }

    @Override
    public List<Message> findMessagesByChannelIdAndMemberId(UUID channelId, UUID memberId) {
        Channel channel = userCoordinatorService.findAccessibleChannel(channelId,memberId);
        List<Message> messages
                = messageData.values()
                .stream()
                .filter(e-> e.getChannel().getId().equals(channel.getId()))
                .sorted(Comparator
                        .comparing(Message::getCreatedAt)//시간순
                        .thenComparing(m->m.getSequence())//같은 시간이면 시퀀스로 정렬
                )
                .toList();
        return messages;
    }

    private Message getMessageById(UUID id) {
        validateMessage(id);
        Message message = messageData.get(id);
        return message;
    }

    @Override
    public void deleteByIdAndSenderId(UUID id, UUID senderId) {
        checkSender(id,senderId);
        messageData.remove(id);//단방향이라서 다른 데이터 파일 저장 필요없음
    }

    private void validateMessage(UUID messageId) {
        boolean exitsMessege = messageData.containsKey(messageId);
        if(!exitsMessege){
            throw new NoSuchElementException("해당 메세지 없음: "+messageId);
        }
    }
    private void checkSender(UUID messageId, UUID senderId) {
        Message message = getMessageById(messageId);
        userCoordinatorService.findAccessibleChannel(message.getChannel().getId(), senderId);
        if(!message.getSender().getId().equals(senderId)){
            throw new IllegalArgumentException("작성자가 아님: 사용자ID-"+senderId);
        }
    }
    private void validateContent(String content) {
        if(content == null || content.isEmpty()){
            throw new IllegalArgumentException("비어있는 메세지");
        }
    }
}
