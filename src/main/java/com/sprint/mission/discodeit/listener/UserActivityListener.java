package com.sprint.mission.discodeit.listener;

import com.sprint.mission.discodeit.event.MessageSentEvent;
import com.sprint.mission.discodeit.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserActivityListener {
    private final UserStatusService userStatusService;

    @EventListener
    public void handleMessageSent(MessageSentEvent event) {
        userStatusService.updateByUserId(event.authorId());
    }
}
