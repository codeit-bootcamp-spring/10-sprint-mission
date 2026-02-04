package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.channel.ChannelResponse;
import com.sprint.mission.discodeit.dto.channel.PublicChannelCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.message.MessageResponse;
import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {
    static UserResponse setupUser(UserService userService) {
        UserCreateRequest request = new UserCreateRequest(
                "woody",
                "woddy@codeit.com",
                "woody1234",
                null // 프로필 이미지 없음
        );
        return userService.create(request);
    }

    static ChannelResponse setupChannel(ChannelService channerService) {
        PublicChannelCreateRequest request = new PublicChannelCreateRequest(
                "공지",
                "공지 채널입니다."
        );
        return channerService.createPublic(request);
    }

    static void messageCreateTest(MessageService messageService, ChannelResponse channel, UserResponse author) {
        MessageCreateRequest request = new MessageCreateRequest(
                "안녕하세요. 첫 메시지입니다.",
                channel.id(),
                author.id(),
                null // 첨부파일 없음
        );

        MessageResponse message = messageService.create(request);
        System.out.println("메시지 생성 성공. id: " + message.id() + ", content: " + message.content());
    }


    public static void main(String[] args) {
        // 스프링 컨테이너 실행
        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // Bean 가져오기
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        UserResponse user = setupUser(userService);
        ChannelResponse channel = setupChannel(channelService);

        messageCreateTest(messageService, channel, user);
    }
}