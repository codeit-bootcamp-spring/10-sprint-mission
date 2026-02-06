package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.request.AttachmentCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.PublicCreateRequestDTO;
import com.sprint.mission.discodeit.dto.request.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.response.ChannelSummaryResponseDTO;
import com.sprint.mission.discodeit.dto.response.MessageResponseDTO;
import com.sprint.mission.discodeit.dto.response.UserSummaryResponseDTO;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootApplication
public class DiscodeitApplication {
    static UserSummaryResponseDTO setupUser(UserService userService) {
        UserCreateRequestDTO userCreateRequestDTO =
                new UserCreateRequestDTO("woody", "woody@codeit.com", "woody1234", null);
        return userService.create(userCreateRequestDTO);
    }

    static ChannelSummaryResponseDTO setupPrivateChannel(ChannelService channelService) {
        PublicCreateRequestDTO publicCreateRequestDTO = new PublicCreateRequestDTO(
                "공지",
                "공지 채널입니다."
        );
        return channelService.create(publicCreateRequestDTO);
    }

    static void messageCreateTest(MessageService messageService, UUID channelId, UUID authorId) {
        List<AttachmentCreateRequestDTO> attachments = new ArrayList<>();
        MessageCreateRequestDTO messageCreateRequestDTO = new MessageCreateRequestDTO(
                "안녕하세요.",
                channelId,
                authorId,
                attachments
        );
        MessageResponseDTO messageResponseDTO =
                messageService.create(messageCreateRequestDTO);
        System.out.println("메시지 생성: " + messageResponseDTO.messageId());
    }

    public static void main(String[] args) {
        ApplicationContext context =
                SpringApplication.run(DiscodeitApplication.class, args);

        //javaApplication의 main메소드에서 Service를 초기화하는 코드를 SpringContext를 활용해 대체
        UserService userService = context.getBean(UserService.class);
        ChannelService channelService = context.getBean(ChannelService.class);
        MessageService messageService = context.getBean(MessageService.class);

        // javaApplication의 main메소드의 셋업, 테스트 부분의 코드 복사
        // 셋업
        UserSummaryResponseDTO userSummaryResponseDTO = setupUser(userService);
        ChannelSummaryResponseDTO channelSummaryResponseDTO = setupPrivateChannel(channelService);
        // 테스트
        UUID userId = userSummaryResponseDTO.userId();
        UUID channelId = channelSummaryResponseDTO.channelId();
        messageCreateTest(messageService, channelId, userId);
    }

}
