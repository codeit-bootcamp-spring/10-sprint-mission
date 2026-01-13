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

    JCFChannelService(UserService userService){
        data = new HashMap<>();
        this.userService = userService;
    }

    @Override
    public Channel create(String name, String description,ChannelType type, UUID ownerId) {
        User owner =userService.findById(ownerId);
        Channel channel = new Channel(name, description, type, owner);
        data.put(channel.getId(),channel);

        owner.getChannels().add(channel);  // 👈 추가 필요. 이것도 양방향 동시성? ㅇㅇ 여기서 owner라고 하는게 좋나? 아니면 user라고 하는게 좋나??
        return channel;
    }

    @Override
    public Channel findById(UUID id) {
        return data.get(id);
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(data.values());
    }

    @Override
    public Channel update(UUID id, String name, String description, ChannelType type) {
        Channel channel = findById(id);
        channel.setName(name);
        channel.setDescription(description);
        channel.setType(type);
        return channel;
    }

    @Override
    public void transferOwnership(UUID channelId, UUID newOwnerId) {
        Channel channel = findById(channelId);
        User newOwner = userService.findById(newOwnerId);
        channel.setOwner(newOwner);
        // 새 오너를 선택하기 위해 이전 작업에 톡방에 참여한 인원의 리스트 생성
    }

    @Override
    public void delete(UUID id) {
        Channel  channel = findById(id);

        for(User member: channel.getMembers()){
            member.getChannels().remove(channel); // ?? 뭔데이거
        }

        data.remove(id);
    }

    @Override
    public void addMember(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        channel.getMembers().add(user);

        user.getChannels().add(channel);
    }

    @Override
    public void removeMember(UUID channelId, UUID userId) {
        Channel channel = findById(channelId);
        User user = userService.findById(userId);
        channel.getMembers().remove(user);
        //양방향 동시성 문제
        user.getChannels().remove(channel);

    }
}
