package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserCoordinatorService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JCFUserCoordinatorService implements UserCoordinatorService {
    private final UserService userService;
    private final ChannelService channelService;

    public JCFUserCoordinatorService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public List<Channel> getChannels(UUID id) {
        User user = userService.getUserById(id);
        return new ArrayList<>(user.getChannels());
    }

    @Override
    public List<User> findAllMembers(UUID id, UUID memberId) {
        Channel channel = getChannelByIdAndMemberId(id, memberId);
        return new ArrayList<>(channel.getMembers());
    }

    @Override
    public Channel addMembers(UUID id, UUID ownerId, List<UUID> memberIds) {
        Channel channel = channelService.getChannelById(id);
        User owner = userService.getUserById(ownerId);
        channel.checkChannelOwner(owner);
        if(channel.getChannelType().equals(Channel.PUBLIC_CHANNEL)){
            throw new IllegalArgumentException("공개 채널에 멤버를 추가할 수 없음. 채널ID: "+ id);
        }
        List<User> newMembers= memberIds.stream()
                .map(uid -> userService.getUserById(uid))//추가할 멤버들의 유효성
                .collect(Collectors.toList());
        channel.addMembers(newMembers);
        return channel;
    }
    @Override
    public Channel removeMembers(UUID id, UUID ownerId, List<UUID> memberIds) {
        Channel channel = channelService.getChannelById(id);
        User owner = userService.getUserById(ownerId);
        channel.checkChannelOwner(owner);
        if(channel.getChannelType().equals(Channel.PUBLIC_CHANNEL)){
            throw new IllegalArgumentException("공개 채널에서 멤버를 제거할 수 없음. 채널ID: "+ id);
        }
        if(memberIds.contains(ownerId)){
            throw new IllegalArgumentException("채널 소유자는 제거될 수 없음. 소유자ID: "+ id);
        }
        List<User> members = memberIds.stream()
                .map(uid->{User user =userService.getUserById(uid);
                    channel.checkMember(user);
                    return user;
                })
                .collect(Collectors.toList());
        channel.removeMembers(members);
        return channel;
    }

    @Override
    public Channel removeMember(UUID id, UUID memberId) {
        Channel channel = channelService.getChannelById(id);
        if(channel.getChannelType().equals(Channel.PUBLIC_CHANNEL)){
            throw new IllegalArgumentException("공개 채널에서 나갈 수 없음. 채널ID: "+ id);
        }
        if(memberId.equals(channel.getOwner().getId())){
            throw new IllegalArgumentException("채널 소유자는 채널에서 나갈 수 없음. 소유자ID: "+ id);
        }
        User member =userService.getUserById(memberId);
        channel.checkMember(member);
        channel.removeMember(member);
        return channel;
    }
    @Override
    public Channel getChannelByIdAndMemberId(UUID id, UUID memberId) {
        Channel channel = channelService.getChannelById(id);
        User user = userService.getUserById(memberId);
        if(!channel.getChannelType().equals(Channel.PUBLIC_CHANNEL)){
            channel.checkMember(user);
        }
        return channel;
    }
        /*
    @Override//디엠
    public Channel getOrCreateDirectChannelByChatterIds(UUID senderId, UUID receiverId){
        /*
        1. 유저의 유효성 검사하기
        2. 모든 채널에서
        -> 다이렉트 채널 필터(채널에 둘만 속해있을 수도 있으니까 멤버만 비교하면 안됨)
        -> 멤버가 2인 채널 추출 및 반환(그룹 디엠 대비) 이 메서드는 2인용이니까...
        -> 멤버 비교해서 해당하는 채널 또는 널 반환
         */
    /*
        User sender = userService.getUserById(senderId);
        User receiver = userService.getUserById(receiverId);
        Set<UUID> chatterIdSet = new HashSet<>(Set.of(senderId,receiverId));
        Channel directChannel = data.values()
                                    .stream()
                                    .filter(c->c.getChannelType().equals(Channel.DIRECT_CHANNEL))
                                    .filter(c-> {
                                        Set<UUID> memberIdSet = c.getMembers().stream()
                                                                .map(m->m.getId())
                                                                .collect(Collectors.toSet());
                                        if(memberIdSet.equals(chatterIdSet)) {
                                            return true;
                                        }
                                        return false;
                                    })
                                    .findFirst().orElse(null);
        if(directChannel == null){
            directChannel = createDirectChannel(chatterIdSet);
        }
        return directChannel;
    }
    private Channel createDirectChannel(Set<UUID> chatterIdSet){
        Channel directChannel = new Channel(null,null,null,Channel.DIRECT_CHANNEL);
        for(UUID chatterId : chatterIdSet){
            directChannel.addMember(userService.getUserById(chatterId));
        }
        data.put(directChannel.getId(), directChannel);
        return directChannel;
    }
    */
}
