package com.sprint.mission.discodeit.service.jcf;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import java.util.*;

public class JCFMessageService implements MessageService {

    // 전체 메세지 저장
    private final Map<UUID, Message> repository = new HashMap<>();
    // 채널 JCF 객체 저장
    private final ChannelService channelService;

    public JCFMessageService(ChannelService channelService) {
        this.channelService = channelService;
    }

    @Override
    public void create(String content, User user, Channel channel) {    // 내용, 사용자, 채널 객체 받아서 메세지 생성
        if (content != null && user != null && channel != null && channel.getUserIds().contains(user.getId())) {
            Message message = new Message(content, user.getId(), channel.getId());
            repository.put(message.getId(), message);
            channel.addMessage(message);
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
            message.update(content);
        }
    }

    @Override
    public void delete(UUID id) {
        Message message = repository.remove(id);

        if (message != null) {
            Channel channel = channelService.findById(message.getChannelId());
            if (channel != null) {
                channel.removeMessage(id);
            }
        }
    }
}
