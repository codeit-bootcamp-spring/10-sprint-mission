package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
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
    public ServiceType type;

    public ServiceFactory(ServiceType type) {
        this.type = type;
    }

    public ChannelService channelService() {
        if (channelService == null) {
            switch (type) {
                case JCF:
                    channelService = JCFChannelService.getInstance();
                    break;
                case FILE:
                    channelService = FileChannelService.getInstance();
                    break;

            }
        }
        return channelService;
    }

    public MessageService messageService() {
        if (messageService == null) {
            switch (type) {
                case JCF:
                    messageService = JCFMessageService.getInstance();
                    break;
                case FILE:
                    messageService = FileMessageService.getInstance();
                    break;

            }
        }
        return messageService;
    }

    public UserService userService() {
        if (userService == null) {
            switch (type) {
                case JCF:
                    userService = JCFUserService.getInstance();
                    break;
                case FILE:
                    userService = FileUserService.getInstance();
                    break;

            }
        }
        return userService;
    }
}
