package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.repository.*;
import com.sprint.mission.discodeit.repository.file.*;
import com.sprint.mission.discodeit.service.*;
import com.sprint.mission.discodeit.service.basic.*;

import java.util.UUID;

public class JavaApplication {

    // 1. 유저 셋업 로직
    static User setupUser(UserService userService) {
        User user = userService.create("woody", "woody@codeit.com", "woody1234");
        return user;
    }

    // 2. 채널 셋업 로직
    static Channel setupChannel(ChannelService channelService) {
        Channel channel = channelService.create("공지", "공지 채널입니다.");
        return channel;
    }

    // 3. 메시지 생성 테스트 로직
    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        Message message = messageService.create("안녕하세요.", author.getId(), channel.getId());
        System.out.println("메시지 생성 완료: ID = " + message.getId() + ", 내용 = " + message.getContent());
    }

    public static void main(String[] args) {
        // [TODO] Basic*Service 구현체 초기화

        // A. 저장소(Repository) 초기화: 파일 기반 저장소 사용
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        // B. 서비스(Service) 초기화 및 의존성 주입
        UserService userService = new BasicUserService(userRepository);
        ChannelService channelService = new BasicChannelService(channelRepository);
        MessageService messageService = new BasicMessageService(messageRepository, userService, channelService);
        InteractionService interactionService = new BasicInteractionService(userService, channelService, messageService);

        System.out.println("=== 디스코드 서비스 테스트 시작 ===");

        try {
            // 셋업: 유저와 채널을 각각 생성합니다.
            User user = setupUser(userService);
            System.out.println("유저 셋업 완료: " + user.getName());

            Channel channel = setupChannel(channelService);
            System.out.println("채널 셋업 완료: " + channel.getName());

            interactionService.join(user.getId(), channel.getId());
            System.out.println("유저 채널 참가: " + user.getName() + " -> " + channel.getName());

            messageCreateTest(messageService, channel, user);

            System.out.println("\n--- 최종 데이터 상태 ---");
            System.out.println("총 유저 수: " + userService.findAll().size());
            System.out.println("총 채널 수: " + channelService.findAll().size());
            System.out.println("해당 채널의 메시지 수: " + messageService.findAllByChannelId(channel.getId()).size());

        } catch (Exception e) {
            System.err.println("테스트 실행 중 오류 발생!");
            e.printStackTrace();
        }

        System.out.println("\n=== 테스트 종료 (데이터는 파일에 저장되었습니다) ===");
    }
}