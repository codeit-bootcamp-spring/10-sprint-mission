package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
    static UserDto.Response setupUser(UserService userService) {
        UserDto.Response userResponse = userService.create(
                new UserDto.Create(
                        "woody",
                        "woody@codeit.com",
                        "woody1234",
                        null
                )
        );
        return userResponse;
    }

    static ChannelDto.Response setupChannel(ChannelService channelService, UUID creatorID) {
        ChannelDto.Response channelResponse = channelService.createPublic(
                creatorID,
                new ChannelDto.CreatePublic(
                        "공지",
                        "공지 채널입니다."
                )
        );
        return channelResponse;
    }

    static void messageCreateTest(MessageService messageService, UUID authorId, UUID channelId) {
        MessageDto.Response msgResponse = messageService.create(
                new MessageDto.Create(
                        "안녕하세요",
                        authorId,
                        channelId,
                        List.of()
                )
        );
        System.out.println("메시지 ID: " + msgResponse.id());
        System.out.println("작성자: " + msgResponse.authorId());
        System.out.println("작성 채널: " + msgResponse.channelId());
        System.out.println("내용: " + msgResponse.content());
        System.out.println("작성 시간: " + msgResponse.createAt());
    }

    public static void main(String[] args) {
        ApplicationContext ac = SpringApplication.run(DiscodeitApplication.class, args);

        Arrays.stream(ac.getBeanNamesForAnnotation(Repository.class))
                .forEach(System.out::println);

        UserService userService = ac.getBean(BasicUserService.class);
        ChannelService channelService = ac.getBean(BasicChannelService.class);
        MessageService messageService = ac.getBean(BasicMessageService.class);

        UserDto.Response userResponse = setupUser(userService);
        UUID userId = userResponse.id();

        ChannelDto.Response channelResponse = setupChannel(channelService, userId);
        UUID channelId = channelResponse.id();

        messageCreateTest(messageService, userId, channelId);

    }
}
