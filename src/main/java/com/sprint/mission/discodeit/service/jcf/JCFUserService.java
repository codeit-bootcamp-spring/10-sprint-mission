//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//
//public class JCFUserService implements UserService {
//    private final HashMap<UUID, User> data;
//
//    public JCFUserService() {
//        this.data = new HashMap<>();
//    }
////    public JCFUserService(JCFChannelService channelService) {
////        this.data = new HashMap<>();
////        this.channelService = channelService;
////    }
//
//    @Override
//    public User create(String name, String email) {
//        User user = new User(name, email);
//        data.put(user.getId(), user);
//        return user;
//    }
//
//    @Override
//    public User find(UUID id) {
//        User user = Optional.ofNullable(data.get(id)).orElseThrow(() -> new NoSuchElementException());
//        return user;
//    }
//
//    @Override
//    public List<User> findAll() {
//        return new ArrayList<>(this.data.values());
//    }
//
//    @Override
//    public User updateUser(UUID id, String name) {
//        this.find(id).updateName(name);
//        return this.find(id);
//    }
//
//    @Override
//    public void delete(UUID id) {
//        this.find(id);
//        this.data.remove(id);
//    }
//
//    // 특정 채널의 참가한 유저 목록 조회
//    public List<User> readChannelUserList(UUID channelId, JCFChannelService channelService) {
//        Channel channel = channelService.read(channelId);
//        return channel.getUserList();
//    }
//}
