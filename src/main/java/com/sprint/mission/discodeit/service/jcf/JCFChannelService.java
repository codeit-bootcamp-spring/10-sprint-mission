package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data = new HashMap<>();
    private final JCFUserService userService;

    public JCFChannelService(JCFUserService userService) {
        this.userService = userService;
    }

    // 채널 생성
    @Override
    public Channel create(String name, String description, String type, boolean isPublic) {
        Channel newChannel = new Channel(name, description, type, isPublic);
        data.put(newChannel.getId(), newChannel);
        return newChannel;
    }

    // 채널 ID로 조회
    @Override
    public Channel findById(UUID id){
        Channel channel = data.get(id);
        if (channel == null) {
            throw new NoSuchElementException("실패: 존재하지 않는 채널 ID입니다.");
        }
        return channel;
    }

    // 채널 전부 조회
    @Override
    public List<Channel> findAll(){
        return new ArrayList<>(data.values());
    }

    // 채널 정보 수정
    @Override
    public Channel update(UUID id, String name, String description, boolean isPublic){
        Channel channel = findById(id);
        channel.update(name, description, isPublic);
        return channel;
    }

    // 채널 삭제
    @Override
    public void delete(UUID id){
        findById(id);
        data.remove(id);
    }

    // 채널 참가
    @Override
    public void join(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);

        if (channel.isMember(user)) {
            throw new IllegalStateException("이미 채널에 가입된 유저입니다.");
        }

        channel.addMember(user);
        user.addJoinedChannel(channel);
    }


    // 채널 탈퇴
    @Override
    public void leave(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);

        if (!channel.isMember(user)) {
            throw new IllegalStateException("실패: 해당 채널의 멤버가 아닙니다.");
        }

        channel.removeMember(user);
        user.leaveChannel(channel);
    }

    // 특정 채널의 유저 목록 조회
    @Override
    public List<User> findMembersByChannelId(UUID channelId) {
        Channel channel = findById(channelId);
        return channel.getMembers();
    }

    // 특정 채널의 메시지 목록 조회
    @Override
    public List<Message> findMessagesByChannelId(UUID channelId){
        Channel channel = findById(channelId);
        return channel.getMessages();
    }
}
