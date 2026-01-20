package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.RoleService;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private static JCFChannelService instance = null;
    private JCFChannelService(){}
    public static JCFChannelService getInstance(){
        if(instance == null){
            instance = new JCFChannelService();
        }
        return instance;
    }

    Set<Channel> channels = new HashSet<>();

    @Override
    public Channel find(UUID id) {
        return channels.stream()
                .filter(channel -> id.equals(channel.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Channel not found: id = " + id));
    }

    @Override
    public Set<Channel> findAll() {
        Set<Channel> newChannels = new HashSet<>();
        newChannels.addAll(channels);
        return newChannels;
    }

    @Override
    public Channel create(String channelName, String channelDescription) {
        Channel channel = new Channel(channelName, channelDescription);
        channels.add(channel);
        return channel;
    }

    @Override
    public void delete(UUID channelID, UUID userID) {
        Channel channel = find(channelID);
        User user = JCFUserService.getInstance().find(userID);
        boolean isADMIN = channel.getRoles().stream()
                        .anyMatch(R-> R.getRoleName().equals(PermissionLevel.ADMIN)
                                && R.getUsers().equals(user));
        if(isADMIN){
            channels.remove(channel);
        }
        else{
            throw new RuntimeException("User not allowed to delete channel");
        }
    }

    @Override
    public Channel update(UUID id, String name, String desc) {
        Channel willUpdate = this.find(id);
        Optional.ofNullable(name)
                .ifPresent(willUpdate::updateChannelName);
        Optional.ofNullable(desc)
                .ifPresent(willUpdate::updateChannelDescription);
        return willUpdate;
    }

    @Override
    public Channel update(Channel channel) {
        return null;
    }

    public void printChannel(UUID id){
        Channel PrintingChannel = this.find(id);
        PrintingChannel.printChannel();
    }

    public void updateUserRole(UUID channelID, UUID willChangeUserID, PermissionLevel roleName, UUID tryingUserID){
        Channel channel = this.find(channelID);
        User willChangeUser = JCFUserService.getInstance().find(willChangeUserID);
        User tryingUser = JCFUserService.getInstance().find(tryingUserID);

        Role role = channel.getRoles().stream()
                .filter(R-> R.getUsers().equals(willChangeUser))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found in channel"));

        boolean isAdmin = channel.getRoles().stream()
                .anyMatch(R-> R.getUsers().equals(tryingUser) && R.getRoleName().equals(PermissionLevel.ADMIN));

        if(isAdmin){
            JCFRoleService.getInstance().update(role.getId(), roleName);
        }
        else{
            throw new RuntimeException("User not allowed to change role");
        }

    }

    public Message addMessage(UUID channelID, UUID userID, String msg) {
        Message sendMessage = JCFMessageService.getInstance().create(userID, msg, channelID);
        User user = JCFUserService.getInstance().find(userID);
        Channel channel = this.find(channelID);

        boolean isAllowedUser = user.getRoles()
                .stream()
                .anyMatch(g->g.getChannel().equals(channel));

        if(isAllowedUser){
            channel.getMessages().add(sendMessage);
        }
        else{
            throw new RuntimeException("User not allowed to send message in this channel");
        }
        return sendMessage;
    }

}