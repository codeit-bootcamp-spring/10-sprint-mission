package com.sprint.mission.discodeit.test;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.List;
import java.util.Optional;

public class UserTest {
    public static void main(String[] args) {
        JCFUserService userService = new JCFUserService();
        System.out.println("===== user1, user2, user3 생성 =====");
        User user1 = userService.createUser("park@gmail.com", "parkhyun", "박", "1234", "20000000");
        User user2 = userService.createUser("jung@gmail.com", "jung", "정", "1234", "20101010");
        User user3 = userService.createUser("hyun@gmail.com", "qorhvk", "hyun", "1234", "20101020");
        System.out.println(userService);

//        System.out.println("===== 중복 이메일이 존재 =====");
//        userService.createUser("park@gmail.com", "hyun", "현", "1234", "20201010");
         // Exception in thread "main" java.lang.IllegalStateException: 동일한 이메일이 존재합니다

//        System.out.println("===== 로그인 =====");
//        Optional<User> login = userService.readUserByEmailAndPw("jung@gmail.com", "1234");
//        System.out.println(login);
//
//        System.out.println("===== 본인 정보 =====");
//        Optional<User> findUser = userService.readUserById(null); // user1.getId()
//        System.out.println(findUser);

//        System.out.println("===== 모든 사용자 목록 =====");
//        List<User> allUsers = userService.readAllUsers();
//        System.out.println(allUsers);
//
//        System.out.println("===== 전체 검색으로 특정 사용자 목록 =====");
//        List<User> searchUsers = userService.searchAllUsersByPartialName("hyun");
//        System.out.println(searchUsers);
//
//
//        System.out.println("===== 이메일 수정 =====");
//        Optional<User> updateEmail = userService.updateEmail(user2.getId(),"으아아아아아@gmail.com");
//        System.out.println("===== 비밀번호 수정 =====");
//        Optional<User> updatePassword = userService.updatePassword(user2.getId(), "5678");
//        System.out.println("===== 별명 수정 =====");
//        Optional<User> updateNickName = userService.updateNickName(user2.getId(), "배고파");
//        System.out.println("===== 사용자 이름 수정 =====");
//        Optional<User> updateUserName = userService.updateUserName(user1.getId(), "배아파");
//        System.out.println("===== 생년월일 수정 =====");
//        Optional<User> updateBirthday = userService.updateBirthday(user2.getId(), "2000-01-01");
//
//        System.out.println(userService);
//
//        System.out.println("===== 전체 검색으로 특정 사용자 목록 =====");
//        List<User> searchUsers1 = userService.searchAllUsersByPartialName("배");
//        System.out.println(searchUsers1);
//
//        System.out.println("===== channel1, channel2 생성 =====");
//        JCFChannelService channelService = new JCFChannelService();
//        Channel channel1 = channelService.createChannel(user1, false, "general", "General Channel");
//        Channel channel2 = channelService.createChannel(user2, true, "private", "private Channel");
//        System.out.println(channelService);
//        System.out.println(channel1);
//        System.out.println(channel2);
//
//        System.out.println("===== channel1에 user2 참가 =====");
//        userService.joinChannel(user2.getId(), channel1);
//        userService.joinChannel(user3.getId(), channel1);
//        System.out.println(user2.getJoinChannelList());
//        System.out.println(channel1.getChannelMembersList());
//        System.out.println(userService);
//
//        System.out.println("===== user2가 참여한 모든 채널 =====");
//        List<Channel> allUsersInChannel = userService.readAllJoinChannelsAtUserByUserId(user2.getId());
//
//        System.out.println(user2.getJoinChannelList());
//
//        System.out.println("===== user2 참여 채널 탈퇴 =====");
//        Optional<User> leaveUser = userService.leaveChannel(user2.getId(), channel1);
//
//        System.out.println(allUsersInChannel);
//
//        System.out.println("===== 삭제 =====");
//        userService.deleteUser(user1.getId());
//        userService.deleteUser(user2.getId());
//        System.out.println(userService);
    }
}
