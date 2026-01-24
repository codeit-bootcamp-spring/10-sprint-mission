package com.sprint.mission.discodeit.service.file;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import java.io.*;
import java.util.*;

public class FileChannelService implements ChannelService {

    private FileChannelRepository fileChannelRepository = FileChannelRepository.getInstance();
    private FileUserService fileUserService = new FileUserService();
    private FileRoleService fileRoleService = new FileRoleService();
    private FileMessageService fileMessageService = new FileMessageService();

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

        User user = fileUserService.findAll().stream()
                .filter(u -> u.getId().equals(userID))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found: id = " + userID));


        boolean isADMIN = channel.getRolesID().stream()
                .map(fileRoleService::find)
                .anyMatch(R -> R.getUserID().equals(userID)
                        && R.getRoleName().equals(PermissionLevel.ADMIN));
        if(isADMIN){
            channel.getRolesID().forEach(fileRoleService::delete); //이 채널과 관련된 권한객체 전원 삭제하기
            channel.getMessagesID().forEach(id -> fileMessageService.delete(id, userID));//채널에 속한 메시지들 삭제
            fileChannelRepository.fileDelete(channelID); //채널 파일에서 삭제하기


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

        fileChannelRepository.fileSave(channels);
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

        Role role = channel.getRolesID().stream()
                .map(fileRoleService::find)
                .filter(R-> R.getUserID().equals(willChangeUserID))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User not found in channel"));

        boolean isAdmin = channel.getRolesID().stream()
                .map(fileRoleService::find)
                .anyMatch(R-> R.getUserID().equals(tryingUserID) && R.getRoleName().equals(PermissionLevel.ADMIN));

        if(isAdmin){
            fileRoleService.update(role.getId(), roleName);
        }
        else{
            throw new RuntimeException("User not allowed to change role");
        }
    }

    @Override
    public Message addMessage(UUID channelID, UUID userID, String msg) {
        //권한 확인을 위한 최신 데이터 조회
        User user = fileUserService.find(userID);
        Channel channel = this.find(channelID);

        // 허용된 유저인지 판단
        boolean isAllowedUser = user.getRoleIDs().stream()
                .map(fileRoleService::find)
                .anyMatch(role -> role.getChannelID().equals(channelID));

        if (isAllowedUser) {
            // 메시지 생성 및 저장
            Message newMessage = fileMessageService.create(userID, msg, channelID);
            
            // Channel 엔티티의 메시지 리스트 동기화
            channel.getMessagesID().add(newMessage.getId());
            this.update(channel.getId(), channel.getRolesID(), channel.getMessagesID()); // 채널 파일 업데이트

            return newMessage;
        } else {
            throw new RuntimeException("User not allowed to send message in this channel");
        }
    }

}
