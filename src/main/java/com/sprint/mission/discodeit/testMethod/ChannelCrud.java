package com.sprint.mission.discodeit.testMethod;


import com.sprint.mission.discodeit.dto.ChannelDto;
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
    public void CreatePublicChannel(String name, String description) {
        channelService.createPublic(new ChannelDto.ChannelRequest(name, description));
        System.out.println("공개 채널 생성에 성공하였습니다. \n채널명: " + name + "\n채널 소개" + description);
    }

    // 비공개 채팅방 생성
    public void CreatePrivateChannel(List<String> userName) {
        UUID channelId = channelService.createPrivate(userName.stream()
                .map(name -> userService.CheckUserByName(name).getId()).toList()).channelId();
        System.out.println();
        System.out.println("익명 채널 생성에 성공하였습니다. \n채널 ID: "  + channelId.toString().substring(0, 8));
    }

    // 전체 채널 목록 출력
    public void ListChannel() {
        System.out.println("공개 채널 리스트:\n");
        channelService.findAllPublicChannels().forEach(x ->System.out.println(x.name()));
        System.out.println("익명 채널 리스트:\n");
        channelService.findAllPrivateChannels().forEach(x ->System.out.println(x.channelId().toString().substring(0, 8)));

    }

    // 유저가 참여한 채널 출력
    public void ListChannelByUser(String userName){
        User user = userService.CheckUserByName(userName);
        System.out.println(userName + " 유저가 참여한 공개 채널 리스트");
        user.getChannels().stream().filter(channelId -> channelService.findById(channelId) instanceof ChannelDto.ChannelResponsePublic).
                forEach(channelId -> System.out.println((ChannelDto.ChannelResponsePublic)channelService.findById(channelId)));
        System.out.println(userName + " 유저가 참여한 익명 채널 리스트");

    }

    // 마지막 메세지가 작성된 시간 출력
    public void LastMessageTime() {
    }

    // 채널 정보 수정
    public void UpdateChannel() {
    }

    // 채널 삭제
    public void DeleteChannel() {
    }

}
