package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageCreateRequest;
import com.sprint.mission.discodeit.dto.request.MessageUpdateRequest;
import com.sprint.mission.discodeit.entity.*;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.message.MessageNotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.BinaryContentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class BasicMessgeServiceTest {
    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ChannelRepository channelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BinaryContentService binaryContentService;

    @InjectMocks
    private BasicMessageService messageService;

    // CREATE
    @Test
    void create_success_without_attachments() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request =
                new MessageCreateRequest("hello", channelId, authorId);
        Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
        User author = mock(User.class);
        Message savedMessage = new Message("hello", channel, author, Collections.emptyList());

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);

        // when
        Message result = messageService.create(request, null);

        // then
        assertEquals("hello", result.getContent());
        assertEquals(channel, result.getChannel());
        assertEquals(author, result.getAuthor());
        assertTrue(result.getAttachments().isEmpty());

        then(channelRepository).should().findById(channelId);
        then(userRepository).should().findById(authorId);
        then(messageRepository).should().save(any(Message.class));
        then(binaryContentService).should(never()).create(any());
    }

    @Test
    void create_success_with_attachments() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request =
                new MessageCreateRequest("hello", channelId, authorId);

        BinaryContentCreateRequest attachmentRequest1 = mock(BinaryContentCreateRequest.class);
        BinaryContentCreateRequest attachmentRequest2 = mock(BinaryContentCreateRequest.class);

        BinaryContent attachment1 = mock(BinaryContent.class);
        BinaryContent attachment2 = mock(BinaryContent.class);

        Channel channel = new Channel(ChannelType.PUBLIC, "general", "desc");
        User author = mock(User.class);

        Message savedMessage = new Message(
                "hello",
                channel,
                author,
                List.of(attachment1, attachment2)
        );

        given(channelRepository.findById(channelId)).willReturn(Optional.of(channel));
        given(userRepository.findById(authorId)).willReturn(Optional.of(author));
        given(binaryContentService.create(attachmentRequest1)).willReturn(attachment1);
        given(binaryContentService.create(attachmentRequest2)).willReturn(attachment2);
        given(messageRepository.save(any(Message.class))).willReturn(savedMessage);

        // when
        Message result = messageService.create(
                request,
                List.of(attachmentRequest1, attachmentRequest2)
        );

        // then
        assertEquals("hello", result.getContent());
        assertEquals(2, result.getAttachments().size());

        then(binaryContentService).should().create(attachmentRequest1);
        then(binaryContentService).should().create(attachmentRequest2);
        then(messageRepository).should().save(any(Message.class));
    }

    @Test
    void create_fail_channel_not_found() {
        // given
        UUID channelId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();

        MessageCreateRequest request =
                new MessageCreateRequest("hello", channelId, authorId);

        given(channelRepository.findById(channelId)).willReturn(Optional.empty());

        // when & then
        assertThrows(ChannelNotFoundException.class,
                () -> messageService.create(request, null));

        then(channelRepository).should().findById(channelId);
        then(userRepository).shouldHaveNoInteractions();
        then(messageRepository).shouldHaveNoInteractions();
    }

    // FIND
    @Test
    void findAllByChannelId_with_cursor() {
        // given
        UUID channelId = UUID.randomUUID();
        Instant cursor = Instant.now();
        List<Message> messages = List.of(mock(Message.class));

        given(messageRepository.findByChannel_IdAndCreatedAtBeforeOrderByCreatedAtDesc(
                eq(channelId), eq(cursor), any(Pageable.class)))
                .willReturn(messages);

        // when
        List<Message> result = messageService.findAllByChannel_Id(channelId, cursor, 10);

        // then
        assertEquals(1, result.size());
        then(messageRepository).should()
                .findByChannel_IdAndCreatedAtBeforeOrderByCreatedAtDesc(
                        eq(channelId), eq(cursor), any(Pageable.class));
        then(messageRepository).should(never())
                .findByChannel_IdOrderByCreatedAtDesc(eq(channelId), any(Pageable.class));
    }


    // UPDATE
    @Test
    void update_success() {
        // given
        UUID messageId = UUID.randomUUID();
        Message message = new Message(
                "old content",
                new Channel(ChannelType.PUBLIC, "general", "desc"),
                mock(User.class),
                Collections.emptyList()
        );

        MessageUpdateRequest request = new MessageUpdateRequest("new content");

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        // when
        Message updated = messageService.update(messageId, request);

        // then
        assertEquals("new content", updated.getContent());
        then(messageRepository).should().findById(messageId);
    }

    @Test
    void update_fail_not_found() {
        // given
        UUID messageId = UUID.randomUUID();
        MessageUpdateRequest request = new MessageUpdateRequest("new content");

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MessageNotFoundException.class,
                () -> messageService.update(messageId, request));

        then(messageRepository).should().findById(messageId);
    }

    // DELETE
    @Test
    void delete_success() {
        // given
        UUID messageId = UUID.randomUUID();
        Message message = mock(Message.class);

        given(messageRepository.findById(messageId)).willReturn(Optional.of(message));

        // when
        messageService.delete(messageId);

        // then
        then(messageRepository).should().findById(messageId);
        then(messageRepository).should().delete(message);
    }

    @Test
    void delete_fail_not_found() {
        // given
        UUID messageId = UUID.randomUUID();

        given(messageRepository.findById(messageId)).willReturn(Optional.empty());

        // when & then
        assertThrows(MessageNotFoundException.class,
                () -> messageService.delete(messageId));

        then(messageRepository).should().findById(messageId);
        then(messageRepository).should(never()).delete(any(Message.class));
    }
}
