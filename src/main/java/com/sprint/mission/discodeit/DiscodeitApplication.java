package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.websocket.Decoder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
            UserCreateRequest userRequest1 = new UserCreateRequest(
                    "tester",
                    "test@example.com",
                    "password123" ,
                    null);
            UserResponse user1 = userService.create(userRequest1);

            BinaryContentRequest profileImageRequest = new BinaryContentRequest(
                    "profile.png",
                    "image/png",
                    "Beer".getBytes(StandardCharsets.UTF_8)
            ); // 테스트용 더미 바이트 데이터
            UserCreateRequest userRequest2 = new UserCreateRequest(
                    "tester2 - 프로필 이미지",
                    "test2@example.com",
                    "password123",
                    profileImageRequest);
            UserResponse user2 = userService.create(userRequest2);

            // 2. 테스트용 공용 채널 생성
            PublicChannelCreateRequest channelRequest1 = new PublicChannelCreateRequest("자유게시판", "자유롭게 대화하는 곳입니다.");
            ChannelResponse channelPublic = channelService.createPublic(channelRequest1);

            Set<UUID> participantIds = Set.of(user1.id(), user2.id());
            PrivateChannelCreateRequest channerRequest2 = new PrivateChannelCreateRequest(participantIds);
            ChannelResponse channelPrivate = channelService.createPrivate(channerRequest2);

            // 3. 테스트용 초기 메시지 생성
            MessageCreateRequest messageRequest1 = new MessageCreateRequest(
                    "메시지1",
                    channelPublic.id(),
                    user1.id(),
                    List.of() // 첨부파일 없음
            );
            MessageResponse message1 = messageService.create(messageRequest1);

            BinaryContentRequest attachment1 = new BinaryContentRequest(
                    "beer.jpg",
                    "image/jpg",
                    "Beer".getBytes(StandardCharsets.UTF_8)
            );
            BinaryContentRequest attachment2 = new BinaryContentRequest(
                    "wine.jpg",
                    "image/jpg",
                    "Wine".getBytes(StandardCharsets.UTF_8)
            );
            MessageCreateRequest messageRequest2 = new MessageCreateRequest(
                    "메시지2 - 첨부파일",
                    channelPublic.id(),
                    user1.id(),
                    List.of(attachment1, attachment2)
            );
            MessageResponse message2 = messageService.create(messageRequest2);

            // 콘솔에 ID 정보 출력 (Postman 테스트 시 복사해서 사용 가능)
            System.out.println("\n===== [Initial Data Seeded] =====");
            System.out.println("Test User1: " + user1);
            System.out.println("Test User2: " + user2);
            System.out.println("Test Channel(Public): " + channelPublic);
            System.out.println("Test Channel(Private): " + channelPrivate);
            System.out.println("Test Message1: " + message1);
            System.out.println("Test Message2: " + message2);
            System.out.println("=================================\n");
        };
    }
}