package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.BaseEntity;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.*;

public class UserTest {
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

        System.out.println("===== 채널 참여 =====");
        System.out.println("===== user1 channel2에 참여 =====");
        userService.joinChannel(user1.getId(), user1.getId(), channel2);
//        userService.joinChannel(user1.getId(), channel2); // 참여 여부 테스트
        System.out.println("===== user3 channel1에 참여 =====");
        userService.joinChannel(user3.getId(), user3.getId(), channel1);

        System.out.println("===== 채널 참여 =====");
        user3.getJoinChannelList().stream().map(BaseEntity::getId).forEach(System.out::println);
        System.out.println();
        System.out.println(userService.joinChannel(user3.getId(), user3.getId(), channel2));
        user3.getJoinChannelList().stream().map(BaseEntity::getId).forEach(System.out::println);

        System.out.println("===== 채널 탈퇴 =====");
//        userService.leaveChannel(user3.getId(), user3.getId(), channel1);
//        user3.getJoinChannelList().stream().map(BaseEntity::getId).forEach(System.out::println);

//        System.out.println("===== 로그인 =====");

//        System.out.println("===== 본인 정보 =====");

//        System.out.println("===== 모든 사용자 목록 =====");

//        System.out.println("===== 전체 검색으로 특정 사용자 목록 =====");

//        System.out.println("===== 이메일 수정 =====");
//        System.out.println(user1.getEmail());
//        userService.updateEmail(user1.getId(), user1.getId(), "change@change.com");
//        System.out.println(user1.getEmail());
//
//        System.out.println("===== 비밀번호 수정 =====");
//        System.out.println(user1.getPassword());
//        userService.updatePassword(user1.getId(), user1.getId(), "5678");
//        System.out.println(user1.getPassword());
//
//        System.out.println("===== 별명 수정 =====");
//        System.out.println(user1.getNickName());
//        userService.updateNickName(user1.getId(), user1.getId(), "changeNick1");
//        System.out.println(user1.getNickName());
//
//        System.out.println("===== 사용자 이름 수정 =====");
//        System.out.println(user2.getUserName());
//        userService.updateUserName(user2.getId(), user2.getId(), "changeName2");
//        System.out.println(user2.getUserName());
//
//        System.out.println("===== 생년월일 수정 =====");
//        System.out.println(user3.getBirthday());
//        userService.updateBirthday(user3.getId(), user3.getId(), "20000002");
//        System.out.println(user3.getBirthday());

        System.out.println("===== 삭제 =====");
        System.out.println(userService.readUserById(user1.getId()));
        System.out.println(user1.getOwnerChannelList());
        System.out.println(user1.getJoinChannelList());
        System.out.println(userService.readUserJoinChannelsByUserId(user1.getId()));
        System.out.println("=====");
//        user1.getJoinChannelList().forEach(channel -> channel.removeChannelMembers(user1));
//        user1.getOwnerChannelList().forEach(channel->channel.re)
        userService.deleteUser(user1.getId(), user2.getId());
        System.out.println("=====");
        System.out.println(channel1.getOwner());
        System.out.println(channel1.getChannelMembersList());

//        System.out.println("===== 전체 검색으로 특정 사용자 목록 =====");

//        System.out.println("===== user2가 참여한 모든 채널 =====");

//        System.out.println("===== 특정 사용자가 참여한 채널 중에서 특정 채널 검색 =====");

//        System.out.println("===== 특정 사용자가 owner인 모든 채널 =====");



    }
}
