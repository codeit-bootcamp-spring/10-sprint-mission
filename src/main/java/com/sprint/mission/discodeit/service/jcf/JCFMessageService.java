
package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JCFMessageService implements MessageService {

    private final List<Message> data;
    private UserService userService;
    private ChannelService channelService;

    public JCFMessageService(UserService userService,ChannelService channelService) {
        this.data = new ArrayList<>();
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public Message createMessage(UUID userId, UUID channelId, String content) {
        // 1. userId로 실제 User 조회 (없으면 예외)
        User user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 2. channelId로 실제 Channel 조회 (없으면 예외)
        Channel channel = channelService.findById(channelId)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelId));

        // 3. 조회한 실제 객체로 Message 생성
        Message message = new Message(user, channel, content);
        data.add(message);
        return message;
    }

    @Override
    public Optional<Message> findById(UUID id) {
        return data.stream()
                .filter(channel -> channel.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data);
    }

    @Override
    public void updateMessage(UUID id, String content, UUID userId, UUID channelId) {
       //1. 수정할 메시지 찾기
        Message message = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Message not found"));

        //2. content가 null이 아니면 수정
        Optional.ofNullable(content).ifPresent(message::setContent);

        //3. userId가 null이 아니면 -> User 조회 후 설정
        if(userId != null) {
            User user = userService.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
            message.setUser(user);
        }
        // 4. channelId가 null이 아니면 → Channel 조회 후 설정
        if (channelId != null) {
            Channel channel = channelService.findById(channelId)
                    .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + channelId));
            message.setChannel(channel);
        }
        message.touch();

    }



    @Override
    public void delete(UUID id) {
        Message message = findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Channel not found: " + id));

        data.remove(message);
    }

}
