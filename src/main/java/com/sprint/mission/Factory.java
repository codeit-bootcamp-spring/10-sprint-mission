package com.sprint.mission;

import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.file.FileChannelService;
import com.sprint.mission.discodeit.service.file.FileMessageService;
import com.sprint.mission.discodeit.service.file.FileUserService;
import com.sprint.mission.discodeit.service.jcf.JCFChannelService;
import com.sprint.mission.discodeit.service.jcf.JCFMessageService;
import com.sprint.mission.discodeit.service.jcf.JCFUserService;

public class Factory {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    public Factory() {
        this.userService = new FileUserService();
        this.channelService = new FileChannelService();
        this.messageService = new FileMessageService();

        // 각 서비스 주입 ?
        userService.setMessageService(messageService);
        userService.setChannelService(channelService);

        channelService.setMessageService(messageService);
        channelService.setUserService(userService);

        messageService.setChannelService(channelService);
        messageService.setUserService(userService);
    }
    // Getter
    public UserService getUserService() {
        return userService;
    }

    public ChannelService getChannelService() {
        return channelService;
    }

    public MessageService getMessageService() {
        return messageService;
    }
}
