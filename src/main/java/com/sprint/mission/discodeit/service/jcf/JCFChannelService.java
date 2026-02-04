//package com.sprint.mission.discodeit.service.jcf;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.IsPrivate;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//
//public class JCFChannelService implements ChannelService {
//    private final UserService userService;
//    private final ChannelRepository channelRepository;
//    private final MessageRepository messageRepository;
//
//    public JCFChannelService(UserService userService, ChannelRepository channelRepository, MessageRepository messageRepository) {
//        this.userService = userService;
//        this.channelRepository = channelRepository;
//        this.messageRepository = messageRepository;
//    }
//
//    @Override
//    public Channel create(String name, IsPrivate isPrivate, UUID ownerId) {
//        User owner = userService.findById(ownerId);
//        Channel channel = new Channel(name, isPrivate, owner);
//        channel.addUser(owner); // 나도 멤버에 넣어주기
//        return channelRepository.save(channel);
//    }
//
//    @Override
//    public Channel findById(UUID id) {
//        Channel channel = channelRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("실패 : 존재하지 않는 채널 ID입니다."));
//
//        return channel;
//    }
//
//    @Override
//    public List<Channel> readAll() {
//        return channelRepository.readAll();
//    }
//
//    @Override
//    public Channel update(UUID id, String name, IsPrivate isPrivate, User owner) {
//        Channel channel = findById(id);
//        channel.updateName(name);
//        channel.updatePrivate(isPrivate);
//        channel.updateOwner(owner);
//        return channelRepository.save(channel);
//    }
//
//    public void joinChannel(UUID userId, UUID channelId) {
//        Channel channel = findById(channelId);
//        User user = userService.findById(userId);
//        channel.addUser(user);
//        user.getChannels().add(channel); // 양방향 연결
//    }
//
//    @Override
//    public void delete(UUID id) {
//        findById(id);
//        // 채널의 메시지 삭제하기
//        List<Message> remainMessages = messageRepository.readAll().stream()
//                .filter(msg -> !msg.getChannel().getId().equals(id))
//                .toList();
//
//        messageRepository.saveAll(remainMessages);
//        channelRepository.delete(id);
//    }
//
//    // 채널에서 주고받은 메시지 출력
//    public List<Message> getChannelMessages(UUID channelId) {
//        Channel channel = findById(channelId);
//        return messageRepository.readAll().stream()
//                .filter(msg -> msg.getChannelId().equals(channelId))
//                .toList();
////        return channel.getMessages();
//    }
//
//    @Override
//    public List<User> getChannelUsers(UUID channelId) {
//        Channel channel = findById(channelId);
//        return channel.getUsers();
//    }
//
//}
