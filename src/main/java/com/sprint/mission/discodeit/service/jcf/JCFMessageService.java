package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFMessageService implements MessageService {

    // 메모리 저장소: Key는 Message의 ID, Value는 Message 객체
    private final Map<UUID, Message> repository = new HashMap<>();
    private final ChannelService channelService;

    public JCFMessageService(ChannelService channelService){
        this.channelService = channelService;
    }

    @Override
    public void create(Message message) {
        // 메시지 객체가 null이거나 ID가 없는 경우를 대비한 방어 코드
        if (message != null && message.getId() != null) {
            Channel channel = channelService.findById(message.getChannelId());

            if (channel != null) {
                repository.put(message.getId(), message);
                channel.addMessage(message);
            }
        }
    }

    @Override
    public Message findById(UUID id) {
        return repository.get(id);
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId) {
        // 해당 채널 ID를 가진 메시지만 필터링하여 리스트로 반환
        Channel channel = channelService.findById(channelId);
        return (channel != null) ? channel.getMessages() : List.of();
    }

    @Override
    public void update(UUID id, String content) {
        Message message = repository.get(id);
        if (message != null) {
            // 메시지의 내용(content)을 업데이트
            message.update(content); // Message 엔티티에 해당 메서드가 있다고 가정
        }
    }

    @Override
    public void delete(UUID id) {
        Message message = repository.remove(id);

        if (message != null) {
            Channel channel = channelService.findById(message.getChannelId());
            if (channel != null){
                channel.removeMessage(id);
            }
        }
    }
}
