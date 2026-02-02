//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import java.util.*;
//
//public class JCFChannelService implements ChannelService {
//    private final Map<UUID, Channel> data;
//
//    // 생성자
//    public JCFChannelService() {
//        this.data = new HashMap<>();
//    }
////    public JCFChannelService(JCFUserService userService) {
////        this.data = new HashMap<>();
////        this.userService = userService;
////    }
//
//    @Override
//    public Channel create(String channelName) {
//        Channel channel = new Channel(channelName);
//        this.data.put(channel.getId(), channel);
//        return channel;
//    }
//
//    @Override
//    public Channel findById(UUID id) {
//        Channel channel = Optional.ofNullable(data.get(id)).orElseThrow(() -> new NoSuchElementException());
//        return channel;
//    }
//
//    @Override
//    public List<Channel> findAll() {
//        return new ArrayList<>(this.data.values());
//    }
//
//    @Override
//    public Channel updateChannelname(UUID id, String name) {
//        this.findById(id).updateChannelName(name);
//        return this.findById(id);
//    }
//
//    @Override
//    public void delete(UUID id) {
//        this.findById(id);
//        this.data.remove(id);
//    }
//
//    public void joinUser(UUID userId, UUID channelId, JCFUserService userService) {
//        User user = userService.findById(userId);
//        Channel channel = data.get(channelId);
//        user.getChannelList().add(channel);
//        channel.getUserList().add(user);
//    }
//
//    public void quitUser(UUID userId, UUID channelId, JCFUserService userService) {
//        User user = userService.findById(userId);
//        Channel channel = data.get(channelId);
//        user.getChannelList().remove(channel);
//        channel.getUserList().remove(user);
//    }
//
//    // 특정 유저가 참가한 채널 리스트 조회
//    public List<Channel> readUserChannelList(UUID userId, JCFUserService userService) {
//        User user = userService.findById(userId);
//        return user.getChannelList();
//    }
//}