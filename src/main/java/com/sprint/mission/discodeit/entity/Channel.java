package com.sprint.mission.discodeit.entity;


import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.*;
import java.util.stream.Collectors;

public class Channel extends BaseEntity {

    private String channelName;
    private Set<UUID> memberIds = new LinkedHashSet<>();
    private final Map<UUID, Message> messages = new LinkedHashMap<>();

    public Channel(String channelName) {
        super();
        this.channelName = channelName;
    }

    public void updateChannelName(String channelName) {
        this.channelName = channelName;
        touch();
    }


    // ID 기반 멤버 추가
    public void addMember(UUID userId) {
        memberIds.add(userId);
        touch(); // 수정 시간 갱신
    }

    public void removeMember(UUID userId) {
        memberIds.remove(userId);
        touch();
    }

    public boolean hasMember(UUID userId) {
        return memberIds.contains(userId);
    }

    // ID 기반 메시지
    public Message addMessage(UUID senderId, String content) {
        if (!hasMember(senderId)) {
            throw new IllegalArgumentException("채널 멤버만 메시지를 작성할 수 있습니다.");
        }

        Message message = new Message(senderId, content);
        messages.put(message.getId(), message);
        touch();

        return message;
    }


    public void removeMessage(UUID messageId) {
        messages.remove(messageId);
        touch();
    }

    public Collection<Message> getMessages() {
        return messages.values();
    }

    public String getChannelName() {
        return channelName;
    }

    public Set<UUID> getMemberIds() {
        return memberIds;
    }
}




