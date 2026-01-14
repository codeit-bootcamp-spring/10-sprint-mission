package org.example.service.jcf;

import org.example.entity.Channel;
import org.example.entity.ChannelType;
import org.example.entity.User;
import org.example.service.ChannelService;
import org.example.service.UserService;

import java.util.*;

public class JCFChannelService implements ChannelService {

    private final Map<UUID,Channel> data;
    private final UserService userService;

    public JCFChannelService(UserService userService){
        data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel create(String name, String description,ChannelType type, UUID ownerId) {
        // 입력값 검증 추가
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("필드: name, 조건: null이 아니고 빈 값이 아님, 값: " + name);
        }
        User owner =userService.findById(ownerId);
        Channel channel = new Channel(name, description, type, owner);
        data.put(channel.getId(),channel);
        owner.getChannels().add(channel);  // 👈 추가 필요. 이것도 양방향 동시성? ㅇㅇ 여기서 owner라고 하는게 좋나? 아니면 user라고 하는게 좋나??
        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        return Optional.ofNullable(data.get(channelId))
                .orElseThrow(()->new NoSuchElementException("필드: id, 조건: 존재하는 채널, 값: " + channelId));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID channelId, String name, String description, ChannelType type) {
        Channel channel = findById(channelId);

        // null이 아닌 값만 업데이트
        Optional.ofNullable(name).ifPresent(channel::setName);
        Optional.ofNullable(description).ifPresent(channel::setDescription);
        Optional.ofNullable(type).ifPresent(channel::setType);

        return channel;
    }

    /*@Override
    public Channel update(UUID channelId, String name, String description, ChannelType type) {
        Channel channel = findById(channelId);
        channel.setName(name);
        channel.setDescription(description);
        channel.setType(type);
        return channel;
    }*/

    @Override
    public void transferOwnership(UUID channelId, UUID newOwnerId) {
        Channel channel = findById(channelId);
        User newOwner = userService.findById(newOwnerId);

        // 채널 멤버 검증
        if (!channel.getMembers().contains(newOwner)) {
            throw new IllegalArgumentException("필드: newOwnerId, 조건: 채널 멤버여야 함, 값: " + newOwnerId);
        }
        channel.setOwner(newOwner);
    }

    @Override
    public void delete(UUID channelId) {
        Channel  channel = findById(channelId);

        for(User member: channel.getMembers()){
            member.getChannels().remove(channel);
        }

        data.remove(channelId);
    }

    @Override
    public void addMember(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);

        // 중복 멤버 검증 추가
        if (channel.getMembers().contains(user)) {
            throw new IllegalArgumentException("필드: userId, 조건: 채널에 없는 유저, 값: " + userId);
        }

        channel.getMembers().add(user);
        user.getChannels().add(channel);
    }

    @Override
    public void removeMember(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);

        // 오너는 나갈 수 없음 검증 추가
        if (channel.getOwner().getId().equals(userId)) {
            throw new IllegalArgumentException("필드: userId, 조건: 채널 오너가 아님, 값: " + userId);
        }

        channel.getMembers().remove(user);
        user.getChannels().remove(channel);


    }
}
