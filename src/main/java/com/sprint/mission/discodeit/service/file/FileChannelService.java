package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileRoleRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    private FileChannelRepository fileChannelRepository = new FileChannelRepository();
    private FileUserRepository fileUserRepository = new FileUserRepository();
    private FileMessageRepository fileMessageRepository = new FileMessageRepository();
    private FileRoleRepository fileRoleRepository = new FileRoleRepository();

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
        return fileChannelRepository.fileLoad();
    }

    @Override
    public Channel create(String channelName, String channelDescription) {
        Channel channel = new Channel(channelName, channelDescription);
        Set<Channel> channelsInFile = findAll();
        channelsInFile.add(channel);
        fileChannelRepository.fileSave(channelsInFile);
        return channel;
    }

    @Override
    public void delete(UUID channelID, UUID userID) {
        Set<Channel> channelsInFile = findAll();
        Channel channel = channelsInFile.stream()
                .filter(ch -> ch.getId().equals(channelID))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Channel not found: id = " + channelID));

        User user = fileUserRepository.fileLoad().stream()
                .filter(u -> u.getId().equals(userID))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found: id = " + userID));


        boolean isADMIN = channel.getRoles().stream()
                .anyMatch(R -> R.getUsers().getId().equals(userID)
                        && R.getRoleName().equals(PermissionLevel.ADMIN));
        if(isADMIN){
            fileChannelRepository.fileDelete(channelID);

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

        fileChannelRepository.fileSave(channelsInFile);
        return channel;
    }

    public Channel update(UUID id, List<Role> roles, List<Message> messages) {
        Set<Channel> channels = findAll();

        Channel channel = channels.stream()
                .filter(ch -> ch.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Channel not found: id = " + id));

        // 먼저 제거
        channels.remove(channel);

        if (roles != null) channel.updateRoles(roles);
        if (messages != null) channel.updateMessages(messages);

        // 수정된 객체 다시 추가
        channels.add(channel);

        fileChannelRepository.fileSave(channels);
        return channel;
    }

    @Override
    public void printChannel(UUID id) {
        Channel channel = this.find(id);

        // 채널 파일의 channel.getMessages()는 신뢰하지 않고,
        // messages.dat에서 채널 ID로 조회해서 출력한다.
        FileMessageService messageService = new FileMessageService();
        Set<Message> allMessages = messageService.findAll();

        List<Message> channelMessages = allMessages.stream()
                .filter(m -> m.getChannel() != null && id.equals(m.getChannel().getId()))
                .toList();

        System.out.println("=========================");
        System.out.println(channel); // Channel.toString(): 채널명/설명/유저목록(roles)
        System.out.println("=========================");
        channelMessages.forEach(System.out::println);
        System.out.println("=========================");
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
            role.updateGroupName(roleName);
            FileRoleService roleService = new FileRoleService();
            roleService.update(role.getId(), roleName);

            update(channelID, channel.getRoles(), channel.getMessages());
        }
        else{
            throw new RuntimeException("User not allowed to change role");
        }
    }

    @Override
    public Message addMessage(UUID channelID, UUID userID, String msg) {
        // 1. 필요한 서비스 주입 (생성자 주입 권장하나 현재 구조 유지)
        FileMessageService messageService = new FileMessageService();
        FileUserService userService = new FileUserService();
        
        // 2. 권한 확인을 위한 최신 데이터 조회
        User user = userService.find(userID);
        Channel channel = this.find(channelID);

        // 3. 비즈니스 로직 검증
        boolean isAllowedUser = user.getRoles().stream()
                .anyMatch(role -> role.getChannel().getId().equals(channelID));

        if (isAllowedUser) {
            // 4. 메시지 생성 및 저장 (Message 파일에 저장됨)
            Message newMessage = messageService.create(userID, msg, channelID);
            
            // 5. Channel 엔티티의 메시지 리스트 동기화 (필요한 경우에만)
            // 사실 파일 시스템에서는 매번 전체 리스트를 관리하기보다 
            // findMessagesByChannelId(channelID) 같은 메서드를 제공하는 것이 더 효율적입니다.
            channel.getMessages().add(newMessage);
            this.update(channel.getId(), channel.getRoles(), channel.getMessages()); // 채널 파일 업데이트

            return newMessage;
        } else {
            throw new RuntimeException("User not allowed to send message in this channel");
        }
    }

}
