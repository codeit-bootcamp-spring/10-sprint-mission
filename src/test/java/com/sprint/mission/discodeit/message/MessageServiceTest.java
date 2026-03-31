package com.sprint.mission.discodeit.message;

import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {
    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void messageCreatePassTest() {
        // given

        // when

        // then
    }

    @Test
    void messageCreateFailTest() {
        // given

        // when

        // then
    }

    @Test
    void messageUpdatePassTest() {
        // given

        // when

        // then
    }

    @Test
    void messageUpdateFailTest() {
        // given

        // when

        // then
    }

    @Test
    void messageDeletePassTest() {
        // given

        // when

        // then
    }

    @Test
    void messageDeleteFailTest() {
        // given

        // when

        // then
    }

    @Test
    void messageFindByUserIdPassTest() {
        // given

        // when

        // then
    }

    @Test
    void messageFindByUserIdFailTest() {
        // given

        // when

        // then
    }
}
