package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService) {
        data = new HashMap<>();
        this.userService = userService;
    }
    @Override
    public Channel addChannel(String name, String description, UUID ownerId, boolean openType) {
        validateChannelName(name);
        Channel channel = new Channel(name, description, userService.getUserById(ownerId), openType);
        data.put(channel.getId(), channel);
        return channel;
    }

    @Override
    public Channel updateChannelName(UUID id, String name, UUID ownerId) {
        Channel channel = getChannelById(id);
        checkChannelOwner(channel, ownerId);
        if(!name.equals(channel.getName())){
            validateChannelName(name);
        }
        channel.setName(name);
        return channel;
    }

    @Override
    public Channel updateChannelDescription(UUID id, String description, UUID ownerId) {
        Channel channel = getChannelById(id);
        checkChannelOwner(channel, ownerId);
        channel.setDescription(description);
        return getChannelById(id);
    }

    @Override
    public Channel addMembers(UUID id, UUID ownerId, List<UUID> memberIds) {
        Channel channel = getChannelById(id);
        checkChannelOwner(channel, ownerId);
        if(channel.isOpenType()){
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
        Channel channel = getChannelById(id);
        checkChannelOwner(channel, ownerId);
        if(channel.isOpenType()){
            throw new IllegalArgumentException("공개 채널에서 멤버를 제거할 수 없음. 채널ID: "+ id);
        }
        if(memberIds.contains(ownerId)){
            throw new IllegalArgumentException("채널 소유자는 제거될 수 없음. 소유자ID: "+ id);
        }
        List<User> members = memberIds.stream()
                .map(uid->{User user =userService.getUserById(uid);
                    checkMember(channel,user);
                    return user;
                })
                .collect(Collectors.toList());
        channel.removeMembers(members);
        return channel;
    }

    @Override
    public Channel getChannelByIdAndMemberId(UUID id, UUID memberId) {
        Channel channel = getChannelById(id);
        User user = userService.getUserById(memberId);
        if(!channel.isOpenType()){
            checkMember(channel, user);
        }
        return channel;
    }

    @Override
    public List<Channel> findAllChannels() {
        return new ArrayList<>(data.values());//방어적 복사
    }

    @Override
    public void deleteChannelById(UUID id, UUID ownerId) {
        Channel channel = getChannelById(id);
        checkChannelOwner(channel, ownerId);
        channel.removeAllMembers();
        //채널 메세지 삭제 로직 필요
        data.remove(id);
    }

    @Override
    public List<User> findAllMembers(UUID id, UUID memberId) {
        Channel channel = getChannelByIdAndMemberId(id, memberId);
        return new ArrayList<>(channel.getMembers());
    }

    private void validateChannelName(String channelName) {
        boolean exists = data.values().stream()
                .anyMatch(c ->c.getName().equals(channelName));
        if(exists){
           throw new IllegalArgumentException("이미 존재하는 채널이름: "+channelName);
        }
    }

    private void validateChannel(UUID channelId) {
        boolean exists = data.containsKey(channelId);
        if(!exists){
            throw new NoSuchElementException("존재하지 않는 채널ID: "+channelId);
        }
    }

    private void checkChannelOwner(Channel channel, UUID ownerId) {
        User owner = userService.getUserById(ownerId); //사용자가 존재
        if(!channel.getOwner().getId().equals(owner.getId())){
            throw new IllegalArgumentException("채널의 소유자가 아님: [채널ID-"+channel.getId()+" 사용자ID-" + ownerId+"]");
        }
    }

    private Channel getChannelById(UUID id) {
        validateChannel(id);
        return data.get(id);
    }

    private void checkMember(Channel channel, User member) {
        boolean check = channel.getMembers()
                .stream()
                .anyMatch(m -> m.getId().equals(member.getId()));
        if(!check){
            throw new IllegalArgumentException("채널의 속한 사용자가 아님: "+member.getId());
        }
    }
}
