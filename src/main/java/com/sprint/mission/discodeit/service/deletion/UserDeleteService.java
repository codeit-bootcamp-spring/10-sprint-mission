package com.sprint.mission.discodeit.service.deletion;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserDeleteService {
    private final UserService userService;
    private final ChannelService channelService;
    private final MessageService messageService;

    // 유저 삭제 메소드
    public void deleteUser(UUID userId) {
        // userId null 검증 및 유저 존재 확인
        userService.findUserById(userId);

        // 삭제하려는 유저가 owner인 채널이 있는지 확인(owner인 채널이 있으면 유저 삭제 불가)
        boolean isChannelOwner = channelService.findAllByUserId(userId).stream()
                .anyMatch(cr -> cr.ownerId().equals(userId));
        if (isChannelOwner) {
            throw new IllegalStateException("현재 owner인 channel이 존재합니다. 먼저 채널을 변경하세요.");
        }

        // 삭제하려는 user의 모든 메세지 삭제
        for (Message message : messageService.findUserMessagesByUserId(userId)) {
            messageService.deleteMessage(userId, message.getId());
        }
//        System.out.println("해당 user 메세지 삭제 완료");

        for (UUID channelId : channelService.findJoinChannelsByUserId(userId)) {
            channelService.leaveChannel(userId, channelId);
        }
//        System.out.println("성공: 해당 user가 참여한 채널 나가기");

        // user 삭제
        userService.deleteUser(userId);
//        System.out.println("해당 user 삭제 완료");
    }
}
