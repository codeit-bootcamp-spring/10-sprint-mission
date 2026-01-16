package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.factory.ServiceFactory;
import com.sprint.mission.discodeit.service.*;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        ServiceFactory factory = ServiceFactory.getInstance();
        UserService userService = factory.userService();
        ChannelService channelService = factory.channelService();
        MessageService messageService = factory.messageService();

        User user1 = userService.create("정혁");
        Channel channel1 = channelService.create("헬스 정보 공유방");
        channelService.joinChannel(channel1.getId(), user1.getId());
        System.out.println("채널 참가자: " + userService.findUsersByChannelId(channel1.getId()));
        System.out.println("유저의 채널 목록: " + userService.findById(user1.getId()).getChannels());
        messageService.create(user1.getId(), channel1.getId(), "오늘 운동 하셨나요?");
        messageService.create(user1.getId(), channel1.getId(), "오늘 식단 하셨나요?");
        List<Message> channelMessages = messageService.findMessagesByChannelId(channel1.getId());
        System.out.println("채널 내 메시지 목록: " + channelMessages);
        List<Message> userMessages = userService.findMessagesByUserId(user1.getId());
        System.out.println("유저가 작성한 메시지 목록: " + userMessages);
        try {
            System.out.println("\n--- 중복 참가 시도 ---");
            channelService.joinChannel(channel1.getId(), user1.getId());
        } catch (IllegalStateException e) {
            System.out.println("예외 발생(정상): " + e.getMessage());
        }
        System.out.println("\n--- 탈퇴 테스트 ---");
        channelService.leaveChannel(channel1.getId(), user1.getId());
        System.out.println("탈퇴 후 참가자: " + userService.findUsersByChannelId(channel1.getId()));


    }

}
