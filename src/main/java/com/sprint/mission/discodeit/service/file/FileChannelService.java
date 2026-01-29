//package com.sprint.mission.discodeit.service.file;
//
//import com.sprint.mission.discodeit.entity.Channel;
//import com.sprint.mission.discodeit.entity.User;
//import com.sprint.mission.discodeit.service.ChannelService;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.NoSuchElementException;
//import java.util.UUID;
//
//public class FileChannelService implements ChannelService {
//    private List<Channel> data;
//
//    public FileChannelService() {
//        this.data = new ArrayList<>();
//    }
////    public FileChannelService(FileUserService userService) {
////        this.data = new ArrayList<>();
////        this.userService = userService;
////    }
//
//    // 직렬화
//    public void serialize(List<Channel> channels) {
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("channels.ser"))) {
//            oos.writeObject(channels);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    // 역직렬화
//    public List<Channel> deserialize() {
//        List<Channel> newChannels = List.of();
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("channels.ser"))) {
//            newChannels = (List<Channel>) ois.readObject();
////            System.out.println(newChannels);
//        } catch (IOException | ClassNotFoundException e) {
//            System.out.println("역직렬화가 안됨");
//        }
//        return newChannels;
//    }
//
//    @Override
//    public Channel create(String channelName) {
//        Channel channel = new Channel();
//        this.data.add(channel);
//        serialize(data);
//        return channel;
//    }
//
//    @Override
//    public Channel read(UUID id) {
//        for (Channel channel : deserialize()) {
//            if (channel.getId().equals(id)) {
//                return channel;
//            }
//        }
//        throw new NoSuchElementException();
//    }
//
//    @Override
//    public List<Channel> readAll() {
//        return deserialize();
//    }
//
//    @Override
//    public Channel updateChannelname(UUID id, String name) {
//        this.data = deserialize();
//        for (Channel channel : this.data) {
//            if (channel.getId().equals(id)) {
//                channel.updateChannelName(name);
//                serialize(this.data);
//                return channel;
//            }
//        }
//        throw new NoSuchElementException();
//    }
//
//    @Override
//    public void delete(UUID id) {
//        this.data = deserialize();
//        for (Channel channel : this.data) {
//            if (channel.getId().equals(id)) {
//                this.data.remove(channel);
//                serialize(this.data);
//                break;
//            }
//        }
//    }
//
//    // 유저 참가
//    public void joinUser(UUID userId, UUID channelId, FileUserService userService) {
//        User user = userService.find(userId);
//        Channel channel;
//        try {
//            channel = this.read(channelId);
//            user.getChannelList().add(channel);
//            channel.getUserList().add(user);
//        } catch (NoSuchElementException e) {
//            System.out.println("채널 검색 실패");
//        }
//    }
//
//    // 유저 탈퇴
//    public void quitUser(UUID userId, UUID channelId, FileUserService userService) {
//        User user = userService.find(userId);
//        try {
//            Channel channel = this.read(channelId);
//            user.getChannelList().remove(channel);
//            channel.getUserList().remove(user);
//        } catch (NoSuchElementException e) {
//            System.out.println("채널 검색 실패");
//        }
//
//    }
//
//    // 특정 유저가 참가한 채널 리스트 조회
//    public List<Channel> readUserChannelList(UUID userId, FileUserService userService) {
//        User user = userService.find(userId);
//        return user.getChannelList();
//    }
//}
