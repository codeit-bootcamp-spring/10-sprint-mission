package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sprint.mission.discodeit.dto.channeldto.ChannelDto;
import com.sprint.mission.discodeit.dto.channeldto.PrivateChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelCreateDTO;
import com.sprint.mission.discodeit.dto.channeldto.PublicChannelUpdateRequestDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.channel.ChannelNameDuplicationException;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class BasicChannelServiceTest {

    @Mock
    ChannelRepository channelRepository;
    @Mock
    ReadStatusRepository readStatusRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    MessageRepository messageRepository;
    @Mock
    ChannelMapper channelMapper;

    @InjectMocks
    BasicChannelService channelService;

    @Nested
    public class public_channel_create_test {

        @Test
        @DisplayName("공개 채널 생성 성공")
        void public_channel_create_success() {
            // given
            String name = "Name";
            String description = "Description";
            PublicChannelCreateDTO req = new PublicChannelCreateDTO(name, description);

            when(channelRepository.existsByName(any(String.class))).thenReturn(false);
            Channel channel = new Channel(ChannelType.PUBLIC, name, description);
            channel.setId(UUID.randomUUID());

            when(channelRepository.save(any(Channel.class)))
                .thenReturn(channel);

            ChannelDto expected = new ChannelDto(channel.getId(), ChannelType.PUBLIC, name,
                description, List.of(), null);
            when(channelMapper.toDto(any(Channel.class), isNull())).thenReturn(expected);

            // when
            ChannelDto result = channelService.createPublicChannel(req);

            assertEquals(expected, result);
            assertEquals(name, result.name());
            assertEquals(description, result.description());
            // then
            verify(channelRepository).existsByName(any(String.class));
            verify(channelRepository).save(any(Channel.class));
            verify(channelMapper).toDto(any(Channel.class), isNull());
        }

        @Test
        @DisplayName("공개 채널 생성 실패 (중복된 이름)")
        void public_channel_create_fail_duplicate_name() {
            // given
            String name = "publicChannel";
            String description1 = "channel1";
            String description2 = "channel2";

            PublicChannelCreateDTO req1 = new PublicChannelCreateDTO(name, description1);
            PublicChannelCreateDTO req2 = new PublicChannelCreateDTO(name, description2);

            when(channelRepository.existsByName(name)).thenReturn(false, true);
            when(channelRepository.save(any(Channel.class))).thenReturn(
                new Channel(ChannelType.PUBLIC, name, description1));
            channelService.createPublicChannel(req1);

            // when + then
            assertThrows(ChannelNameDuplicationException.class,
                () -> channelService.createPublicChannel(req2));
            verify(channelRepository, times(2)).existsByName(name);
            verify(channelRepository, times(1)).save(any(Channel.class));


        }
    }

    @Nested
    public class private_channel_create_test {

        @Test
        @DisplayName("개인 채널 생성 성공")
        void private_channel_create_success() {
            // given

            // participants 리스트에 들어갈 유저의 ID
            UUID userId1 = UUID.randomUUID();
            UUID userId2 = UUID.randomUUID();

            // 사설 채널 생성 요청
            PrivateChannelCreateDTO req = new PrivateChannelCreateDTO(List.of(userId1, userId2));

            // Participants 리스트에 들어갈 유저들 생성
            User user1 = new User("user1", "u1@b.com", "abc", null);
            User user2 = new User("user2", "u2@b.com", "def", null);

            // userRepository의 findById 호출 시 Optional에 래핑된 해당 유저 객체들을 반환함.
            when(userRepository.findById(userId1)).thenReturn(Optional.of(user1));
            when(userRepository.findById(userId2)).thenReturn(Optional.of(user2));

            // 사설 채널 객체 생성.
            Channel saved = new Channel(ChannelType.PRIVATE, "private-", "private channel");
            saved.setId(UUID.randomUUID()); // 랜덤으로 id 지정

            // channelRepository의 save 영속화 메소드 호출 시 saved 채널 반환
            when(channelRepository.save(any(Channel.class))).thenReturn(saved);
            // ReadStatus를 최신화 한 채널을 영속화하는 메서드를 호출. Optional에 래핑된 saved 채널 반환
            when(channelRepository.findWithParticipantsById(saved.getId())).thenReturn(
                Optional.of(saved));

            // 테스트에서 기대하는 expected 값
            ChannelDto expected = new ChannelDto(saved.getId(), ChannelType.PRIVATE,
                saved.getName(), saved.getDescription(), List.of(), null);
            // ChannelMapper의 toDto 메서드는 expected 값을 반환
            when(channelMapper.toDto(any(Channel.class), isNull())).thenReturn(expected);

            // when
            ChannelDto result = channelService.createPrivateChannel(req);

            // then
            assertNotNull(result); // 결과가 null이 아닌지?
            assertEquals(ChannelType.PRIVATE, result.type()); // 생성된 채널의 타입이 Private인지

            ArgumentCaptor<Channel> captor = ArgumentCaptor.forClass(Channel.class);
            verify(channelRepository).save(
                captor.capture()); // Captor는 save에 파라미터로 들어가는 Channel.class를 캡쳐한다.
            assertEquals(ChannelType.PRIVATE,
                captor.getValue().getType()); // getValue로 해당 채널에 대한 접근이 가능하다.

            verify(userRepository).findById(userId1);
            verify(userRepository).findById(userId2);
            verify(readStatusRepository, times(2)).save(any(ReadStatus.class));
            verify(channelMapper).toDto(any(Channel.class), isNull());
        }

        @Test
        @DisplayName("개인 채널 생성 실패 (대상자 없음)")
        void private_channel_create_fail_empty_participants() {

        }
    }

    @Nested
    public class channel_find_test {

        @Test
        @DisplayName("채널 단건 조회 성공")
        void find_channel_success() {
            // given
            UUID channelId = UUID.randomUUID();
            Channel existing = new Channel(ChannelType.PUBLIC, "general", "desc");
            existing.setId(channelId);

            Message lastMessage = null;
            ChannelDto expected = new ChannelDto(channelId, ChannelType.PUBLIC, "general", "desc",
                List.of(), null);

            when(channelRepository.findById(channelId)).thenReturn(Optional.of(existing));
            when(messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channelId)).thenReturn(
                lastMessage);
            when(channelMapper.toDto(existing, lastMessage)).thenReturn(expected);

            // when
            ChannelDto result = channelService.find(channelId);

            // then
            assertNotNull(result);
            assertEquals(expected, result);
            verify(channelRepository).findById(channelId);
            verify(messageRepository).findTopByChannelIdOrderByCreatedAtDesc(channelId);
            verify(channelMapper).toDto(existing, lastMessage);
        }

        @Test
        @DisplayName("채널 단건 조회 실패 (존재하지 않는 ID)")
        void find_channel_fail_not_found() {
            // given
            UUID channelId = UUID.randomUUID();
            when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

            // when + then
            assertThrows(ChannelNotFoundException.class, () -> channelService.find(channelId));
            verify(channelRepository).findById(channelId);
            verify(messageRepository, never()).findTopByChannelIdOrderByCreatedAtDesc(
                any(UUID.class));
            verify(channelMapper, never()).toDto(any(Channel.class), any(Message.class));
        }
    }

    @Nested
    public class channel_update_test {

        @Test
        @DisplayName("공개 채널 업데이트 성공")
        void update_channel_success() {
            // given
            UUID channelId = UUID.randomUUID();
            Channel existing = new Channel(ChannelType.PUBLIC, "old-name", "old-desc");
            existing.setId(channelId);

            PublicChannelUpdateRequestDTO req = new PublicChannelUpdateRequestDTO("new-name",
                "new-desc");
            Message lastMessage = null;
            ChannelDto expected = new ChannelDto(channelId, ChannelType.PUBLIC, "new-name",
                "new-desc", List.of(), null);

            when(channelRepository.findById(channelId)).thenReturn(Optional.of(existing));
            when(messageRepository.findTopByChannelIdOrderByCreatedAtDesc(channelId)).thenReturn(
                lastMessage);
            when(channelMapper.toDto(existing, lastMessage)).thenReturn(expected);

            // when
            ChannelDto result = channelService.update(channelId, req);

            // then
            assertNotNull(result);
            assertEquals("new-name", result.name());
            assertEquals("new-desc", result.description());
            verify(channelRepository).findById(channelId);
            verify(messageRepository).findTopByChannelIdOrderByCreatedAtDesc(channelId);
            verify(channelMapper).toDto(existing, lastMessage);
        }

        @Test
        @DisplayName("공개 채널 업데이트 실패 (존재하지 않는 ID)")
        void update_channel_fail_not_found() {
            // given
            UUID channelId = UUID.randomUUID();
            PublicChannelUpdateRequestDTO req = new PublicChannelUpdateRequestDTO("new-name",
                "new-desc");
            when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

            // when + then
            assertThrows(ChannelNotFoundException.class,
                () -> channelService.update(channelId, req));
            verify(channelRepository).findById(channelId);
            verify(messageRepository, never()).findTopByChannelIdOrderByCreatedAtDesc(
                any(UUID.class));
            verify(channelMapper, never()).toDto(any(Channel.class), any(Message.class));
        }
    }

    @Nested
    public class channel_delete_test {

        @Test
        @DisplayName("채널 삭제 성공")
        void delete_channel_success() {
            // given
            UUID channelId = UUID.randomUUID();
            Channel existing = new Channel(ChannelType.PUBLIC, "general", "desc");
            existing.setId(channelId);
            when(channelRepository.findById(channelId)).thenReturn(Optional.of(existing));

            // when + then
            assertDoesNotThrow(() -> channelService.delete(channelId));
            verify(channelRepository).findById(channelId);
            verify(channelRepository).delete(existing);
        }

        @Test
        @DisplayName("채널 삭제 실패 (존재하지 않는 ID)")
        void delete_channel_fail_not_found() {
            // given
            UUID channelId = UUID.randomUUID();
            when(channelRepository.findById(channelId)).thenReturn(Optional.empty());

            // when + then
            assertThrows(ChannelNotFoundException.class, () -> channelService.delete(channelId));
            verify(channelRepository).findById(channelId);
            verify(channelRepository, never()).delete(any(Channel.class));
        }
    }

}
