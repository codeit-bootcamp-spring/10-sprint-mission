package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }

    @Bean
    public CommandLineRunner dataInitializer(
            UserService userService,
            ChannelService channelService,
            MessageService messageService
    ) {
        return args -> {
            // 1. 테스트용 유저 생성
            UserCreateRequest userRequest = new UserCreateRequest("tester", "password123", "test@example.com", null);
            UserResponse user = userService.create(userRequest);

            UserCreateRequest userRequest2 = new UserCreateRequest("tester2", "password123", "test2@example.com", null);
            UserResponse user2 = userService.create(userRequest2);

            // 2. 테스트용 공용 채널 생성
            PublicChannelCreateRequest channelRequest = new PublicChannelCreateRequest("자유게시판", "자유롭게 대화하는 곳입니다.");
            ChannelResponse channel = channelService.createPublic(channelRequest);

            // 3. 테스트용 초기 메시지 생성
            MessageCreateRequest messageRequest = new MessageCreateRequest(
                    "메시지1",
                    channel.id(),
                    user.id(),
                    List.of() // 첨부파일 없음
            );
            MessageResponse message = messageService.create(messageRequest);

            MessageCreateRequest messageRequest2 = new MessageCreateRequest(
                    "메시지2",
                    channel.id(),
                    user.id(),
                    List.of() // 첨부파일 없음
            );
            MessageResponse message2 = messageService.create(messageRequest2);

            // 콘솔에 ID 정보 출력 (Postman 테스트 시 복사해서 사용 가능)
            System.out.println("\n===== [Initial Data Seeded] =====");
            System.out.println("Test User id: " + user.id());
            System.out.println("Test Channel id: " + channel.id());
            System.out.println("Test Message id: " + message.id());
            System.out.println("=================================\n");
        };
    }
}