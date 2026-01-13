package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.Exception.AlreadyJoinedChannelException;
import com.sprint.mission.discodeit.Exception.SameNameChannelException;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.entity.Channel;
import java.util.*;


public class JCFChannelService implements ChannelService {

    private Map<UUID, Channel> channels =  new LinkedHashMap<>();

    //채널 생성
    @Override
    public Channel channelCreate(String channelName) {
        for(Channel channel : channels.values()){
            if(channel.getChannelName().equals(channelName)){
                throw new SameNameChannelException("이미 있는 채널입니다.");
            }
        }

        Channel channel = new Channel(channelName);
        channels.put(channel.getChannelId(), channel);

        return channel;
    }

    // 채널 단건 조회
    @Override
    public Channel channelFind(UUID channelId) {
        Channel channel = channels.get(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        return channel;
    }
    // 채널 다건 조회
    @Override
    public List<Channel> channelFindAll() {
        return new ArrayList<>(channels.values());
    }

    // 채널 맴버 추가
    @Override
    public Channel channelMemberAdd(UUID channelId, User user) {
        Channel channel = channels.get(channelId);

        //채널 확인
        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        // 맴버 식별자 동일 = 같은사람
        if (channel.hasMember(user.getUserId())) {
            throw new AlreadyJoinedChannelException("이미 채널에 참여한 사용자입니다.");
        }

        channel.addMember(user);
        return channel;
    }
    // 채널 맴버 삭제
    @Override
    public Channel channelMemberRemove(UUID channelId, UUID userId) {
        Channel channel = channels.get(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        if (!channel.hasMember(userId)) {
            throw new IllegalArgumentException("채널에 없는 사용자입니다.");
        }

        channel.removeMember(userId);
        return channel;
    }

    // 채널 삭제
    @Override
    public Channel channelDelete(UUID channelId) {
        Channel channel = channels.remove(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        return channel;
    }

    // 채널명 변경
    @Override
    public Channel channelNameUpdate(UUID channelId, String channelName) {
        Channel channel = channels.get(channelId);

        if (channel == null) {
            throw new IllegalArgumentException("존재하지 않는 채널입니다.");
        }

        if (channel.getChannelName().equals(channelName)) {
            throw new SameNameChannelException("이미 존재하는 이름 입니다.");
        }

        channel.updateChannelName(channelName);
        return channel;
    }




}
