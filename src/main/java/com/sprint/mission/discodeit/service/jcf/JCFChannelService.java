//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class JCFChannelService implements ChannelService {
//    private final UserService userService;
//    private MessageService messageService;
//    private final List<Channel> channels = new ArrayList<>();
//
//    // 채널 참여, 퇴장을 위해 userService 참조
//    public JCFChannelService(UserService userService) {
//        this.userService = userService;
//    }
//
//    public void setMessageService(MessageService messageService) {
//        this.messageService = messageService;
//    }
//
//    @Override
//    public Channel create(String channelName) {
//        Channel newChannel = new Channel(channelName);
//        channels.add(newChannel);
//        return newChannel;
//    }
//
//    @Override
//    public Channel findById(UUID uuid) {
//        return channels.stream()
//                .filter(c -> c.getId().equals(uuid))
//                .findFirst()
//                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다. ID: "  + uuid));
//    }
//
//    @Override
//    public List<Channel> findAll() {
//        return new ArrayList<>(channels);
//    }
//
//    @Override
//    public List<Channel> findChannelsByUserId(UUID userId) {
//        User user = userService.findById(userId);
//
//        return user.getMyChannels();
//    }
//
//    @Override
//    public Channel update(UUID id, String updateChannelName) {
//        Channel updateChannel = findById(id);
//        updateChannel.updateChannel(updateChannelName);
//        return updateChannel;
//    }
//
//    public void addChannelByUserId(UUID channelId, UUID userId) {
//        Channel channel = findById(channelId);
//        User user = userService.findById(userId);
//        channel.addMember(user);
//        user.addMyChannel(channel);
//    }
//
//    @Override
//    public void removeChannelByUserId(UUID channelId, UUID userId) {
//        Channel channel = findById(channelId);
//        User user = userService.findById(userId);
//        channel.removeMember(user);
//        user.removeMyChannel(channel);
//    }
//
//    @Override
//    public void deleteChannelByChannelId(UUID channelId) {
//        Channel channel = findById(channelId);
//        messageService.deleteMessagesByChannelId(channelId);
//        new ArrayList<>(channel.getMembers()).forEach(user -> {
//            user.removeMyChannel(channel);
//        });
//        channels.remove(channel);
//    }
//}
