//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.*;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.ClearMemory;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class FileMessageService extends AbstractFileService implements MessageService, ClearMemory {
//
//    private final UserService userService;
//    private final ChannelService channelService;
//
//    // path : 파일이름 - 메인에서 저장 위치 지정
//    public FileMessageService(String path, UserService userService, ChannelService channelService) {
//        super(path);
//        this.userService = userService;
//        this.channelService = channelService;
//    }
//
//    // 객체를 파일에 저장 - 직렬화
//    private void save(Message message) {
//        Map<UUID, Message> data = load();
//
//        if (data.containsKey(message.getId())) {
//            Message existing = data.get(message.getId());
//            existing.updateContent(message.getContent());
//            existing.updateUpdatedAt(System.currentTimeMillis());
//            data.put(existing.getId(), existing);
//        } else {
//            data.put(message.getId(), message);
//        }
//        writeToFile(data);
//    }
//
//    @Override
//    public Message create(UUID userId, UUID channelId, String content) {
//        User user = userService.findById(userId);
//        Channel channel = channelService.findById(channelId);
//
//        Message message = new Message(user, channel, content);
//        save(message);
//        return message;
//    }
//
//    @Override
//    public Message findById(UUID id) {
//        Map<UUID, Message> data = load();
//        validateExistence(data, id);
//        return data.get(id);
//    }
//
//    @Override
//    public List<Message> readAll() {
//        Map<UUID, Message> data = load();
//        return List.copyOf(data.values());    // 값들만 뽑아서 불변 리스트로 반환, 조회용이라 불변
//    }
//
//    @Override
//    public Message update(UUID id) {
//        Map<UUID, Message> data = load();
//        validateExistence(data, id);
//        Message message = findById(id);
//        save(message);
//        return message;
//    }
//
//    @Override
//    public void searchMessage(UUID channelId, String msg) {
//        Channel channel = channelService.findById(channelId);
//        String result = channel.getMessages().stream()
//                .filter(m -> m.getContent().contains(msg))
//                .map(m -> "- " + m.getContent())
//                .collect(Collectors.joining("\n"));
//        System.out.println("[" + channel.getName() + "] : " + msg);
//
//        if (!result.isEmpty()) {
//            System.out.println(result);
//        } else {
//            System.out.println("찾는 내용이 없습니다.");
//        }
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
//        return dmChannel.getId();
//    }
//
//    private Channel getOrCreateDMChannel(UUID user1Id, UUID user2Id) {
//        User user1 = userService.findById(user1Id);
//        User user2 = userService.findById(user2Id);
//
//        return user1.getChannels().stream()
//                .filter(c -> c.getIsPrivate() == IsPrivate.PRIVATE)
//                .filter(c -> c.getUsers().size() == 2)
//                .filter(c -> c.getUsers().stream().anyMatch(u -> u.equals(user2)))
//                .findFirst()
//                .orElseGet(() -> {
//                    Channel newDmChannel = channelService.create("DM - " + user1.getName() + "-" + user2.getName(), IsPrivate.PRIVATE, user1.getId());
//                    newDmChannel.addUser(user2);
//                    return newDmChannel;
//                });
//    }
//
//
//    @Override
//    public void delete(UUID id) {
//        remove(id);
//    }
//
//    private void remove(UUID id) {
//        Map<UUID, Message> data = load();
//        validateExistence(data, id);
//        data.remove(id);
//        writeToFile(data);
//    }
//
//    @Override
//    public void clear() {
//        writeToFile(new HashMap<UUID, Message>());
//    }
//
//    private void validateExistence(Map<UUID, Message> data, UUID id) {
//        if (!data.containsKey(id)) {
//            throw new NoSuchElementException("실패 : 존재하지 않는 메시지 ID입니다.");
//        }
//    }
//}
