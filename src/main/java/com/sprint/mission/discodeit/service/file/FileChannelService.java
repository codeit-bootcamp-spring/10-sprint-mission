//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.IsPrivate;
//import com.sprint.mission.discodeit.entity.Message;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.repository.UserRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.ClearMemory;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class FileChannelService implements ChannelService, ClearMemory {
//
//    private final UserService userService;
//    private final UserRepository userRepository;
//    private final ChannelRepository channelRepository;
//    private final MessageRepository messageRepository;
//
//    public FileChannelService(UserService userService, UserRepository userRepository, ChannelRepository channelRepository, MessageRepository messageRepository) {
//        this.userService = userService;
//        this.userRepository = userRepository;
//        this.channelRepository = channelRepository;
//        this.messageRepository = messageRepository;
//    }
//
//    @Override
//    public Channel create(String name, IsPrivate isPrivate, UUID ownerId) {
//        User user = userService.findById(ownerId);
//        Channel channel = new Channel(name, isPrivate, user);
//        channelRepository.save(channel);
//        return channel;
//    }
//
//    @Override
//    public Channel findById(UUID id) {
//        Channel channel = channelRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("실패 : 존재하지 않는 채널 ID입니다."));
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
//        channelRepository.save(channel);
//        userRepository.save(user);
//    }
//
//    public List<Message> getChannelMessages(UUID channelId) {
//        findById(channelId);
//        return messageRepository.readAll().stream()
//                .filter(msg -> msg.getChannelId().equals(channelId))
//                .sorted(Comparator.comparing(Message::getCreatedAt))
//                .toList();
//
//    }
//
//    @Override
//    public List<User> getChannelUsers(UUID channelId) {
//        findById(channelId);
//        return channelRepository.readAll().stream()
//                .filter(ch -> ch.getId().equals(channelId))
//                .findFirst()
//                .map(Channel::getUsers)
//                .orElse(Collections.emptyList());
//    }
//
//    @Override
//    public void delete(UUID id) {
//        findById(id);
//
//        // 채널의 메시지 삭제하기
//        List<Message> remainMessages = messageRepository.readAll().stream()
//                .filter(msg -> !msg.getChannel().getId().equals(id))
//                .toList();
//
//        messageRepository.saveAll(remainMessages);
//
//        channelRepository.delete(id);
//    }
//
//    @Override
//    public void clear() {
//        channelRepository.clear();
//    }
//}
