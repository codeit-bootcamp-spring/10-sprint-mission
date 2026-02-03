
package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.ChannelUpdateRequest;
import com.sprint.mission.discodeit.dto.channel.PrivateChannelCreateRequest;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.message.MessageUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;

import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;
import com.sprint.mission.discodeit.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;


@SpringBootApplication
public class DiscodeitApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscodeitApplication.class, args);
    }

    @Bean
    CommandLineRunner smokeTest(
            UserService userService,
            ChannelService channelService,
            MessageService messageService
    ) {
        return args -> {

            // 1) USER

            System.out.println("===== 1) USER: create =====");

            UserResponse u1 = userService.createUser(
                    new UserCreateRequest("홍길동", "gildong", "g@test.com", "1234", null)
            );
            UserResponse u2 = userService.createUser(
                    new UserCreateRequest("김철수", "chulsoo", "c@test.com", "1234", null)
            );

            System.out.println("u1 = " + u1);
            System.out.println("u2 = " + u2);

            UUID u1Id = u1.id();
            UUID u2Id = u2.id();

            System.out.println("\n===== USER: findById / findAll =====");
            System.out.println("find u1 = " + userService.findUserById(u1Id));
            System.out.println("all users = " + userService.getUserAll());

            System.out.println("\n===== USER: update (name/alias/email/password) =====");
            UserResponse u1Updated = userService.updateUser(
                    new UserUpdateRequest(
                            u1Id,
                            "홍길동(수정)",
                            "gildong2",
                            "g2@test.com",
                            "9999",
                            null //
                    )
            );
            System.out.println("u1Updated = " + u1Updated);


            // 2) CHANNEL

            System.out.println("\n===== 2) CHANNEL: createPublic =====");
            ChannelResponse publicCh = channelService.createPublicChannel(
                    new PublicChannelCreateRequest("general")
            );
            System.out.println("publicCh = " + publicCh);

            System.out.println("\n===== CHANNEL: createPrivate (u1,u2) + ReadStatus 생성 =====");
            ChannelResponse privateCh = channelService.createPrivateChannel(
                    new PrivateChannelCreateRequest(List.of(u1Id, u2Id))
            );
            System.out.println("privateCh = " + privateCh);

            System.out.println("\n===== CHANNEL: findById =====");
            System.out.println("find public = " + channelService.findChannelById(publicCh.id()));
            System.out.println("find private = " + channelService.findChannelById(privateCh.id()));

            System.out.println("\n===== CHANNEL: findAllByUserId (u1 기준) =====");
            List<ChannelResponse> u1Visible = channelService.findAllByUserId(u1Id);
            u1Visible.forEach(ch -> System.out.println("u1 sees: " + ch));

            System.out.println("\n===== CHANNEL: join/leave (public 채널) =====");
            channelService.joinChannel(u2Id, publicCh.id());
            System.out.println("u2 joined public. check again:");
            channelService.findAllByUserId(u2Id).forEach(ch -> System.out.println("u2 sees: " + ch));

            channelService.leaveChannel(u2Id, publicCh.id());
            System.out.println("u2 left public. check again:");
            channelService.findAllByUserId(u2Id).forEach(ch -> System.out.println("u2 sees: " + ch));

            System.out.println("\n===== CHANNEL: update (PUBLIC만 가능) =====");
            ChannelResponse publicUpdated = channelService.updateChannel(
                    new ChannelUpdateRequest(publicCh.id(), "general-renamed")
            );
            System.out.println("publicUpdated = " + publicUpdated);


            // MESSAGE

            System.out.println("\n===== 3) MESSAGE: create (첨부 2개 포함) =====");

            List<BinaryContentCreateRequest> attachments = List.of(
                    new BinaryContentCreateRequest(
                            "a.txt",
                            "text/plain",
                            "hello".getBytes(StandardCharsets.UTF_8)
                    ),
                    new BinaryContentCreateRequest(
                            "b.txt",
                            "text/plain",
                            "world".getBytes(StandardCharsets.UTF_8)
                    )
            );

            MessageResponse m1 = messageService.createMessage(
                    new MessageCreateRequest(
                            u1Id,
                            publicCh.id(),
                            "첫 번째 메시지 (첨부2개)",
                            attachments
                    )
            );
            System.out.println("m1 = " + m1);

            MessageResponse m2 = messageService.createMessage(
                    new MessageCreateRequest(
                            u2Id,
                            publicCh.id(),
                            "두 번째 메시지 (첨부없음)",
                            List.of()
                    )
            );
            System.out.println("m2 = " + m2);

            System.out.println("\n===== MESSAGE: findById =====");
            System.out.println("find m1 = " + messageService.findMessageResponseByID(m1.id()));

            System.out.println("\n===== MESSAGE: findAllByChannelId (public) =====");
            List<MessageResponse> publicMsgs = messageService.findAllByChannelId(publicCh.id());
            publicMsgs.forEach(msg -> System.out.println("public msg: " + msg));

            System.out.println("\n===== MESSAGE: update =====");
            MessageResponse m1Updated = messageService.updateMessage(
                    new MessageUpdateRequest(m1.id(), "첫 번째 메시지 (수정됨)")
            );
            System.out.println("m1Updated = " + m1Updated);

            System.out.println("\n===== CHANNEL lastMessageAt 갱신 확인 =====");
            System.out.println("public channel again = " + channelService.findChannelById(publicCh.id()));

            System.out.println("\n===== MESSAGE: delete (첨부 BinaryContent도 같이 삭제돼야 함) =====");
            messageService.deleteMessage(m1.id());
            System.out.println("deleted m1. remaining messages:");
            messageService.findAllByChannelId(publicCh.id()).forEach(System.out::println);

            // ---------------------------
            // 4) DELETE CASCADE TEST
            // ---------------------------
            System.out.println("\n===== 4) CHANNEL delete (Message + ReadStatus 같이 삭제) =====");
            channelService.deleteChannel(privateCh.id());
            System.out.println("deleted private channel. u1 visible channels:");
            channelService.findAllByUserId(u1Id).forEach(System.out::println);

            System.out.println("\n===== USER delete (참여 채널 퇴장 + 메시지 정리 + profile/userStatus 정리) =====");
            userService.deleteUser(u2Id);
            System.out.println("deleted u2. all users:");
            System.out.println(userService.getUserAll());


            System.out.println(" TEST END ");

        };
    }
}


