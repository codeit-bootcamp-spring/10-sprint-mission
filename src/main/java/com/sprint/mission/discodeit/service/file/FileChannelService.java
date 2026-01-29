//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//import com.sprint.mission.discodeit.service.MessageService;
//import com.sprint.mission.discodeit.service.UserService;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//public class FileChannelService implements ChannelService {
//    private final UserService userService;
//    private MessageService messageService;
//
//    private static final String FILE_PATH = "channels.dat";
//    private final List<Channel> channels;
//
//    public FileChannelService(UserService userService) {
//        this.userService = userService;
//        this.channels = loadFromFile();
//    }
//
//    public void setMessageService(MessageService messageService) {
//        this.messageService = messageService;
//    }
//
//    @Override
//    public void save(Channel channel) {
//        saveToFile();
//    }
//
//    @Override
//    public Channel create(String channelName) {
//        Channel newChannel = new Channel(channelName);
//        channels.add(newChannel);
//        saveToFile();
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
//        return user.getChannels();
//    }
//
//    @Override
//    public Channel update(UUID id, String updateChannelName) {
//        Channel update = findById(id);
//        update.update(updateChannelName);
//        saveToFile();
//        return update;
//    }
//
//    public void addChannelByUserId(UUID channelId, UUID userId) {
//        Channel channel = findById(channelId);
//        User user = userService.findById(userId);
//        channel.addMember(user);
//        user.addChannel(channel);
//        saveToFile();
//    }
//
//    @Override
//    public void removeChannelByUserId(UUID channelId, UUID userId) {
//        Channel channel = findById(channelId);
//        User user = userService.findById(userId);
//        channel.removeMember(user);
//        user.removeChannel(channel);
//        saveToFile();
//    }
//
//    @Override
//    public void deleteChannelByChannelId(UUID channelId) {
//        Channel channel = findById(channelId);
//        messageService.deleteMessagesByChannelId(channelId);
//        new ArrayList<>(channel.getMembers()).forEach(user -> {
//            user.removeChannel(channel);
//        });
//        channels.remove(channel);
//        saveToFile();
//    }
//
//    // 직렬화
//    private void saveToFile() {
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_PATH))) {
//            oos.writeObject(channels);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 역직렬화
//    @SuppressWarnings("unchecked")
//    private List<Channel> loadFromFile() {
//        File file = new File(FILE_PATH);
//        if (!file.exists()) {
//            return new ArrayList<>();
//        }
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
//            return (List<Channel>) ois.readObject();
//        } catch (IOException | ClassNotFoundException e) {
//            return new ArrayList<>();
//        }
//    }
//}
