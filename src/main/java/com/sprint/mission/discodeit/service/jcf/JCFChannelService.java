//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.exception.*;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.UserService;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class JCFChannelService implements ChannelService {
//
//    private final Map<UUID, Channel> channels = new LinkedHashMap<>();
//    private final UserService userService;
//
//    //반복되는 예외처리 메서드
//    public Channel getRequiredChannel(UUID channelId) {
//        Channel channel = channels.get(channelId);
//        if (channel == null) {
//            throw new ChannelNotFoundException();
//        }
//        return channel;
//    }
//
//    public JCFChannelService(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Override
//    public Channel createChannel(String channelName) {
//
//        if(channels.values().stream()
//                .anyMatch(channel -> channel.getChannelName().equals(channelName))){
//            throw new DuplicationChannelException();
//        }
//        Channel channel = new Channel(channelName);
//        channels.put(channel.getId(), channel);
//        return channel;
//    }
//
//    @Override
//    public Channel findChannel(UUID channelId) {
//        return getRequiredChannel(channelId);
//    }
//
//    @Override
//    public List<Channel> findAllChannel() {
//        return new ArrayList<>(channels.values());
//    }
//
//    @Override
//    public Channel userAddChannel(UUID channelId, UUID userId) {
//        Channel channel = findChannel(channelId);
//        User user = userService.findUser(userId);
//        if (channel.hasChannelUser(user)) throw new AlreadyJoinedChannelException();
//        channel.addChannelUser(user);
//        return channel;
//    }
//
//    @Override
//    public Channel nameUpdateChannel(UUID channelId, String channelName) {
//        Channel channel = findChannel(channelId);
//        if (channel.getChannelName().equals(channelName)) throw new DuplicationChannelException();
//        channel.updateChannelName(channelName);
//        return channel;
//    }
//
//    @Override
//    public Channel deleteChannel(UUID channelId) {
//        return getRequiredChannel(channelId);
//    }
//    // 특정 사용자의 이용채널 조회
//    @Override
//    public Channel findByUserChannel(UUID userId) {
//        userService.findUser(userId);
//
//        return channels.values().stream()
//                .filter(channel ->
//                        channel.getChannelUser().stream()
//                                .anyMatch(user -> user.getId().equals(userId))
//                )
//                .findFirst()
//                .orElseThrow(ChannelNotFoundException::new);
//    }
//
//    // 특정채널 전체 사용자 조회
//    public String findAllUserInChannel(UUID channelId) {
//        Channel channel = findChannel(channelId);
//
//        if (channel.getChannelUser().isEmpty()) {
//            throw new UserNotInChannelException();
//        }
//
//        return channel.getChannelUser().stream()
//                .map(User::getUserName)
//                .collect(Collectors.joining(", "));
//    }
//}