package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.readstatus.CreateReadStatusRequest;
import com.sprint.mission.discodeit.dto.readstatus.ReadStatusResponse;
import com.sprint.mission.discodeit.dto.readstatus.UpdateReadStatusRequest;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.ReadStatusType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.utils.FileIOHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@SpringBootTest
public class BasicReadStatusServiceTest {

    @Autowired
    private BasicReadStatusService readStatusService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChannelRepository channelRepository;

    @Autowired
    private ReadStatusRepository readStatusRepository;

    @BeforeEach
    void setUp() {
        FileIOHelper.flushData();
    }

    @Test
    @DisplayName("ReadStatus 생성 성공")
    void testCreateReadStatus() {
        // given
        User user = new User("user1", "1234", "user1@test.com");
        userRepository.save(user);

        Channel channel = Channel.buildPublic("test", "desc");
        channelRepository.save(channel);

        CreateReadStatusRequest request = new CreateReadStatusRequest(
                user.getId(),
                channel.getId()
        );

        // when
        UUID readStatusId = readStatusService.createReadStatus(request);

        // then
        ReadStatus readStatus = readStatusRepository.findById(readStatusId).orElseThrow();

        assertThat(readStatus.getUserId()).isEqualTo(user.getId());
        assertThat(readStatus.getChannelId()).isEqualTo(channel.getId());
        assertThat(readStatus.getReadStatusType()).isEqualTo(ReadStatusType.UNREAD);
    }

    @Test
    @DisplayName("중복 ReadStatus 생성 실패")
    void testDuplicateReadStatusFail() {
        // given
        User user = new User("user6", "1234", "user6@test.com");
        userRepository.save(user);

        Channel channel = Channel.buildPublic("c6", "d6");
        channelRepository.save(channel);

        CreateReadStatusRequest request =
                new CreateReadStatusRequest(user.getId(), channel.getId());

        readStatusService.createReadStatus(request);

        // then
        assertThatIllegalArgumentException()
                .isThrownBy(() -> readStatusService.createReadStatus(request));
    }

    @Test
    @DisplayName("ReadStatus 단건 조회 성공")
    void testFindReadStatusById() {
        // given
        User user = new User("user2", "1234", "user2@test.com");
        userRepository.save(user);

        Channel channel = Channel.buildPublic("test2", "desc2");
        channelRepository.save(channel);

        ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
        readStatusRepository.save(readStatus);

        // when
        ReadStatusResponse response = readStatusService.findReadStatusByReadStatusId(readStatus.getId());

        // then
        assertThat(response.id()).isEqualTo(readStatus.getId());
        assertThat(response.readStatusType()).isEqualTo(ReadStatusType.UNREAD);
    }

    @Test
    @DisplayName("User 기준 ReadStatus 전체 조회")
    void testFindAllReadStatusesByUserId() {
        // given
        User user = new User("user3", "1234", "user3@test.com");
        userRepository.save(user);

        Channel ch1 = Channel.buildPublic("c1", "d1");
        Channel ch2 = Channel.buildPublic("c2", "d2");
        channelRepository.save(ch1);
        channelRepository.save(ch2);

        ReadStatus rs1 = new ReadStatus(user.getId(), ch1.getId());
        ReadStatus rs2 = new ReadStatus(user.getId(), ch2.getId());

        readStatusRepository.save(rs1);
        readStatusRepository.save(rs2);

        // when
        List<?> responses =
                readStatusService.findAllReadStatusesByUserId(user.getId());

        // then
        assertThat(responses).hasSize(2);
    }

    @Test
    @DisplayName("ReadStatus 상태 토글 성공")
    void testUpdateReadStatus() {
        // given
        User user = new User("user4", "1234", "user4@test.com");
        userRepository.save(user);

        Channel channel = Channel.buildPublic("c", "d");
        channelRepository.save(channel);

        ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
        readStatusRepository.save(readStatus);

        UpdateReadStatusRequest request =
                new UpdateReadStatusRequest(readStatus.getId());

        // when
        var response = readStatusService.updateReadStatus(request);

        // then
        assertThat(response.readStatusType()).isEqualTo(ReadStatusType.READ);
    }

    @Test
    @DisplayName("ReadStatus 삭제 성공")
    void testDeleteReadStatus() {
        // given
        User user = new User("user5", "1234", "user5@test.com");
        userRepository.save(user);

        Channel channel = Channel.buildPublic("c5", "d5");
        channelRepository.save(channel);

        ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
        readStatusRepository.save(readStatus);

        // when
        readStatusService.deleteReadStatus(readStatus.getId());

        // then
        assertThat(readStatusRepository.findById(readStatus.getId()))
                .isEmpty();
    }

}
