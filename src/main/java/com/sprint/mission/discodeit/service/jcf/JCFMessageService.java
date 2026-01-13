package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final JCFChannelService channelService;

    // 메시지 서비스 생성 시 채널 서비스 주입
    public JCFMessageService(JCFChannelService channelService) {
        this.channelService = channelService;
    }

    // 1. 메시지 생성
    @Override
    public Message messageCreate(UUID channelId, User sender, String content) {
        Channel channel = channelService.channelFind(channelId);

        if (!channel.hasMember(sender.getUserId())) {
            throw new IllegalArgumentException("채널 멤버만 메시지를 작성할 수 있습니다.");
        }

        Message message = new Message(sender, content);
        channel.addMessage(message);

        return message;
    }

    // 2. 메시지 단건 조회
    @Override
    public Message messageFind(UUID channelId, UUID messageId) {
        Channel channel = channelService.channelFind(channelId);
        Message message = channel.getMessages()
                .stream()
                .filter(m -> m.getMessageId().equals(messageId))
                .findFirst()
                .orElse(null);

        if (message == null) {
            throw new IllegalArgumentException("해당 메시지가 존재하지 않습니다.");
        }
        return message;
    }

    // 3. 메시지 전체 조회
    @Override
    public List<Message> messageFindAll(UUID channelId) {
        Channel channel = channelService.channelFind(channelId);
        return new ArrayList<>(channel.getMessages());
    }

    // 4. 메시지 수정
    @Override
    public Message messageUpdate(UUID channelId, UUID messageId, String newContent) {
        Message message = messageFind(channelId, messageId);
        message.updateContent(newContent);
        return message;
    }

    // 5. 메시지 삭제
    @Override
    public void messageDelete(UUID channelId, UUID messageId) {
        Channel channel = channelService.channelFind(channelId); // 채널 가져오기
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        if (!channel.getMessages().stream().anyMatch(m -> m.getMessageId().equals(messageId))) {
            throw new IllegalArgumentException("존재하지 않는 메시지입니다.");
        }

        channel.removeMessage(messageId); // 여기서 실제 삭제
    }
}
