package com.sprint.mission.discodeit.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageCreateRequestDTO;
import com.sprint.mission.discodeit.dto.messagedto.MessageDto;
import com.sprint.mission.discodeit.dto.messagedto.MessageUpdateRequestDto;
import com.sprint.mission.discodeit.dto.userdto.UserCreateRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class MessageIntegrationTest {

    @Autowired
    MessageService messageService;

    @Autowired
    UserService userService;

    @Autowired
    ChannelService channelService;

    @Autowired
    MessageRepository messageRepository;

    @Test
    @DisplayName("메시지 생성 동작")
    @Transactional
    void message_create_fully_works() {
        // given
        // 메시지를 발행할 유저 생성
        UserDto user = userService.create(
            new UserCreateRequestDTO("a", "a@a.com", "pw"), null);
        // 채널 생성
        ChannelDto channel = channelService.createPublicChannel(
            new PublicChannelCreateDTO("general", "desc"));

        // 메시지 생성 요청 dto
        MessageCreateRequestDTO req = new MessageCreateRequestDTO("hello", channel.id(), user.id());

        // when
        MessageDto saved = messageService.create(null, req);

        // then
        Message message = messageRepository.findById(saved.id())
            .orElseThrow(); // 메시지 레포지토리 내에서 해당 메시지 존재 검증
        // 메시지의 내용이 일치한 지 검증
        assertThat(saved.content()).isEqualTo(req.content());
        assertThat(saved.channelId()).isEqualTo(channel.id());
        assertThat(saved.author().id()).isEqualTo(user.id());
        assertThat(message.getContent()).isEqualTo(req.content());
    }

    @Test
    @DisplayName("메시지 수정 동작")
    @Transactional
    void message_update_fully_works() {
        // given
        UserDto user = userService.create(
            new UserCreateRequestDTO("a", "a@a.com:", "pw"),
            null);
        ChannelDto channel = channelService.createPublicChannel(
            new PublicChannelCreateDTO("general", "desc"));

        MessageDto created = messageService.create(null,
            new MessageCreateRequestDTO("old-content", channel.id(), user.id()));
        MessageUpdateRequestDto req = new MessageUpdateRequestDto("new-content");

        // when
        MessageDto updated = messageService.update(created.id(), req);

        // then
        Message message = messageRepository.findById(created.id()).orElseThrow();
        assertThat(updated.content()).isEqualTo(req.newContent());
        assertThat(message.getContent()).isEqualTo(req.newContent());
    }

    @Test
    @DisplayName("메세지 삭제 동작")
    @Transactional
    void message_delete_fully_works() {
        // given
        UserDto user = userService.create(
            new UserCreateRequestDTO("author", "author" + "@test.com", "pw"),
            null);
        ChannelDto channel = channelService.createPublicChannel(
            new PublicChannelCreateDTO("general", "desc"));

        MessageDto created = messageService.create(null,
            new MessageCreateRequestDTO("to-delete", channel.id(), user.id()));

        // when
        messageService.delete(created.id());

        // then
        assertThat(messageRepository.findById(created.id())).isEmpty();
    }
}
