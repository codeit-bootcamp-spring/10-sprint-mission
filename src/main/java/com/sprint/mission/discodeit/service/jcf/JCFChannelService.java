package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.util.Validator;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> channels;
    private MessageService messageService;

    public JCFChannelService() {
        channels = new HashMap<>();
    }

    // 외부에서 객체를 받는 것 보다는 메소드 내부에서 객체 생성해서 반환
    @Override
    public Channel createChannel(String channelName) {
        Validator.validateNotNull(channelName, "생성하고자하는 채널의 채널명이 null일 수 없음");
        Validator.validateNotBlank(channelName, "생성하고자하는 채널의 채널명이 빈문자열일 수 없음");
        Channel channel = new Channel(channelName.trim());
        channels.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        Channel channel = channels.get(id);
        if (channel == null) {
            throw new IllegalStateException("해당 id의 채널을 찾을 수 없음");
        }
        return channel;
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(channels.values());
    }

    @Override
    public Channel updateById(UUID id, String newChannelName) {
        Validator.validateNotNull(newChannelName, "업데이트하고자 하는 채널의 채널명이 null일 수 없음");
        Validator.validateNotBlank(newChannelName, "업데이트하고자 하는 채널의 채널명이 빈 문자열일 수 없음");
        Channel targetChannel = findById(id);
        targetChannel.setChannelName(newChannelName.trim());
        return targetChannel;
    }

    // 채널 삭제시 참여 중인 유저와 작성된 메시지들과의 연관데이터도 제거
    @Override
    public void deleteById(UUID id) {
        Channel channel = findById(id);
        // 채널에 참여 중인 유저 리스트
        List<User> users = channel.getJoinedUsers().stream().toList();
        // 채널에 남겨진 메시지 리스트
        List<Message> messages = channel.getMessageList().stream().toList();
        // 유저 객체의 채널 리스트에서 채널 삭제
        for (User user : users) {
            user.leaveChannel(channel);
        }
        // 메시지 삭제
        for (Message message : messages) {
            messageService.deleteById(message.getId());
            message.getUser().removeMessage(message, channel);
        }
        channels.remove(id);
    }

    // 해당 user Id를 가진 유저가 속한 채널 목록을 반환
    @Override
    public List<Channel> getChannelsByUserId(UUID userId) {
        return channels.values().stream()
                .filter(channel ->
                        channel.getJoinedUsers().stream()
                                .anyMatch(user -> user.getId().equals(userId)))
                .toList();
    }

    @Override
    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }
}
