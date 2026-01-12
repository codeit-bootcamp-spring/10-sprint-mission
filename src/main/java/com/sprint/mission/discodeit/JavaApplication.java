package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class JavaApplication {
    public static void main(String[] args) {
        /**
         * 도메인 별 서비스 구현체 테스트
         * [ ] 등록
         * [ ] 조회(단건, 다건)
         * [ ] 수정
         * [ ] 수정된 데이터 조회
         * [ ] 삭제
         * [ ] 조회를 통해 삭제되었는지 확인
         */


        // 구현체 인스턴스화
        UserService userService = JCFUserService.getInstance();
        ChannelService channelService = JCFChannelService.getInstance();
        MessageService messageService = JCFMessageService.getInstance();

        // user 도메인
        System.out.println("========== User ==========");

        // 유저 등록
        User user1 = userService.create(
            "신지연",
            "sinjiyeon_51815",
            "sjiyeon759@gmail.com",
            "010-0000-0000"
        );
        User user2 = userService.create(
            "김철수",
            "cheolsu_11111",
            "cheolsu11@gmail.com",
            "010-1111-1111"
        );

        // id, 이름으로 유저 정보 조회(단건)
        System.out.println("\n======== 유저 단건 조회 ========");
        System.out.println(userService.findById(user1.getId()));
        System.out.println(userService.findByUserName(user1.getUserName()));

        // 전체 유저 조회(다건)
        System.out.println("\n======== 유저 다건 조회 ========");
        System.out.println(userService.findAllUser());

        // 수정
        user2 = userService.updateUser(user2.getId(), new User(
                null,
                "cheolsu_99999",
                "cheolsu99@naver.com",
                "010-9999-9999"
        ));

        // 수정된 데이터 조회
        System.out.println("\n======== 수정된 데이터 조회 ========");
        System.out.println(user2);

        // 삭제
        userService.delete(user2.getId());

        // 조회를 통해 삭제되었는지 확인
        System.out.println("\n======== 삭제된 데이터 조회 ========");
        try {
            System.out.println(userService.findById(user2.getId()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        // channel
        System.out.println("\n\n============ Channel ============");

        // 채널 등록
        Channel ch1 = channelService.create("ch_1");
        Channel ch2 = channelService.create("ch_2");

        // id로 채널 정보 조회(단건)
        System.out.println("\n======== 채널 단건 조회 ========");
        System.out.println(channelService.findById(ch1.getId()));

        // 전체 채널 조회(다건)
        System.out.println("\n======== 채널 다건 조회 ========");
        System.out.println(channelService.findAllChannel());

        // 수정
        ch1 = channelService.addUser(ch1.getId(), user1.getId());
        ch2 = channelService.addUser(ch2.getId(), user1.getId());
        ch1 = channelService.updateName(ch1.getId(), "CHANNEL_01");


        // 수정된 데이터 조회
        System.out.println("\n======== 수정된 데이터 조회 ========");
        System.out.println(ch1);

        // 삭제
        channelService.delete(ch2.getId());

        // 조회를 통해 삭제되었는지 확인
        System.out.println("\n======== 삭제된 데이터 조회 ========");
        try {
            System.out.println(channelService.findById(ch2.getId()));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }


        // message
        System.out.println("\n\n========== Message ==========");;

        // 메시지 등록
        Message message1 = messageService.create(
            user1.getId(),
            "Hello World!",
            ch1.getId()
        );

        Message message2 = messageService.create(
            user1.getId(),
            "안녕하세요!",
            ch1.getId()
        );

        // uuid로 메시지 정보 조회(단건)
        System.out.println("\n======== 메시지 단건 조회 ========");
        System.out.println(
            messageService.findById(message1.getId())
        );

        // 전체 메시지 조회(다건)
        System.out.println("\n======== 메시지 다건 조회(채널) ========");
        System.out.println(messageService.findByChannel(ch1.getId()));

        System.out.println("\n======== 메시지 다건 조회(유저) ========");
        System.out.println(messageService.findByUser(user1.getId()));

        // 수정
        message2 = messageService.updateById(message2.getId(), "안녕하세요?");

        // 수정된 데이터 조회
        System.out.println("\n======== 수정된 데이터 조회 ========");
        System.out.println(message2);

        // 삭제
        messageService.delete(message2.getId());

        // 조회를 통해 삭제되었는지 확인
        System.out.println("\n======== 삭제된 데이터 조회 ========");
        try {
            System.out.println(
                messageService.findById(message2.getId())
            );
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
