package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class DiscodeitApplication {

    public static void main(String[] args) {


        ConfigurableApplicationContext context = SpringApplication.run(DiscodeitApplication.class, args);
//        UserService userService = context.getBean(UserService.class);
        BasicUserService basicUserService = context.getBean(BasicUserService.class);
        UserService userService =  context.getBean(UserService.class);
        ChannelService channelService;
        MessageService messageService;





    }

}
