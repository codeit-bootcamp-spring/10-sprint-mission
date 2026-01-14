package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;

import java.util.*;

public class JCFMessageService implements MessageService{
    private final Map<UUID, Message> data = new HashMap<>();
    private Map<UUID, List<Message>> channelIndex = new HashMap<>(); // 특정 채널의 메시지 조회용 인덱스
    private final UserService userService;
    private final ChannelService channelService;


    public JCFMessageService(UserService userService, ChannelService channelService){
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void create(Message message) {
        validateCreateMessage(message);
        data.put(message.getId(), message);
        channelIndex.computeIfAbsent(message.getChannel().getId(), k -> new ArrayList<>())
                .add(message);
    }

    @Override
    public Message readById(UUID id){
        return data.get(id);
    }

    @Override
    public List<Message> readAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> readAllByChannelId(UUID channelId, UUID userId) { // 특정 채널의 메시지 조회
        validateAccess(userId, channelId);
        return channelIndex.getOrDefault(channelId, Collections.emptyList());
    }

    @Override
    public void update(Message message) {
        if (data.containsKey(message.getId())) {
            data.put(message.getId(), message);
        }
    }

    @Override
    public void delete(UUID id) {
        Message message = data.get(id);

        if (message != null) {
            data.remove(id);
            UUID channelId = message.getChannel().getId();
            List<Message> messagesInChannel = channelIndex.get(channelId);

            if (messagesInChannel != null) {
                messagesInChannel.remove(message);
                if (messagesInChannel.isEmpty()) {
                    channelIndex.remove(channelId);
                }
            }
        }
    }


    // 검증
    private void validateAccess(UUID userId, UUID channelId) {
        // 유저 존재 확인
        userService.validateUserStatus(userId);

        // 채널 존재 확인
        channelService.validateChannelStatus(channelId);

        // 채널 멤버 확인
        User user = userService.readById(userId);
        Channel channel = channelService.readById(channelId);
        if (!channel.isMember(user)) {
            throw new IllegalArgumentException("실패: 채널 멤버만 접근할 수 있음");
        }
    }

    private void validateCreateMessage(Message message) {
        validateAccess(message.getUser().getId(), message.getChannel().getId());
    }
}
