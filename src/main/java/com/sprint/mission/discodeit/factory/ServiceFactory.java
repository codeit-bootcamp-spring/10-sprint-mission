package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

/**
 * service 클래스들의 인스턴스화를 담당하는 팩토리
 */
public class ServiceFactory {
    private static UserService userService;
    private static ChannelService channelService;
    private static MessageService messageService;

    public static UserService userService() {
        if (userService == null) {
            userService = JCFUserService.getInstance();
        }
        return userService;
    }

    public static ChannelService channelService() {
        if (channelService == null) {
            channelService = JCFChannelService.getInstance();
        }
        return channelService;
    }

    public static MessageService messageService() {
        if (messageService == null) {
            messageService = JCFMessageService.getInstance();
        }
        return messageService;
    }
}
