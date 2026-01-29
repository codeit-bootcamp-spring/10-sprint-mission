package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;

public class JavaApplication {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);

        // 2. Context에서 Service 빈(Bean) 가져오기 (수동 생성 코드 대체)
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // 3. 테스트를 모아둔 DiscodeitApplication 빈 가져오기
        DiscodeitApplication app = context.getBean(DiscodeitApplication.class);

        // 4. 테스트 실행
        System.out.println("=== 정상 흐름 테스트 ===");
        app.runTest(userService, channelService, messageService);

        System.out.println("\n=== 유효성 검증 실패 테스트 ===");
        app.runValidationTest(userService, channelService, messageService);
    }
}
