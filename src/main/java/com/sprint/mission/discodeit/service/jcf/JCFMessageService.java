package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserCoordinatorService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public class JCFMessageService implements MessageService {
    private final Map<UUID, Message> data;
    private final UserService userService;
    private final UserCoordinatorService userCoordinatorService;

    public JCFMessageService(UserService userService, UserCoordinatorService userCoordinatorService) {
        data = new HashMap<>();
        this.userService = userService;
        this.userCoordinatorService = userCoordinatorService;
    }


//    @Override
//    public Message sendDirectMessage(UUID senderId, UUID receiverId, String content) {
//        /*
//        1. 송수신자가 포함되어 있는 다이렉트 채널 가져오기
//            - 유저 유효성 확인
//            - 없으면 생성
//        2. 채널에 메세지 보내기- 아래의 샌드 메서드 활용
//         */
//        Channel channel = userCoordinatorService.getOrCreateDirectChannelByChatterIds(senderId, receiverId);
//        Message message = send(senderId, channel.getId(), content);
//        return message;
//    }

    @Override
    public Message send(UUID senderId, UUID channelId, String content) {
        validateContent(content);
        User sender =  userService.getUserById(senderId);//사실 아래에서 이미 확인해줌
        Channel channel = userCoordinatorService.getChannelByIdAndMemberId(channelId, senderId);
        Message message = new Message(sender,channel, content);
        data.put(message.getId(), message);
        return message;
    }

    @Override
    public Message updateMessage(UUID senderId, UUID id, String content) {
        validateContent(content);
        checkSender(id,senderId);
        Message message = getMessageById(id);
        message.setContent(content);
        return message;
    }

    @Override
    public List<Message> getMessagesByChannelIdAndMemberId(UUID channelId, UUID memberId) {
        Channel channel = userCoordinatorService.getChannelByIdAndMemberId(channelId,memberId);
        List<Message> messages
                = data.values()
                    .stream()
                    .filter(e-> e.getChannel().getId().equals(channel.getId()))
                .sorted(Comparator
                        .comparing(Message::getCreatedAt,Comparator.reverseOrder())//시간순
                        .thenComparing(m->m.getSequence())//같은 시간이면 시퀀스로 정렬
                        )
                .toList();
        return messages;
    }

    private Message getMessageById(UUID id) {
        validateMessage(id);
        Message message = data.get(id);
        return message;
    }

    @Override
    public void delete(UUID id, UUID senderId) {
        checkSender(id,senderId);
        data.remove(id);
    }

    private void validateMessage(UUID messageId) {
        boolean exitsMessege = data.containsKey(messageId);
        if(!exitsMessege){
            throw new NoSuchElementException("해당 메세지 없음: "+messageId);
        }
    }
    private void checkSender(UUID messageId, UUID senderId) {
        Message message = getMessageById(messageId);
        userCoordinatorService.getChannelByIdAndMemberId(message.getChannel().getId(), senderId);
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
