package com.sprint.mission.discodeit.testMethod;


import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ChannelCrud {

    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    // 일반 채팅방 생성
    public void createPublicChannel(String name, String description) {
        channelService.createPublic(new ChannelDto.ChannelRequest(name, description));
        System.out.println("공개 채널 생성에 성공하였습니다. \n채널명: " + name + "\n채널 소개" + description);
    }

    // 비공개 채팅방 생성
    public void createPrivateChannel(List<String> userName) {
        UUID channelId = channelService.createPrivate(userName.stream()
                .map(name -> userService.checkUserByName(name).getId()).toList()).channelId();
        System.out.println();
        System.out.println("익명 채널 생성에 성공하였습니다. \n채널 ID: " + channelId.toString().substring(0, 8));
    }

    // 전체 채널 목록 출력
    public void listChannel() {
        System.out.println("공개 채널 리스트:\n");
        channelService.findAllPublicChannels().forEach(x -> System.out.println(x.name()));
        System.out.println("익명 채널 리스트:\n");
        channelService.findAllPrivateChannels().forEach(x -> System.out.println(x.serverId()));

    }

    // 유저가 참여한 채널 출력
    public void listChannelByUser(String userName) {
        User user = userService.checkUserByName(userName);
        System.out.println(userName + " 유저가 참여한 공개 채널 리스트");
        user.getChannels().stream().filter(channelId -> channelService.findById(channelId) instanceof ChannelDto.ChannelResponsePublic).
                forEach(channelId -> System.out.println(((ChannelDto.ChannelResponsePublic) channelService.findById(channelId)).name()));
        System.out.println(userName + " 유저가 참여한 익명 채널 리스트");
        user.getChannels().stream().filter(channelId -> channelService.findById(channelId) instanceof ChannelDto.ChannelResponsePrivate).
                forEach(channelId -> System.out.println(((ChannelDto.ChannelResponsePrivate) channelService.findById(channelId)).serverId()));

    }

    // 채널 내 메세지 전체 출력
    public void listAllMessage(String nameOrId) {
        Channel channel = channelService.checkChannel(nameOrId);
        System.out.println("메세지 목록: ");
        channel.getMessageIds().forEach(messageId -> {
            MessageDto.MessageResponse message = messageService.findById(messageId);
            System.out.println(message.content() + " - " + message.createdAt() + "ID: " + message.messageId().toString().substring(0,8));
        });
    }

    // 마지막 메세지가 작성된 시간 출력
    public void lastMessageTime(String nameOrId) {
        Channel channel = channelService.checkChannel(nameOrId);
        System.out.println("마지막 메세지 작성: " + channel.getLastMessageAt());
    }

    // 채널 정보 수정 (공개 채널)
    public void updatePublicChannel(String name, String description) {
        Channel channel = channelService.checkChannel(name);
        if (channel.getType().equals(Channel.channelType.PRIVATE)) {
            System.out.println("공개 채널만 정보 수정이 가능합니다");
            return;
        }
        channelService.update(channel.getId(), new ChannelDto.ChannelRequest(name, description));
        System.out.println("채널 정보가 수정되었습니다. \n채널명: " + name + "채널 설명: " + description);
    }

    // 채널 삭제
    public void deleteChannel(String nameOrId) {
        Channel channel = channelService.checkChannel(nameOrId);
        channelService.delete(channel.getId());
        if (channel.getType().equals(Channel.channelType.PRIVATE))
            System.out.println("익명 채널이 삭제되었습니다. 채널 ID: " + channel.getPrivateServerId());
        else
            System.out.println("공개 채널이 삭제되었습니다. 채널명: " + channel.getName());

    }

}
