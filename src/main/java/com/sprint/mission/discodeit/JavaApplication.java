package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

import java.util.UUID;

public class JavaApplication {
    public static void main(String[] args) {
////        JCFService
//        UserService userService = new JCFUserService();
//        ChannelService channelService = new JCFChannelService();
//        MessageService messageService = new JCFMessageService(userService,channelService);
//        userService.setChannelService(channelService);
//        userService.setMessageService(messageService);
//        channelService.setMessageService(messageService);

        //FileService
        UserService userService = new FileUserService();
        ChannelService channelService = new FileChannelService();
        MessageService messageService = new FileMessageService(userService,channelService);
        userService.setChannelService(channelService);
        userService.setMessageService(messageService);
        channelService.setMessageService(messageService);

        // 유저
        User user1 = userService.createUser("장동규");
        UUID user1Id = user1.getId();

        User user2 = userService.createUser("이정민");
        UUID user2Id = user2.getId();

        User user3 = userService.createUser("곽인성");
        UUID user3Id = user3.getId();

        User user4 = userService.createUser("김혜성");
        UUID user4Id = user4.getId();

        // 유저 생성 시 예외 상황(유저의 이름이 null일 때)
        System.out.println("**생성할 유저의 이름이 null일 때(예외)**");
        try {
            User user5 = userService.createUser(null);
        }catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage()+"\n");
        }

        // 유저 조회
        System.out.println("==전체 유저 조회==");
        userService.findAll().forEach(System.out::println);
        System.out.println("\n==user1(장동규) 조회==");
        System.out.println(userService.findById(user1Id)+"\n");
        // 유저 조회시 예외 상황(id에 해당하는 사용자를 찾을 수 없을 때)
        System.out.println("**id에 해당하는 사용자를 찾을 수 없을 때(예외)**");
        try{
            userService.findById(UUID.randomUUID());
        } catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage()+"\n");
        }

        // 유저 수정
        System.out.println("==user4 이름 변경(김혜성 -> 박상호)==");
        System.out.println(userService.updateById(user4Id,"박상호")); // 김혜성 -> 박상호
        // 유저 수정 시 예외 상황(변경하고자 하는 유저의 id를 찾을 수 없을 때)
        System.out.println("**변경하고자 하는 유저의 id를 찾을 수 없을 때(예외)**");
        try {
            userService.updateById(UUID.randomUUID(),"양성봉");
        } catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage()+"\n");
        }
        // 유저 수정 시 예외 상황(변경 하려는 유저의 이름이 null일 때)
        System.out.println("**변경 하려는 유저의 이름이 null일 때(에외)**");
        try {
            userService.updateById(user1Id,null);
        } catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage()+"\n");
        }

        // 유저 삭제
        System.out.println("==user4(박상호) 삭제 후 전체 출력==");
        userService.deleteById(user4Id);
        userService.findAll().forEach(System.out::println);

        // 채널
        Channel channel1 = channelService.createChannel("스터디 채널");
        UUID channel1Id = channel1.getId();

        Channel channel2 = channelService.createChannel("게임 채널");
        UUID channel2Id = channel2.getId();
        //채널 생성 시 예외 발생(채널의 채널명이 null일 경우)
        System.out.println("\n**생성하려는 채널의 채널명이 null인 경우(예외)**");
        try {
            channelService.createChannel(null);
        } catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage());
        }
        
        // 채널 입장
//        user1.joinChannel(channel1);// 장동규 -> 스터디 채널
//        user2.joinChannel(channel1);// 이정민 -> 스터디 채널
//        user3.joinChannel(channel2);// 곽인성 -> 게임 채널
        userService.joinChannel(user1.getId(),channel1Id);
        userService.joinChannel(user2.getId(),channel1Id);
        userService.joinChannel(user3.getId(),channel2Id);

        // 채널 조회
        System.out.println("\n==모든 채널==");
        channelService.findAll().forEach(System.out::println);
        System.out.println("\n==channel1(스터디 채널) 조회==");
        System.out.println(channelService.findById(channel1Id)+"\n");
        // 채널 조회 시 예외 상황(id에 해당하는 채널이 없는 경우)
        System.out.println("**id에 해당하는 채널이 없는 경우(예외)**");
        try {
            channelService.findById(UUID.randomUUID());
        } catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage()+"\n");
        }

        // 채널 수정
        System.out.println("==channel2 이름 변경 (게임 채널 -> game 채널) ==");
        System.out.println(channelService.updateById(channel2Id,"game 채널")+"\n");
        // 채널 수정 시 예외 상황(채널명을 null로 변경하는 경우)
        System.out.println("**채널명을 null로 변경하는 경우(예외)**");
        try {
            channelService.updateById(channel1Id,null);
        } catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage()+"\n");
        }

        // 채널 삭제
        System.out.println("==channel2(game) 삭제==");
        channelService.deleteById(channel2Id);
        channelService.findAll().forEach(System.out::println);


        // 메시지
        Message message1 = messageService.createMessage(user1Id,"토익 스터디 합시다",channel1Id);
        UUID message1Id = message1.getId();

        Message message2 = messageService.createMessage(user2Id,"스프링 스터디 합시다",channel1Id);
        UUID message2Id = message2.getId();
        // 메시지 생성 시 예외 상황(생성하려는 메시지가 null인 경우)
        System.out.println("\n**생성하려는 메시지가 null인 경우(예외)**");
        try {
            messageService.createMessage(user1Id,null,channel1Id);
        } catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage());
        }
        // 메시지 생성 시 예외 상황(참여하지 않은 채널에 메시지를 작성하려는 경우)
        System.out.println("\n**참여하지 않은 채널에 메시지를 작성하려는 경우(예외)**");
        try {
            // user2는 channel2에 참여하지 않은 상황
            messageService.createMessage(user2Id,"새로운 메시지 내용",channel2Id);
        } catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage());
        }

        // 메시지 조회
        System.out.println("\n==message1 조회==");
        System.out.println(messageService.findById(message1Id)+"\n");;
        System.out.println("\n==전체 메시지 조회==");
        messageService.findAll().forEach(System.out::println);
        // 메시지 조회 시 예외 상황(조회하려는 id가 null인 경우)
        System.out.println("\n**조회하려는 id가 null인 경우(예외)**");
        try {
            messageService.findById(null);
        } catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage());
        }
        // 메시지 조회 시 예외 상황(id에 해당하는 메시지가 존재하지 않는 경우)
        System.out.println("\n**id에 해당하는 메시지가 존재하지 않는 경우(예외)**");
        try {
            messageService.findById(UUID.randomUUID());
        } catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage());
        }

        // 메시지 수정
        System.out.println("\n==message1 수정 (토익 스터디 합시다 -> 영어 스터디 합시다)");
        System.out.println(messageService.updateById(message1Id,"영어 스터디 합시다"));
        // 메시지 수정 시 예외 상황(메시지의 내용을 null로 업데이트 하려는 경우)
        System.out.println("\n**메시지의 내용을 null로 업데이트 하려는 경우(예외)**");
        try {
            messageService.updateById(message1Id,null);
        } catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage());
        }

        // 메시지 삭제
        System.out.println("\n==message1 삭제 후 전체 조회 ('영어 스터디 합시다'삭제)==");
        messageService.deleteById(message1Id);
        messageService.findAll().forEach(System.out::println);

        // channel1의 메시지 목록 출력
        System.out.println("\n==channel1의 메시지 목록 출력==");
        messageService.getMessagesByChannelId(channel1Id)
                .forEach(System.out::println);

        // channel1에 참여 중인 유저 목록 출력
        System.out.println("\n==channel1에 참여 중인 유저 목록 출력==");
        userService.getUsersByChannelId(channel1Id)
                .forEach(System.out::println);

        // user1(장동규)가 참여 중인 채널 목록 출력
        System.out.println("\n==user1(장동규)가 참여 중인 채널 목록 출력==");
        channelService.getChannelsByUserId(user1Id)
                .forEach(System.out::println);

        // user1(장동규)가 작성한 메시지 목록 출력
        System.out.println("\n==user1(장동규)가 작성한 메시지 목록 출력==");
        messageService.getMessagesByUserId(user1Id)
                .forEach(System.out::println);


        // 각 객체 삭제 시 연관 데이터 삭제 확인
        User user5 = userService.createUser("노경섭");

        Channel catChannel = channelService.createChannel("고양이 채널");
        Channel dogChannel = channelService.createChannel("강아지 채널");
        Channel soccerChannel = channelService.createChannel("축구 채널");

//        user5.joinChannel(catChannel); // 고양이 채널
//        user5.joinChannel(dogChannel); // 강아지 채널
//        user5.joinChannel(soccerChannel); // 축구 채널
//        user5.joinChannel(channel1); // 스터디 채널
        userService.joinChannel(user5.getId(), catChannel.getId());
        userService.joinChannel(user5.getId(), dogChannel.getId());
        userService.joinChannel(user5.getId(), soccerChannel.getId());
        userService.joinChannel(user5.getId(), channel1.getId());


        Message message3 = messageService.createMessage(user5.getId(),"고양이 귀여워요",catChannel.getId());
        Message message4 = messageService.createMessage(user5.getId(),"고양이 좋아요",catChannel.getId());
        Message message5 = messageService.createMessage(user5.getId(),"강아지 귀여워요",dogChannel.getId());
        Message message6 = messageService.createMessage(user5.getId(),"강아지 좋아요",dogChannel.getId());
        Message message7 = messageService.createMessage(user5.getId(),"축구 재밌어요",soccerChannel.getId());
        Message message8 = messageService.createMessage(user5.getId(),"축구 좋아요",soccerChannel.getId());

        System.out.println("\n=====삭제 시 연관 데이터 삭제 확인=======");
        System.out.println("=====축구 채널 삭제 전=====");
        System.out.println("-user5(노경섭)가 작성한 메시지 목록");
        messageService.getMessagesByUserId(user5.getId())
                        .forEach(System.out::println);
        System.out.println("\n-축구 채널의 메시지 목록");
        messageService.getMessagesByChannelId(soccerChannel.getId())
                        .forEach(System.out::println);
        System.out.println("\n-전체 채널 목록");
        channelService.findAll().forEach(System.out::println);
        System.out.println("\n-전체 메시지 목록");
        messageService.findAll().forEach(System.out::println);
        
        // soccerChannel 삭제시
        System.out.println("\n=====축구 채널 삭제 시=====");
        channelService.deleteById(soccerChannel.getId());
        System.out.println("\n-user5(노경섭)가 작성한 메시지 목록");
        messageService.getMessagesByUserId(user5.getId())
                .forEach(System.out::println);
        System.out.println("\n-축구 채널의 메시지 목록");
        try {
            messageService.getMessagesByChannelId(soccerChannel.getId())
                    .forEach(System.out::println);
        }catch (Exception e) {
            System.out.println("**예외 상황 발생 : "+e.getMessage());
        }
        System.out.println("\n-전체 채널 목록");
        channelService.findAll().forEach(System.out::println);
        System.out.println("\n-전체 메시지 목록");
        messageService.findAll().forEach(System.out::println);
        
        // 메시지 삭제 시
        System.out.println("\n=====message3(고양이 귀여워요) 삭제시 =====");
        messageService.deleteById(message3.getId());
        System.out.println("-전체 메시지 목록");
        messageService.findAll().forEach(System.out::println);
        System.out.println("\n-고양이 채널 메시지 목록");
        messageService.getMessagesByChannelId(catChannel.getId())
                .forEach(System.out::println);
        System.out.println("\n-user5(노경섭)이 작성한 메시지 목록");
        messageService.getMessagesByUserId(user5.getId())
                .forEach(System.out::println);

        // 유저 삭제 시
        System.out.println("\n====== user5(노경섭) 삭제 시 ======");
        userService.deleteById(user5.getId());
        System.out.println("-전체 유저 목록");
        userService.findAll().forEach(System.out::println);
        System.out.println("\n-글을 작성했던 강아지 채널의 유저 목록");
        userService.getUsersByChannelId(dogChannel.getId())
                .forEach(System.out::println);
        System.out.println("\n-글을 작성했던 강아지 채널의 글 목록");
        messageService.getMessagesByChannelId(dogChannel.getId())
                .forEach(System.out::println);
    }
}
