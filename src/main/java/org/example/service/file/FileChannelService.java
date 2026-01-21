package org.example.service.file;

import org.example.entity.Channel;
import org.example.entity.ChannelType;
import org.example.entity.User;
import org.example.exception.NotFoundException;
import org.example.service.ChannelService;
import org.example.service.MessageService;
import org.example.service.UserService;
import org.example.exception.InvalidRequestException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileChannelService implements ChannelService {

    private static final Path FILE_PATH = Paths.get("data", "channels.ser");
    private final UserService userService;
    private MessageService messageService;

    public FileChannelService(UserService userService) {
        this.userService = userService;
        initializeFile();
    }

    public void setMessageService(MessageService messageService) {
        this.messageService = messageService;
    }

    private void initializeFile(){
        try{
            if(Files.notExists(FILE_PATH.getParent())){
                Files.createDirectories(FILE_PATH.getParent());
            }
            if(Files.notExists(FILE_PATH)){
                saveToFile(new HashMap<>());
            }
        }catch(IOException e){
            throw new RuntimeException("파일 초기화 실패",e);
        }
    }

    @SuppressWarnings("unchecked")
    private Map<UUID,Channel> loadFromFile(){
        if(Files.notExists(FILE_PATH)){
            return new HashMap<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(FILE_PATH))){ //역직렬화
            return (Map<UUID, Channel>) ois.readObject();
        } catch(IOException | ClassNotFoundException e){
            throw new RuntimeException("채널 데이터 로드 실패",e);
        }
    }
    //자동으로 스트림 닫힘? io와의 차이점??
    private void saveToFile(Map<UUID,Channel> data){
        try(ObjectOutputStream oos = new ObjectOutputStream(
                Files.newOutputStream(FILE_PATH))){
            oos.writeObject(data); //직렬화
        } catch(IOException e){
            throw new RuntimeException("채널 데이터 저장 실패",e);
        }
    }



    @Override
    public Channel create(String name, String description, ChannelType type, UUID ownerId) {
        if (name == null || name.isBlank()) {
            throw new InvalidRequestException("name", "null이 아니고 빈 값이 아님", name);
        }
        User owner =userService.findById(ownerId);
        Channel channel = new Channel(name, description, type, owner);

        Map<UUID, Channel> data = loadFromFile();
        data.put(channel.getId(), channel);
        saveToFile(data); //data.put(channel.getId(),channel); 차이점 알아보기

        owner.getChannels().add(channel);
        //여기 user저장 안됨???
        return channel;
    }

    @Override
    public Channel findById(UUID channelId) {
        Map<UUID, Channel> data = loadFromFile();
        return Optional.ofNullable(data.get(channelId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 채널", channelId));
    }

    @Override
    public List<Channel> findAll() {
        return new ArrayList<>(loadFromFile().values());
    }

    @Override
    public Channel update(UUID channelId, String name, String description, ChannelType type) {
        Map<UUID, Channel> data = loadFromFile(); //업데이트가 많이 다름. 확인할것

        Channel channel = Optional.ofNullable(data.get(channelId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 채널", channelId));

        Optional.ofNullable(name).ifPresent(channel::updateName);
        Optional.ofNullable(description).ifPresent(channel::updateDescription);
        Optional.ofNullable(type).ifPresent(channel::updateType);

        saveToFile(data);

        return channel;
    }



    @Override
    public void delete(UUID channelId) {
        Map<UUID, Channel> data = loadFromFile();

        Channel channel = Optional.ofNullable(data.get(channelId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 채널", channelId));

        channel.getMembers().forEach(member -> member.getChannels().remove(channel));

        new ArrayList<>(channel.getMessages()).forEach(message ->
                messageService.hardDelete(message.getId())
        );

        data.remove(channelId);
        saveToFile(data);
    }

    @Override
    public void addMember(UUID channelId, UUID userId) {
        Map<UUID, Channel> data = loadFromFile();

        Channel channel = Optional.ofNullable(data.get(channelId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 채널", channelId));
        User user = userService.findById(userId);

        if (channel.getMembers().contains(user)) {
            throw new InvalidRequestException("userId", "채널에 없는 유저", userId);
        }

        channel.addMember(user);

        saveToFile(data);
    }

    @Override
    public void removeMember(UUID channelId, UUID userId) {
        Map<UUID, Channel> data = loadFromFile();

        Channel channel = Optional.ofNullable(data.get(channelId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 채널", channelId));
        User user = userService.findById(userId);

        if (channel.getOwner().getId().equals(userId)) {
            throw new InvalidRequestException("userId", "채널 오너가 아님", userId);
        }

        channel.removeMember(user);

        saveToFile(data);
    }
    @Override
    public void transferOwnership(UUID channelId, UUID newOwnerId) {
        Map<UUID, Channel> data = loadFromFile();

        Channel channel = Optional.ofNullable(data.get(channelId))
                .orElseThrow(() -> new NotFoundException("id", "존재하는 채널", channelId));
        User newOwner = userService.findById(newOwnerId);

        if (!channel.getMembers().contains(newOwner)) {
            throw new InvalidRequestException("newOwnerId", "채널 멤버여야 함", newOwnerId);
        }

        channel.updateOwner(newOwner);

        saveToFile(data);
    }
    @Override
    public List<User> findMembersByChannel(UUID channelId) {
        Channel channel = findById(channelId);
        return channel.getMembers();
    }
}
