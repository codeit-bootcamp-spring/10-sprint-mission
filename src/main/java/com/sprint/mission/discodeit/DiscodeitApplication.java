
package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import java.util.List;
import java.util.UUID;


@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }

    @Bean
    CommandLineRunner channelTestRunner(
            UserService userService,
            ChannelService channelService,
            MessageService messageService
    ) {
        return args -> {

            System.out.println("===== USER 생성 =====");
            var u1 = userService.createUser(
                    new UserCreateRequest("홍길동", "gildong", "g@test.com", "1234", null)
            );
            var u2 = userService.createUser(
                    new UserCreateRequest("김철수", "chulsoo", "c@test.com", "1234", null)
            );

            UUID user1Id = u1.id();
            UUID user2Id = u2.id();

            System.out.println(u1);
            System.out.println(u2);

            System.out.println("\n===== PUBLIC 채널 생성 =====");
            ChannelResponse publicChannel =
                    channelService.createPublicChannel(
                            new PublicChannelCreateRequest("general")
                    );
            System.out.println(publicChannel);

            System.out.println("\n===== PRIVATE 채널 생성 =====");
            ChannelResponse privateChannel =
                    channelService.createPrivateChannel(
                            new PrivateChannelCreateRequest(
                                    List.of(user1Id, user2Id)
                            )
                    );
            System.out.println(privateChannel);

            System.out.println("\n===== PUBLIC 채널 메시지 전송 =====");
            messageService.createMessage(
                    new MessageCreateRequest(
                            user1Id,
                            publicChannel.id(),
                            "안녕하세요!",
                            List.of()
                    )
            );

            Thread.sleep(300); // 시간 차이

            messageService.createMessage(
                    new MessageCreateRequest(
                            user2Id,
                            publicChannel.id(),
                            "반갑습니다!",
                            List.of()
                    )
            );

            System.out.println("\n===== user1 기준 채널 조회 =====");
            channelService.findAllByUserId(user1Id)
                    .forEach(ch -> {
                        System.out.println("채널명: " + ch.name());
                        System.out.println("타입: " + ch.type());
                        System.out.println("마지막 메시지 시각: " + ch.lastMessageAt());
                        System.out.println("참여자 IDs: " + ch.participantUserIds());
                        System.out.println("----");
                    });

            System.out.println("\n===== user2 기준 채널 조회 =====");
            channelService.findAllByUserId(user2Id)
                    .forEach(System.out::println);

            System.out.println("\n===== 채널 테스트 종료 =====");
        };
    }
}


