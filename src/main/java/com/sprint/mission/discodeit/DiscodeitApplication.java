package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.MessageDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.validation.annotation.Validated;

@Transactional
@Validated
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class DiscodeitApplication {
    static User setupUser(UserService userService) {
        UserDto.CreateRequest request = new UserDto.CreateRequest("woody", "woody@codeit.com", "woody1234", null);
        return userService.create(request);
    }

    static Channel setupChannel(ChannelService channelService) {
        ChannelDto.CreatePublicRequest request = new ChannelDto.CreatePublicRequest("공지", "공지 채널입니다.");
        return channelService.create(request);
    }

    static void messageCreateTest(MessageService messageService, Channel channel, User author) {
        MessageDto.CreateRequest request = new MessageDto.CreateRequest("안녕하세요.", author.getId(), channel.getId(), null);
        Message message = messageService.create(request);
        System.out.println("메시지 생성: " + message.getId());
    }

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
        // 서비스 초기화
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 셋업
        User user = setupUser(userService);
        Channel channel = setupChannel(channelService);
        // 테스트
        messageCreateTest(messageService, channel, user);
        System.out.println("test");
    }
}
