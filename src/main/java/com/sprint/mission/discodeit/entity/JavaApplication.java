package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import java.util.*;
import java.util.stream.Collectors;

public class JavaApplication {
    public static void main(String[] args) {

        JCFUserService userService = new JCFUserService();
        JCFChannelService channelService = new JCFChannelService();
        JCFMessageService messageService = new JCFMessageService(channelService);
        ArrayList<UUID> userIds = new ArrayList<>();
        ArrayList<UUID> channelIds = new ArrayList<>();

        //유저 정보
        String[][] userData = {
                {"김코딩", "kim@test.com"},
                {"이코딩", "lee@test.com"},
                {"박코딩", "park@test.com"},
                {"최코딩", "choi@test.com"},
                {"정코딩", "jung@test.com"},
                {"안코딩", "ahn@test.com"}
        };

        //회원정보 저장 -> (JCF)
        for (String[] data : userData) {
            User user = userService.userCreate(data[0], data[1]);
            userIds.add(user.getUserId());
        }
//
//        System.out.println();
//
//        // 유저 찾기: 단건
//        System.out.println("[단건 조회(유저)]");
//        User foundOneUser = userService.userFind(userIds.get(0));
//        System.out.println(foundOneUser);
//
//        System.out.println();
//
//        // 유저 찾기: 다건(ALL)
//        System.out.println("[전체 조회(유저)]");
//        for (User user : userService.userFindAll()) {
//            System.out.println(
//                    "이름: " + user.getUserName() + ", 이메일: " + user.getUserEmail());
//        }
//
//        System.out.println();
//
//        // 유저 정보 수정 & 수정 정보 조회
//        UUID idToUpdate = userIds.get(0);
//        User updateUser = userService.userUpdate(
//                idToUpdate,"김코딩_수정", "kim@test2.com");
//
//        System.out.println("[수정 정보 조회(유저)]");
//        System.out.println(
//                "이름: " + updateUser.getUserName() + ", 이메일: " + updateUser.getUserEmail()
//        );
//
//        System.out.println();
//
//        // 유저 정보 삭제 & 삭제 정보 조회
//        UUID idToDelete = userIds.get(1);
//        userService.userDelete(idToDelete);
//
//        System.out.println("삭제 정보 조회(유저)");
//        if (userService.userFind(idToDelete) == null) {
//            System.out.println("성공적으로 삭제 되었습니다.");
//        }
//
//        System.out.println();
//
//        // 유저 삭제 전체 조회
//        System.out.println("[삭제 후 전체 조회(유저)]");
//        for (User user : userService.userFindAll()) {
//            System.out.println(
//                    "이름: " + user.getUserName() + ", 이메일: " + user.getUserEmail());
//        }

        //================================================================

        //채널 생성
        String[] channelData = {
                "채널1", "채널2", "채널3", "채널4"
        };

        for (String name : channelData) {
            Channel channel = channelService.channelCreate(name);
            channelIds.add(channel.getChannelId());
        }

//        // 채널 보이기: 단건
//        System.out.println("[단건 조회(채널)]");
//        Channel foundOneChannel = channelService.channelFind(channelIds.get(0));
//        System.out.println(foundOneChannel);
//
//        System.out.println();
//
//
//        // 채널 찾기: 다건(all)
//        System.out.println("[전체 조회(채널)]");
//
//        String allChannel = channelService.channelFindAll()
//                .stream()
//                .map(Channel::getChannelName)
//                .collect(Collectors.joining(" , "));
//
//        System.out.println("채널: " + allChannel);
//
//        System.out.println();


        //채널 맴버 추가 및 각 채널에 맴버 표시
        System.out.println("[맴버 추가(채널)]");
        channelService.channelMemberAdd(
                channelIds.get(0),
                userService.userFind(userIds.get(0))    // 채널1 - 김
        );
        channelService.channelMemberAdd(
                channelIds.get(0),
                userService.userFind(userIds.get(1))    // 채널1 - 이
        );
        channelService.channelMemberAdd(
                channelIds.get(1),
                userService.userFind(userIds.get(2))    // 채널2 - 박
        );
        channelService.channelMemberAdd(
                channelIds.get(2),
                userService.userFind(userIds.get(3))    // 채널3 - 최
        );
        channelService.channelMemberAdd(
                channelIds.get(3),
                userService.userFind(userIds.get(4))    // 채널4 - 정
        );
        channelService.channelMemberAdd(
                channelIds.get(1),
                userService.userFind(userIds.get(5))    // 채널2 - 안
        );

        System.out.println("\n[채널별 멤버 목록(추가)]");

        for (Channel channel : channelService.channelFindAll()) {
            System.out.println(channel);
            System.out.println();
        }


//        //채널 맴버 삭제 및 각 채널에 맴버 표시(삭제 시 메시지 오류)
//        System.out.println("[맴버 삭제(채널)]");
//
//        channelService.channelMemberRemove(
//                channelIds.get(0),
//                userService.userFind(userIds.get(0)).getUserId()    // 채널1 - 김
//        );
//        channelService.channelMemberRemove(
//                channelIds.get(1),
//                userService.userFind(userIds.get(2)).getUserId()    // 채널2 - 박
//
//        );

        System.out.println("\n[채널별 멤버 목록(삭제)]");

        for (Channel channel : channelService.channelFindAll()) {
            System.out.println(channel);
            System.out.println();
        }

//
//        System.out.println("[채널 삭제]");
//
//        channelService.channelDelete(channelIds.get(0)); //채널 삭제 시 메세지 오류
//
//        for (Channel channel : channelService.channelFindAll()) {
//            System.out.println(channel);
//            System.out.println();
//        }


//        System.out.println("[채널명 변경]");
//
//        channelService.channelNameUpdate(channelIds.get(1), "채널명변경2");
//
//        for (Channel channel : channelService.channelFindAll()) {
//            System.out.println(channel);
//            System.out.println();
//        }

        //=====================================================

        // 메시지 작성
        System.out.println("[메시지 작성(채널)]");

        Message msg1 = messageService.messageCreate(
                channelIds.get(0), userService.userFind(userIds.get(0)), "안녕하세요!"); //  채널1 - 김
        Message msg2 = messageService.messageCreate(
                channelIds.get(0), userService.userFind(userIds.get(1)), "반갑습니다!"); // 채널1 - 이
        Message msg3 = messageService.messageCreate(
                channelIds.get(1), userService.userFind(userIds.get(2)), "채널2 첫 메시지"); // 채널2 - 박

        // 조회(all)
        for (Channel channel : channelService.channelFindAll()) {
            System.out.println(channel.getChannelName() + " 메시지 목록:");
            if (channel.getMessages().isEmpty()) {
                System.out.println("(메시지 없음)");
            } else {
                for (Message msg : channel.getMessages()) {
                    System.out.println(msg); // Message.toString() 사용 -> 닉네임 + 내용 + 타임스템프
                }
            }
            System.out.println();
        }

        // 메시지 수정
        System.out.println("[메시지 수정(채널)]");
        messageService.messageUpdate(channelIds.get(0), msg2.getMessageId(), "수정된 메시지입니다!");

        // 수정된 데이터 조회
        for (Channel channel : channelService.channelFindAll()) {
            System.out.println(channel.getChannelName() + " 메시지 목록:");
            if (channel.getMessages().isEmpty()) {
                System.out.println("(메시지 없음)");
            } else {
                for (Message msg : channel.getMessages()) {
                    System.out.println(msg);
                }
            }
            System.out.println();
        }

        // 메시지 삭제
        System.out.println("[메시지 삭제(채널)]");
        messageService.messageDelete(channelIds.get(0), msg1.getMessageId()); // 채널1 / msg1 - 김

        // 메시지 출력
        for (Channel channel : channelService.channelFindAll()) {
            System.out.println(channel.getChannelName() + " 메시지 목록:");
            if (channel.getMessages().isEmpty()) {
                System.out.println("(메시지 없음)");
            } else {
                for (Message msg : channel.getMessages()) {
                    System.out.println(msg);
                }
            }
            System.out.println();
        }
    }
}







