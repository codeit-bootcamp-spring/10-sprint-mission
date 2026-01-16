//package com.sprint.mission.service.file;
//
//import com.sprint.mission.entity.Channel;
//import com.sprint.mission.entity.Message;
//import com.sprint.mission.entity.User;
//import com.sprint.mission.service.ChannelService;
//import com.sprint.mission.service.MessageService;
//import com.sprint.mission.service.UserService;
//
//import java.nio.file.Path;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//public class FileMessageService extends BaseFileService implements MessageService {
//    private final UserService userService;
//    private final ChannelService channelService;
//
//    public FileMessageService(Path directory, UserService userService, ChannelService channelService) {
//        super(directory);
//        this.userService = userService;
//        this.channelService = channelService;
//    }
//
//    @Override
//    public Message create(UUID userId, UUID channelId, String content) {
//        User user = userService.findById(userId);
//        Channel channel = channelService.findById(channelId);
//        channel.validateMember(user);
//        Message message = new Message(user, channel, content);
//
//        save(getFilePath(message), message);
//
//        return message;
//    }
//
//    @Override
//    public Message findById(UUID id) {
//        Map<UUID, Message> data = loadData();
//        return getMessageOrThrow(data, id);
//    }
//
//    @Override
//    public List<Message> findAll() {
//        return List.copyOf(loadData().values());
//    }
//
//    @Override
//    public Message update(UUID userId, UUID messageId, String content) {
//        User user = userService.findById(userId);
//        Message message = findById(messageId);
//        message.validateMessageOwner(user);
//
//        message.updateContent(content);
//        save(getFilePath(message), message);
//
//        return message;
//    }
//
//    @Override
//    public void deleteById(UUID userId, UUID MessageId) {
//        User user = userService.findById(userId);
//        Message message = findById(MessageId);
//        message.validateMessageOwner(user);
//
//        delete(getFilePath(message));
//    }
//
//    @Override
//    public List<Message> findByChannelId(UUID channelId) {
//        // 전체 메시지를 가져와서 해당 메시지의 채널 id가 맞다면
//        return findAll().stream()
//                .filter(message -> message.getChannel().getId().equals(channelId))
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public List<Message> findByUserIdAndChannelId(UUID userId, UUID channelId) {
//        // 전체 메시지를 가져와서 메시지가 유저id랑 겹치고 channelId랑 겹치면
//        return findAll().stream()
//                .filter(message ->
//                        message.getUser().getId().equals(userId) &&
//                                message.getChannel().getId().equals(channelId))
//                .collect(Collectors.toList());
//    }
//
//    private Path getFilePath(Message message) {
//        return directory.resolve(message.getId().toString().concat(".ser"));
//    }
//
//    private Map<UUID, Message> loadData() {
//        return load(Message::getId);
//    }
//
//    private Message getMessageOrThrow(Map<UUID, Message> data, UUID id) {
//        if (!data.containsKey(id)) {
//            throw new IllegalArgumentException("해당 메시지가 존재하지 않습니다.");
//        }
//        return data.get(id);
//    }
//}
