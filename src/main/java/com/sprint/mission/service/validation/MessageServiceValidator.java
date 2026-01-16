package com.sprint.mission.service.validation;

import com.sprint.mission.dto.SendMsgVerificationRequest;
import com.sprint.mission.service.BaseService;
import com.sprint.mission.service.ChannelService;
import com.sprint.mission.service.UserService;

import java.util.UUID;

public class MessageServiceValidator implements ServiceValidator<SendMsgVerificationRequest> {
    private final UserService userService;
    private final ChannelService channelService;

    public MessageServiceValidator(UserService userService, ChannelService channelService) {
        this.userService = userService;
        this.channelService = channelService;
    }

    @Override
    public void validateIdExist(SendMsgVerificationRequest request) {
        check(userService, request.getUserId(), "User");
        check(channelService, request.getChannelId(), "Channel");
    }

    private void check(BaseService<?> service, UUID id, String entity) {
        if (!service.hasId(id)) {
            throw new IllegalArgumentException(String.format("%s id don't exist, %s", entity, id));
        }
    }
}
