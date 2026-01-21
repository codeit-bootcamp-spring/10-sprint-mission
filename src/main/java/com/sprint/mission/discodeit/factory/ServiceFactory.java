package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;

/**
 * service 클래스들의 인스턴스화를 담당하는 팩토리
 */
public class ServiceFactory {
    private static UserService userService;
    private static ChannelService channelService;
    private static MessageService messageService;
    public ServiceType type;

    public ServiceFactory(ServiceType type) {
        this.type = type;
    }

    public ChannelService channelService() {
        if (channelService == null) {
            channelService = BasicChannelService.getInstance(type);
        }
        return channelService;
    }

    public MessageService messageService() {
        if (messageService == null) {
            messageService = BasicMessageService.getInstance(type);
        }
        return messageService;
    }

    public UserService userService() {
        if (userService == null) {
            userService = BasicUserService.getInstance(type);
        }
        return userService;
    }
}
