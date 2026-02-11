//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.*;
//import com.sprint.mission.discodeit.repository.ChannelRepository;
//import com.sprint.mission.discodeit.repository.MessageRepository;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.ClearMemory;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class FileMessageService implements MessageService, ClearMemory {
//
//    private final UserService userService;
//    private final ChannelService channelService;
//    private final MessageRepository messageRepository;
//    private final ChannelRepository channelRepository;
//
//    // path : 파일이름 - 메인에서 저장 위치 지정
//    public FileMessageService(UserService userService, ChannelService channelService, ChannelRepository channelRepository, MessageRepository messageRepository) {
//        this.userService = userService;
//        this.channelService = channelService;
//        this.messageRepository = messageRepository;
//        this.channelRepository = channelRepository;
//    }
//
//    @Override
//    public Message create(UUID userId, UUID channelId, String content) {
//        User user = userService.findById(userId);
//        Channel channel = channelService.findById(channelId);
//        Message message = new Message(user, channel, content);
//        channel.addMessage(message);    // 채널에 메시지 추가
//        channelRepository.save(channel);
//        return messageRepository.save(message);
//    }
//
//    @Override
//    public Message findById(UUID id) {
//        Message message = messageRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 메시지 ID입니다."));
//
//        return message;
//    }
//
//    @Override
//    public List<Message> readAll() {
//        return messageRepository.readAll();
//    }
//
//    @Override
//    public Message update(UUID id, String newContent) {
//        Message message = findById(id);
//        message.updateContent(newContent);
//        messageRepository.save(message);
//        return message;
//    }
//
//    @Override
//    public List<Message> searchMessage(UUID channelId, String searchContent) {
//        Channel channel = channelService.findById(channelId);
//        return readAll().stream()
//                .filter(msg -> msg.getChannelId().equals(channelId))
//                .filter(msg -> msg.getContent().contains(searchContent))
//                .toList();
//    }
//
//    @Override
//    public UUID sendDirectMessage(UUID senderId, UUID receiverId, String content) {
//        Channel dmChannel = getOrCreateDMChannel(senderId, receiverId);
//
//        User sender = userService.findById(senderId);
//        Message message = new Message(sender, dmChannel, content);
//
//        dmChannel.addMessage(message);
//        messageRepository.save(message);
//        return dmChannel.getId();
//    }
//
//    private Channel getOrCreateDMChannel(UUID user1Id, UUID user2Id) {
//        User user1 = userService.findById(user1Id);
//        User user2 = userService.findById(user2Id);
//
//        return channelService.readAll().stream()
//                .filter(c -> c.getIsPrivate() == IsPrivate.PRIVATE)
//                .filter(c -> c.getUsers().size() == 2)
//                .filter(c -> c.getUsers().stream().anyMatch(u -> u.getId().equals(user2Id)))
//                .findFirst()
//                .orElseGet(() -> {
//                    Channel newDmChannel = channelService.create("DM - " + user1.getName() + "-" + user2.getName(), IsPrivate.PRIVATE, user1.getId());
//                    newDmChannel.addUser(user2);
//                    channelRepository.save(newDmChannel);
//                    return newDmChannel;
//                });
//    }
//
//
//    @Override
//    public void delete(UUID id) {
//        messageRepository.delete(id);
//    }
//
//    @Override
//    public void clear() {
//        messageRepository.clear();
//    }
//
//}
