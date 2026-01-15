package com.sprint.mission.discodeit.service.jcf;


import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService {

    private JCFChannelService channelService;

    public JCFMessageService(JCFChannelService channelService) {
        this.channelService = channelService;
    }

    // 1. 메시지 생성
    @Override
    public Message createMessage(UUID channelId, UUID senderId, String content) {
        Channel channel = channelService.findChannel(channelId);
        if (!channel.hasMember(senderId)) {
            throw new IllegalArgumentException("채널 멤버만 메시지를 작성할 수 있습니다.");
        }

        // Channel.addMessage가 Message 객체를 만들어 반환하도록 변경
        Message message = channel.addMessage(senderId, content);

        return message; // Channel 내부 Map과 동일한 객체 반환
    }


    // 2. 메시지 단건 조회
    @Override
    public Message findMessage(UUID channelId, UUID messageId) {
        Channel channel = channelService.findChannel(channelId);
        return channel.getMessages()
                .stream()
                .filter(m -> m.getId().equals(messageId))
                .findFirst()
                .orElseThrow(() -> new MessageNotFoundException("해당 메시지가 존재하지 않습니다."));
    }
    @Override
    public List<Message> findAllMessage(UUID channelId) {
        Channel channel = channelService.findChannel(channelId);
        return new ArrayList<>(channel.getMessages());
    }

    @Override
    public Message updateMessage(UUID channelId, UUID messageId, String newContent) {
        Message message = findMessage(channelId, messageId); // 단건 조회 재사용
        message.updateContent(newContent);
        return message;
    }

    @Override
    public void deleteMessage(UUID channelId, UUID messageId) {
        Channel channel = channelService.findChannel(channelId);
        if (!channel.getMessages().stream().anyMatch(m -> m.getId().equals(messageId))) {
            throw new MessageNotFoundException("존재하지 않는 메시지입니다.");
        }
        channel.removeMessage(messageId);
    }

}
