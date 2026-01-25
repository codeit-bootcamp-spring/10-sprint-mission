package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.RoleRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.*;

public class BasicChannelService implements ChannelService {
    private ChannelRepository channelRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private MessageRepository messageRepository;

    public BasicChannelService(
            ChannelRepository channelRepository,
            UserRepository fileUserRepository,
            RoleRepository fileRoleRepository,
            MessageRepository fileMessageRepository
    ) {
        this.channelRepository = channelRepository;
        this.userRepository = fileUserRepository;
        this.roleRepository = fileRoleRepository;
        this.messageRepository = fileMessageRepository;
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
        return channelRepository.fileLoadAll();
    }

    @Override
    public Channel create(String channelName, String channelDescription) {
        Channel channel = new Channel(channelName, channelDescription);
        Set<Channel> channelsInFile = findAll();
        channelsInFile.add(channel);
        channelRepository.fileSave(channelsInFile);
        return channel;
    }

    @Override
    public void delete(UUID channelID, UUID userID) {
        Set<Channel> channelsInFile = findAll();
        Channel channel = channelsInFile.stream()
                .filter(ch -> ch.getId().equals(channelID))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Channel not found: id = " + channelID));

        User user = userRepository.fileLoad(userID);

        boolean isADMIN = channel.getRolesID().stream()
                .map(roleRepository::fileLoad)
                .anyMatch(R -> R.getUserID().equals(userID)
                        && R.getRoleName().equals(PermissionLevel.ADMIN));
        if(isADMIN){
            channel.getRolesID().forEach(roleRepository::fileDelete); //이 채널과 관련된 권한객체 전원 삭제하기
            channel.getMessagesID().forEach(id -> messageRepository.fileDelete(userID));//채널에 속한 메시지들 삭제
            channelRepository.fileDelete(channelID); //채널 파일에서 삭제하기


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

        Optional.ofNullable(name)
                .ifPresent(channel::updateChannelName);
        Optional.ofNullable(desc)
                .ifPresent(channel::updateChannelDescription);

        channelRepository.fileSave(channelsInFile);
        return channel;
    }

    public Channel update(UUID id, List<UUID> roles, List<UUID> messages) {
        Set<Channel> channels = findAll();

        Channel channel = channels.stream()
                .filter(ch -> ch.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Channel not found: id = " + id));

        // 먼저 제거
        channels.remove(channel);

        if (roles != null) channel.setRolesID(roles);
        if (messages != null) channel.setMessagesID(messages);

        // 수정된 객체 다시 추가
        channels.add(channel);

        channelRepository.fileSave(channels);
        return channel;
    }

    @Override
    public void printChannel(UUID id) { //테스트용 메서드, 수정해야함.
        Channel channel = this.find(id);
        System.out.println(channel);
    }

    @Override
    public void updateUserRole(UUID channelID, UUID willChangeUserID, PermissionLevel roleName, UUID tryingUserID) {
        Channel channel = this.find(channelID);

        Role targetRole = channel.getRolesID().stream()
                .map(roleRepository::fileLoad)
                .filter(r -> r.getUserID().equals(willChangeUserID))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found in channel"));

        boolean isAdmin = channel.getRolesID().stream()
                .map(roleRepository::fileLoad)
                .anyMatch(r -> r.getUserID().equals(tryingUserID)
                        && r.getRoleName().equals(PermissionLevel.ADMIN));

        if (!isAdmin) {
            throw new RuntimeException("User not allowed to change role");
        }

        Set<Role> rolesInFile = roleRepository.fileLoadAll();
        Role roleToUpdate = rolesInFile.stream()
                .filter(r -> r.getId().equals(targetRole.getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Role not found: id = " + targetRole.getId()));

        roleToUpdate.updateGroupName(roleName);
        roleRepository.fileSave(rolesInFile);
    }

    @Override
    public Message addMessage(UUID channelID, UUID userID, String msg) {
        Channel channel = this.find(channelID);



        User user = userRepository.fileLoad(userID);

        // 유저가 해당 채널에 속한 Role을 하나라도 가지고 있으면 허용
        boolean isAllowedUser = user.getRoleIDs().stream()
                .map(roleRepository::fileLoad)
                .anyMatch(role -> role.getChannelID().equals(channelID));

        if (!isAllowedUser) {
            throw new RuntimeException("User not allowed to send message in this channel");
        }

        // 메시지 생성 및 저장
        Message newMessage = new Message(userID, msg, channelID);
        Set<Message> messagesInFile = messageRepository.fileLoadAll();
        messagesInFile.add(newMessage);
        messageRepository.fileSave(messagesInFile);

        // Channel 엔티티의 메시지 리스트 동기화 + 채널 저장
        channel.getMessagesID().add(newMessage.getId());
        this.update(channel.getId(), channel.getRolesID(), channel.getMessagesID());

        return newMessage;
    }

}
