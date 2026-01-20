package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFRoleService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.io.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class FileChannelService implements ChannelService {
    private static final String FILE_PATH = "channels.dat";

    public FileChannelService() {
        File file = new File(FILE_PATH);
        if (!file.exists() || file.length() == 0) {
            saveToFile(new HashSet<>());
        }
    }

    public FileChannelService(boolean dummy){ //테스트할때 리셋용 더미생성자
        saveToFile(new HashSet<>());
    }

    @Override
    public Channel find(UUID id) {
        Set<Channel> usersInFile = findAll();
        return usersInFile.stream()
                .filter(ch -> ch.getId().equals(id))
                .findFirst()
                .orElseThrow(() ->new RuntimeException("Channel not found: id = " + id));
    }

    @Override
    public Set<Channel> findAll() {
        try (ObjectInputStream fileInput = new ObjectInputStream(new FileInputStream(FILE_PATH))) {
            return (Set<Channel>)fileInput.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Channel create(String channelName, String channelDescription) {
        Channel channel = new Channel(channelName, channelDescription);
        Set<Channel> channelsInFile = findAll();
        channelsInFile.add(channel);
        saveToFile(channelsInFile);

        return channel;
    }

    @Override
    public void delete(UUID channelID, UUID userID) {
        Set<Channel> channelsInFile = findAll();
        Channel channel = channelsInFile.stream()
                .filter(ch -> ch.getId().equals(channelID))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Channel not found: id = " + channelID));

        FileUserService users = new FileUserService();
        User user = users.find(userID);

        boolean isADMIN = channel.getRoles().stream()
                .anyMatch(R-> R.getRoleName().equals(PermissionLevel.ADMIN)
                        && R.getUsers().equals(user));
        if(isADMIN){
            channelsInFile.remove(channel);
            saveToFile(channelsInFile);
        }
        else{
            throw new RuntimeException("User not allowed to delete channel");
        }
    }

    @Override
    public Channel update(UUID id, String name, String desc) {
        Set<Channel> channelsInFile = findAll();
        Channel channel = channelsInFile.stream()
                .filter(ch -> ch.getId().equals(id))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Channel not found: id = " + id));

        channelsInFile.remove(channel);

        Optional.ofNullable(name)
                .ifPresent(channel::updateChannelName);
        Optional.ofNullable(desc)
                .ifPresent(channel::updateChannelDescription);

        channelsInFile.add(channel);
        saveToFile(channelsInFile);
        return channel;
    }

    public Channel update(Channel channel) {
        Set<Channel> channels = findAll();
        channels.removeIf(c -> c.getId().equals(channel.getId()));
        channels.add(channel);
        saveToFile(channels); // 파일에 쓰는 private 메서드
        return channel;
    }

    @Override
    public void printChannel(UUID id) {
        Channel PrintingChannel = this.find(id);
        PrintingChannel.printChannel();
    }

    @Override
    public void updateUserRole(UUID channelID, UUID willChangeUserID, PermissionLevel roleName, UUID tryingUserID) {
        Channel channel = this.find(channelID);

        Role role = channel.getRoles().stream()
                .filter(R-> R.getUsers().getId().equals(willChangeUserID))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found in channel"));

        boolean isAdmin = channel.getRoles().stream()
                .anyMatch(R-> R.getUsers().getId().equals(tryingUserID) && R.getRoleName().equals(PermissionLevel.ADMIN));

        if(isAdmin){
            FileRoleService roles = new FileRoleService();
            roles.update(role.getId(), roleName);
        }
        else{
            throw new RuntimeException("User not allowed to change role");
        }
    }

    @Override
    public Message addMessage(UUID channelID, UUID userID, String msg) {
        Message sendMessage = null;
        FileMessageService messages = new FileMessageService();
        FileUserService users = new FileUserService();
        User user = users.find(userID);
        Channel channel = this.find(channelID);

        boolean isAllowedUser = user.getRoles()
                .stream()
                .anyMatch(g->g.getChannel().getId().equals(channelID));

        if(isAllowedUser){
            sendMessage = messages.create(userID, msg, channelID);
            channel.getMessages().add(sendMessage);
            update(channel);

        }
        else{
            throw new RuntimeException("User not allowed to send message in this channel");
        }
        return sendMessage;
    }

    private void saveToFile(Set<Channel> channels){
        try (ObjectOutputStream fileOutput = new ObjectOutputStream(new FileOutputStream(FILE_PATH))){
            fileOutput.writeObject(channels);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

}
