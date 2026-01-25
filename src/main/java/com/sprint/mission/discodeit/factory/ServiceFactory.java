package com.sprint.mission.discodeit.factory;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
//import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
//import com.sprint.mission.discodeit.service.file.FileChannelService;
//import com.sprint.mission.discodeit.service.file.FileMessageService;
//import com.sprint.mission.discodeit.service.file.FileUserService;

public class ServiceFactory {
    private static final UserService userService;
    private static final ChannelService channelService;
    private static final MessageService messageService;

    static {
        // JCF로 테스트를 하고 싶을 때는 new JCF ...   (휘발성)
        // File로 테스트를 하고 싶을 때는 new File ... (영속성)
        UserRepository userRepository = new FileUserRepository();
        ChannelRepository channelRepository = new FileChannelRepository();
        MessageRepository messageRepository = new FileMessageRepository();

        BasicUserService u = new BasicUserService(userRepository);
        BasicChannelService c = new BasicChannelService(channelRepository, u);
        BasicMessageService m = new BasicMessageService(messageRepository, u, c);

        u.setChannelService(c);
        u.setMessageService(m); // 유저 삭제 시 메시지 청소를 위해 필요
        c.setMessageService(m); // 채널 삭제 시 메시지 청소를 위해 필요

        userService = u;
        channelService = c;
        messageService = m;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static ChannelService getChannelService() {
        return channelService;
    }

    public static MessageService getMessageService() {
        return messageService;
    }
}
