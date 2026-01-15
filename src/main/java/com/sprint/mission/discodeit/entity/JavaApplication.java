package com.sprint.mission.discodeit.entity;


import com.sprint.mission.discodeit.exception.MessageNotFoundException;
import com.sprint.mission.discodeit.exception.UserNotFoundException;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.view.ChannelMessageView;
import com.sprint.mission.discodeit.view.ChannelView;

import java.util.ArrayList;
import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {

        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService(channelService);

        // 유저 정보
        String[][] userData = {
                {"김코딩", "kim@test.com"},
                {"이코딩", "lee@test.com"},
                {"박코딩", "park@test.com"},
                {"최코딩", "choi@test.com"},
                {"정코딩", "jung@test.com"},
                {"안코딩", "ahn@test.com"}
        };

        // 회원정보 저장 -> (JCF)
        List<User> users = new ArrayList<>();
        for (String[] data : userData) {
            users.add(userService.createUser(data[0], data[1]));
        }

//        System.out.println();
//
//        // 유저 찾기: 단건
//        System.out.println("[단건 조회(유저)]");
//        User foundOneUser = userService.findUser(users.get(0).getId());
//        System.out.println(
//                "이름: " + foundOneUser.getUserName() + "\n" +
//                        "이메일: " + foundOneUser.getUserEmail() + "\n" +
//                        "id:" + foundOneUser.getId()
//        );
//
//        System.out.println();
//
//        // 유저 찾기: 다건(ALL)
//        System.out.println("[전체 조회(유저)]");
//        for (User user : userService.findAllUser()) {
//            System.out.println(user);
//            System.out.println();
//        }
//
//        System.out.println();
//
//        // 유저 정보 수정 & 수정 정보 조회
//        User updateUser =
//                userService.updateUser(users.get(0).getId(), "김코딩_수정", "kim@test2.com");
//
//        System.out.println("[수정 정보 조회(유저)]");
//        for (User user : userService.findAllUser()) {
//            System.out.println(user);
//            System.out.println();
//        }
//
//        System.out.println();
//
//        // 유저 정보 삭제 & 삭제 후 전체 조회
//        User toDeleteUser = users.get(1);
//        if (toDeleteUser == null) {
//            throw new UserNotFoundException();
//        } else {
//            userService.deleteUser(toDeleteUser.getId());
//            System.out.println("[삭제 후 전체 조회(유저)]");
//            for (User user : userService.findAllUser()) {
//                System.out.println(user);
//                System.out.println();
//            }
//        }

        //=====================================================

        // 채널 생성
        String[] channelData = {"채널1", "채널2", "채널3", "채널4"};
        List<Channel> channels = new ArrayList<>();
        for (String name : channelData) {
            channels.add(channelService.createChannel(name));
        }

//        // 채널 단건 조회
//        System.out.println("[단건 조회(채널)]");
//        Channel foundOneChannel = channelService.findChannel(channels.get(0).getId());
//        System.out.println("채널명: " + foundOneChannel.getChannelName());
//
//        System.out.println();
//
//        // 채널 전체 조회
//        System.out.println("[전체 조회(채널)]");
//        for (Channel channel : channelService.findAllChannel()) {
//            System.out.println(ChannelView.viewChannel(channel, userService));
//            System.out.println();
//        }

        System.out.println();

        // 채널 멤버 추가
//        System.out.println("[맴버 추가(채널)]");
        channelService.memberAddChannel(        // 채널1 - 김 추가
                channels.get(0).getId(),
                users.get(0).getId()
        );
        channelService.memberAddChannel(        // 채널1 - 이 추가
                channels.get(0).getId(),
                users.get(1).getId()
        );
        channelService.memberAddChannel(        // 채널2 - 박 추가
                channels.get(1).getId(),
                users.get(2).getId()
        );
        channelService.memberAddChannel(        // 채널3 - 최 추가
                channels.get(2).getId(),
                users.get(3).getId()
        );
        channelService.memberAddChannel(        // 채널4 - 정 추가
                channels.get(3).getId(),
                users.get(4).getId()
        );
        channelService.memberAddChannel(        // 채널2 - 안 추가
                channels.get(1).getId(),
                users.get(5).getId()
        );

//        for (Channel channel : channelService.findAllChannel()) {
//            System.out.println(ChannelView.viewChannel(channel, userService));
//            System.out.println();
//        }
//
//        System.out.println();
//
//        System.out.println(" ");
//        // 채널 멤버 삭제
//        System.out.println("[맴버 삭제(채널)]");
//        channelService.memberRemoveChannel(
//                channels.get(0).getId(), users.get(0).getId());         // 채널1 - 김 삭제
//        channelService.memberRemoveChannel(
//                channels.get(1).getId(), users.get(2).getId());         // 채널2 - 박 삭제
//
//        for (Channel channel : channelService.findAllChannel()) {
//            System.out.println(ChannelView.viewChannel(channel, userService));
//            System.out.println();
//        }
//
//        System.out.println(" ");
//        // 채널 삭제
//        System.out.println("[채널 삭제]");
//        channelService.deleteChannel(channels.get(0).getId());      // 채널1 삭제
//
//        for (Channel channel : channelService.findAllChannel()) {
//            System.out.println(ChannelView.viewChannel(channel, userService));
//            System.out.println();
//        }
//
//        System.out.println(" ");
//        // 채널명 변경
//        System.out.println("[채널명 변경]");
//        channelService.nameUpdateChannel(
//                channels.get(1).getId(), "채널명변경2");
//
//        for (Channel channel : channelService.findAllChannel()) {
//            System.out.println(ChannelView.viewChannel(channel, userService));
//            System.out.println();
//        }

//        //=====================================================
//
        // 메시지 작성
        Message msg1 = messageService.createMessage(
                channels.get(0).getId(),
                users.get(0).getId(),
                "안녕하세요!"
        );
        Message msg2 = messageService.createMessage(
                channels.get(0).getId(),
                users.get(1).getId(),
                "반갑습니다!"
        );
        Message msg3 = messageService.createMessage(
                channels.get(1).getId(),
                users.get(2).getId(),
                "채널2 첫 메시지"
        );


        // 메시지 단건 조회
        System.out.println("메시지 단건 조회");
        Message foundMsg1 = messageService.findMessage(channels.get(0).getId(), msg1.getId());
        System.out.println(foundMsg1);

        System.out.println();


        // 메시지 조회 전체
        for (Channel channel : channelService.findAllChannel()) {
            System.out.println(ChannelView.viewChannel(channel, userService));
            System.out.println();
            System.out.println(ChannelMessageView.viewMessage(channel, userService));
            System.out.println();
        }
        System.out.println();



        // 메시지 수정
        System.out.println("[메시지 수정]");
        messageService.updateMessage(
                channels.get(0).getId(),
                msg2.getId(),
                "수정된 메시지입니다!"
        );

        // 수정된 메시지 조회
        for (Channel channel : channelService.findAllChannel()) {
            System.out.println(ChannelView.viewChannel(channel, userService));
            System.out.println();
            System.out.println(ChannelMessageView.viewMessage(channel, userService));
            System.out.println();
        }


        // 메시지 삭제
        System.out.println("[메시지 삭제]");
        messageService.deleteMessage(
                channels.get(0).getId(), msg1.getId());

        // 삭제 후 메시지 조회
        for (Channel channel : channelService.findAllChannel()) {
            if (channel.getMessages() != null) {
                System.out.println(ChannelView.viewChannel(channel, userService));
                System.out.println();
                System.out.println(ChannelMessageView.viewMessage(channel, userService));
                System.out.println();

            }else {
                throw new MessageNotFoundException();
            }
            System.out.println();
        }








    }
}





