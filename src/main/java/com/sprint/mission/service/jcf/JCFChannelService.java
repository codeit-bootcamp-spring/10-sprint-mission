//package com.sprint.mission.service.jcf;
//
//import com.sprint.mission.entity.Channel;
//import com.sprint.mission.entity.User;
//import com.sprint.mission.service.ChannelService;
//import com.sprint.mission.service.UserService;
//
//import java.util.*;
//
//public class JCFChannelService implements ChannelService {
//    private final UserService userService;
//    private final Map<UUID, Channel> channels;
//
//    public JCFChannelService(UserService userService) {
//        this.channels = new HashMap<>();
//        this.userService = userService;
//    }
//
//    @Override
//    public Channel create(UUID ownerId, String name) {
//        User owner = userService.findById(ownerId);
//
//        Channel channel = new Channel(owner, name);
//
//        owner.joinChannel(channel);
//
//        channels.put(channel.getId(), channel);
//        return channel;
//    }
//
//    @Override
//    public Channel findById(UUID id) {
//        return getChannelOrThrow(id);
//    }
//
//    @Override
//    public List<Channel> findAll() {
//        // 리스트나 맵, 셋을 넘겨줄 땐 내부 요소 보호를 위해 얕은복사
//        return new ArrayList<>(channels.values());
//    }
//
//    @Override
//    public List<User> findByChannelId(UUID channelId) {
//        Channel channel = findById(channelId);
//        return channel.getUsers();
//    }
//
//    @Override
//    public Channel update(UUID id, String name) {
//        Channel channel = findById(id);
//        validateUpdateChannelNameNotExist(id, name);
//        channel.updateName(name);
//        return channel;
//    }
//
//    @Override
//    public void deleteById(UUID id) {
//        validateChannelExists(id);
//        channels.remove(id);
//    }
//
//    @Override
//    public Channel joinChannel(UUID userId, UUID channelId) {
//        User user = userService.findById(userId);
//        Channel channel = findById(channelId);
//
//        user.joinChannel(channel);
//        channel.joinUser(user);
//
//        return channel;
//    }
//
//    @Override
//    public Channel leaveChannel(UUID userId, UUID channelId) {
//        User user = userService.findById(userId);
//        Channel channel = findById(channelId);
//
//        user.leaveChannel(channel);
//        channel.leaveUser(user);
//
//        return channel;
//    }
//
//    private void validateChannelExists(UUID id) {
//        if (!channels.containsKey(id)) {
//            throw new IllegalArgumentException("채널이 존재하지 않습니다.");
//        }
//    }
//
//    private Channel getChannelOrThrow(UUID id) {
//        validateChannelExists(id);
//        return channels.get(id);
//    }
//
//    private void validateUpdateChannelNameNotExist(UUID id, String name) {
//        String trimmedName = name.trim();
//        boolean exist = channels.values().stream()
//                .anyMatch(channel -> channel.getName().equals(trimmedName) &&
//                        !channel.getId().equals(id));
//
//        if (exist) {
//            throw new IllegalArgumentException("존재하는 채널명입니다. 다른이름을 선택해주세요");
//        }
//    }
//}
