package com.sprint.mission;

import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.repository.file.FileChannelRepository;
import com.sprint.mission.discodeit.repository.file.FileMessageRepository;
import com.sprint.mission.discodeit.repository.file.FileUserRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFChannelRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFMessageRepository;
import com.sprint.mission.discodeit.repository.jcf.JCFUserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.basic.BasicChannelService;
import com.sprint.mission.discodeit.service.basic.BasicMessageService;
import com.sprint.mission.discodeit.service.basic.BasicUserService;
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
    private String mode;
    public Factory(String mode) {
        mode = mode.toLowerCase();
        this.mode = mode;

        switch (mode) {
            case "jcf" -> {
                this.userService = new JCFUserService();
                this.channelService = new JCFChannelService();
                this.messageService = new JCFMessageService();
            }

            case "file" -> {
                this.userService = new FileUserService();
                this.channelService = new FileChannelService();
                this.messageService = new FileMessageService();
            }
            case "basic-jcf" -> {
                ChannelRepository channelRepository = new JCFChannelRepository();
                MessageRepository messageRepository = new JCFMessageRepository();
                UserRepository userRepository = new JCFUserRepository();

                this.userService = new BasicUserService(userRepository, channelRepository, messageRepository);
                this.channelService = new BasicChannelService(channelRepository, userRepository, messageRepository);
                this.messageService = new BasicMessageService(messageRepository, userRepository, channelRepository);
            }
            case "basic-file" -> {
                ChannelRepository channelRepository = new FileChannelRepository();
                MessageRepository messageRepository = new FileMessageRepository();
                UserRepository userRepository = new FileUserRepository();

                this.userService = new BasicUserService(userRepository, channelRepository, messageRepository);
                this.channelService = new BasicChannelService(channelRepository, userRepository, messageRepository);
                this.messageService = new BasicMessageService(messageRepository, userRepository, channelRepository);
            }

            default -> throw new IllegalArgumentException("Invalid mode: " + mode);
        }

        // 각 서비스 주입
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

    public String getMode() {
        return mode;
    }
}
