package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.*;

public class ChannelTest {
    public static void main(String[] args) {
        Map<UUID, User> userRepo = new HashMap<>();
        JCFUserService userService = new JCFUserService(userRepo);
        System.out.println("===== user1, user2, user3 생성 =====");
        User user1 = userService.createUser("park@gmail.com", "user1Nick", "user1Name", "1234", "20000000");
        User user2 = userService.createUser("jung@gmail.com", "user2Nick", "user2Name", "1234", "20101010");
        User user3 = userService.createUser("hyun@gmail.com", "user3Nick", "user3Name", "1234", "20101020");
        System.out.println(userService);

        Map<UUID, Channel> channelRepo = new HashMap<>();
        JCFChannelService channelService = new JCFChannelService(channelRepo);
        System.out.println("===== channel1, channel2 생성 =====");
        Channel channel1 = channelService.createChannel(user1, false, "channel1General", "General Channel");
        Channel channel2 = channelService.createChannel(user2, true, "channel2Private", "private Channel");
        System.out.println(channelService);


        System.out.println("===== user1 channel2에 참여 =====");
        userService.joinChannel(user1.getId(), user1.getId(), channel2);
        System.out.println(user1.getJoinChannelList());
//        userService.joinChannel(user1.getId(), channel2); // 참여 여부 테스트
        System.out.println("===== user3 channel1에 참여 =====");
        userService.joinChannel(user3.getId(), user3.getId(), channel1);
        System.out.println(user3.getJoinChannelList());

//        System.out.println("===== channel1 정보 읽기 =====");

//        System.out.println("===== channel name에 general이 들어가는 channel 찾기 =====");

//        System.out.println("===== 채널 목록 전체 =====");

//        System.out.println("===== 비공개 여부에 따른 채널 목록 =====");

//        System.out.println("===== 특정 채널에 속한 모든 유저 =====");

//        System.out.println("===== 특정 채널에서 특정 유저 찾기 =====");

//        System.out.println("===== 채널 channelName 수정 =====");
//
//        System.out.println("===== 채널 isPrivate 수정 =====");
//
//        System.out.println("===== 채널 owner 수정 =====");
//
//        System.out.println("===== 채널 description 수정 =====");


    }
}
