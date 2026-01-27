package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserCoordinatorService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFUserCoordinatorService implements UserCoordinatorService {
    private final UserService userService;
    private final ChannelService channelService;

    public JCFUserCoordinatorService(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public List<Channel> findChannelsByUserId(UUID id) {
        User user = userService.findUserById(id);
        return new ArrayList<>(user.getChannels());
    }

    @Override
    public List<User> findAllMembersByChannelIdAndMemberId(UUID id, UUID memberId) {
        Channel channel = findAccessibleChannel(id, memberId);
        return new ArrayList<>(channel.getMembers());
    }

    @Override
    public Channel addMembers(UUID id, UUID ownerId, List<UUID> memberIds) {
        Channel channel = channelService.getChannelById(id);
        User owner = userService.findUserById(ownerId);
        channel.checkChannelOwner(owner);//dm 걸러짐
        if(channel.getChannelType()==ChannelType.PUBLIC){
            throw new IllegalArgumentException("공개 채널에 멤버를 추가할 수 없음. 채널ID: "+ id);
        }
        List<User> newMembers= memberIds.stream()
                .map(uid -> userService.findUserById(uid))//추가할 멤버들의 유효성
                .collect(Collectors.toList());
        channel.addMembers(newMembers);
        return channel;
    }
    @Override
    public Channel removeMembers(UUID id, UUID ownerId, List<UUID> memberIds) {
        Channel channel = channelService.getChannelById(id);
        User owner = userService.findUserById(ownerId);//dm걸러짐
        channel.checkChannelOwner(owner);
        if(channel.getChannelType()==ChannelType.PUBLIC){
            throw new IllegalArgumentException("공개 채널에서 멤버를 제거할 수 없음. 채널ID: "+ id);
        }
        if(memberIds.contains(ownerId)){
            throw new IllegalArgumentException("채널 소유자는 제거될 수 없음. 소유자ID: "+ id);
        }
        List<User> members = memberIds.stream()
                .map(uid->{User user =userService.findUserById(uid);
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
        if(channel.getChannelType()==ChannelType.PUBLIC||channel.getChannelType()==ChannelType.DIRECT){
            throw new IllegalArgumentException(channel.getChannelType().getValue()+" 채널에서 나갈 수 없음. 채널ID: "+ id);
        }
        if(memberId.equals(channel.getOwner().getId())){
            throw new IllegalArgumentException("채널 소유자는 채널에서 나갈 수 없음. 소유자ID: "+ id);
        }
        User member =userService.findUserById(memberId);
        channel.checkMember(member);
        channel.removeMember(member);
        return channel;
    }
    @Override
    public Channel findAccessibleChannel(UUID id, UUID memberId) {
        Channel channel = channelService.getChannelById(id);
        User user = userService.findUserById(memberId);
        if(channel.getChannelType()!=ChannelType.PUBLIC){
            channel.checkMember(user);
        }
        return channel;
    }

    @Override//디엠
    public Channel findOrCreateDirectChannelByChatterIds(UUID senderId, UUID receiverId){
        /*
        1. 유저의 유효성 검사하기
        2. 모든 채널에서
        -> 다이렉트 채널 필터(채널에 둘만 속해있을 수도 있으니까 멤버만 비교하면 안됨)
        -> 멤버가 2인 채널 추출 및 반환(그룹 디엠 대비) 이 메서드는 2인용이니까...
        -> 멤버 비교해서 해당하는 채널 또는 널 반환
         */
        User sender = userService.findUserById(senderId);//이미 삭제된 사용자와의 대화도 조회가 가능하도록
        Set<UUID> chatterIdSet = new HashSet<>(Set.of(senderId,receiverId));
        Channel directChannel = channelService.findAllChannels()
                                    .stream()
                                    .filter(c->c.getChannelType()==ChannelType.DIRECT)
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
            User receiver = userService.findUserById(receiverId);//삭제된사용자와의 대화가 아니면 수신지 유효성 확인
            directChannel = channelService.createDirectChannel(chatterIdSet.stream().toList());
        }
        return directChannel;
    }

}
