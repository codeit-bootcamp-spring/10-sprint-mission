package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {
    private final String FILE_PATH = "./channels.ser";
    private final FileUserService fileUserService = new FileUserService();

    // 파일 쓰기 (직렬화)
    private void saveData(Map<UUID, Channel> data){
        try (FileOutputStream fos = new FileOutputStream(FILE_PATH);
             ObjectOutputStream oos = new ObjectOutputStream(fos)){

            oos.writeObject(data);

        } catch (IOException e){
            System.err.println("[파일 저장 실패]: " + e.getMessage());
        }
    }

    // 파일 읽기 (역직렬화)
    private Map<UUID, Channel> loadData(){
        File file = new File(FILE_PATH);
        if (!file.exists()) return new HashMap<>();

        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)){

            return (Map<UUID, Channel>) ois.readObject();

        } catch (IOException | ClassNotFoundException e){
            return new HashMap<>();
        }
    }


    // 채널 생성
    @Override
    public Channel create(String name, String description, String type, boolean isPublic) {
        Map<UUID, Channel> channelData = loadData();

        Channel newChannel = new Channel(name, description, type, isPublic);
        channelData.put(newChannel.getId(), newChannel);

        saveData(channelData);
        return newChannel;
    }

    // 채널 ID로 조회
    @Override
    public Channel findById(UUID id){
        Map<UUID, Channel> channelData = loadData();

        Channel channel = channelData.get(id);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 채널 ID입니다.");
        }
        return channel;
    }

    // 채널 전부 조회
    @Override
    public List<Channel> findAll(){
        Map<UUID, Channel> channelData = loadData();
        return new ArrayList<>(channelData.values());
    }

    // 채널 정보 수정
    @Override
    public Channel update(UUID id, String name, String description, boolean isPublic){
        Map<UUID, Channel> channelData = loadData();

        Channel channel = channelData.get(id);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 채널 ID입니다.");
        }

        channel.update(name, description, isPublic);
        saveData(channelData);
        return channel;
    }

    // 채널 삭제
    @Override
    public void delete(UUID id){
        Map<UUID, Channel> channelData = loadData();

        Channel channel = channelData.get(id);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 채널 ID입니다.");
        }

        channelData.remove(id);
        saveData(channelData);
    }

    // 채널 참가
    @Override
    public void join(UUID channelId, UUID userId) {
        User user = fileUserService.findById(userId);

        Map<UUID, Channel> channelData = loadData();
        Channel channel = channelData.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 채널 ID입니다.");
        }

        if (channel.isMember(user)) {
            throw new IllegalStateException("이미 채널에 가입된 유저입니다.");
        }

        channel.addMember(user);
        user.addJoinedChannel(channel);

        saveData(channelData);
        fileUserService.update(user);
    }


    // 채널 탈퇴
    @Override
    public void leave(UUID channelId, UUID userId) {
        User user = fileUserService.findById(userId);

        Map<UUID, Channel> channelData = loadData();
        Channel channel = channelData.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 채널 ID입니다.");
        }

        if (!channel.isMember(user)) {
            throw new IllegalStateException("해당 채널의 멤버가 아닙니다.");
        }

        channel.removeMember(user);
        user.leaveChannel(channel);

        saveData(channelData);
        fileUserService.update(user);
    }

    // 특정 채널의 유저 목록 조회
    @Override
    public List<User> findMembersByChannelId(UUID channelId) {
        Map<UUID, Channel> channelData = loadData();

        Channel channel = channelData.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 채널 ID입니다.");
        }

        return channel.getMembers();
    }

    // 특정 채널의 메시지 목록 조회
    @Override
    public List<Message> findMessagesByChannelId(UUID channelId){
        Map<UUID, Channel> channelData = loadData();

        Channel channel = channelData.get(channelId);
        if (channel == null) {
            throw new NoSuchElementException("존재하지 않는 채널 ID입니다.");
        }

        return channel.getMessages();
    }

    // 다른 FileService에서 채널 파일을 사용 수 있도록 하기 위한 메서드
    public void update(Channel channel) {
        Map<UUID, Channel> channelData = loadData();
        channelData.put(channel.getId(), channel);
        saveData(channelData); // 여기서 private인 saveData를 호출함
    }
}
