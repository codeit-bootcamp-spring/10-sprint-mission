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
    public Message create(String content, UUID userId, UUID channelId) {
        validateAccess(userId, channelId); // 권한 확인

        Message newMessage = new Message(content, userService.findById(userId), channelService.findById(channelId));
        data.put(newMessage.getId(), newMessage); // 데이터 저장

        channelIndex.computeIfAbsent(channelId, k -> new ArrayList<>()).add(newMessage);

        return newMessage;
    }

    @Override
    public Message findById(UUID id){
        Message message = data.get(id);
        if (message == null) {
            throw new NoSuchElementException("실패: 존재하지 않는 메시지 ID");
        }
        return message;
    }

    @Override
    public List<Message> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public List<Message> findAllByChannelId(UUID channelId, UUID userId) { // 특정 채널의 메시지 조회
        validateAccess(userId, channelId);
        return new ArrayList<>(channelIndex.getOrDefault(channelId, Collections.emptyList()));
    }

    @Override
    public Message update(UUID id, String content) {
        Message message = findById(id);
        message.update(content); // 여기서 isEdited가 true로 변함
        return message;
    }

    @Override
    public void delete(UUID id) {
        Message message = findById(id);
        data.remove(id); // 데이터 삭제

        // channelIndex에서 삭제
        List<Message> channelMessages = channelIndex.get(message.getChannel().getId());
        if (channelMessages != null){
            channelMessages.remove(message);
        }
    }

    // 권한 확인
    private void validateAccess(UUID userId, UUID channelId) {
        // 채널 멤버 확인
        User user = userService.findById(userId);
        Channel channel = channelService.findById(channelId);
        if (!channel.isMember(user)) {
            throw new IllegalArgumentException("실패: 채널 멤버만 접근할 수 있음");
        }
    }
}
