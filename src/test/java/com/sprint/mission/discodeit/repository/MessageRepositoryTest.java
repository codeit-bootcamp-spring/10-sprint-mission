package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.enums.ChannelType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@Transactional
class MessageRepositoryTest {
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    ChannelRepository channelRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    EntityManager em;

    @Test
    @DisplayName("채널Id로 조회 시 생성일 순으로 내림차순 정렬했을때 가장 먼저 오는 메시지 조회 반환 성공해야함")
    void findFirstByChannelIdOrderByCreatedAtDesc_success() throws InterruptedException{
        // given

        Channel channel = new Channel("채널","설명");
        channel.setType(ChannelType.PUBLIC);
        channelRepository.save(channel);
        User user = userRepository.save(new User("김현재", "fred@naver.com","123123"));

        Message message1 = messageRepository.save(new Message(user, "안녕하세요!", channel));
        Thread.sleep(50);
        Message message2 = messageRepository.save(new Message(user, "안녕!", channel));
        Thread.sleep(50);
        Message message3 = messageRepository.save(new Message(user, "하세요!", channel));
        Thread.sleep(50);

        // when
        Optional<Message> latestMessage = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channel.getId());
        // then

        assertThat(latestMessage).isPresent();
        assertThat(latestMessage.get().getContent()).isEqualTo("하세요!");
    }

    @Test
    @DisplayName("채널 id로 조회 시 생성일 순으로 내림차순 정렬했을때 메시지가 없어서 empty를 반환해야함")
    void findFirstByChannelIdOrderByCreatedAtDesc_empty() {
        // given
        Channel channel = new Channel("채널","설명");
        channel.setType(ChannelType.PUBLIC);
        channelRepository.save(channel);

        // when
        Optional<Message> latestMessage = messageRepository.findFirstByChannelIdOrderByCreatedAtDesc(channel.getId());

        // then
        assertThat(latestMessage).isEmpty();
    }

    @Test
    @DisplayName("특정 시간 이전의 메시지들을 최신순으로 커서를 통해 가져오는데 성공하지만 다음 메시지가 없으면 hasNext는 false 반환")
    void findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc_hasNext_false() throws InterruptedException {
        // given
        Channel channel = new Channel("채널","설명");
        channel.setType(ChannelType.PUBLIC);
        channelRepository.save(channel);
        User user = userRepository.save(new User("김현재", "fred@naver.com", "1234"));

        // given
        Message msg1 = messageRepository.save(new Message(user, "첫 번째 (가장 오래된)", channel));
        Message msg2 = messageRepository.save(new Message(user, "두 번째 (커서 기준점)", channel));
        Message msg3 = messageRepository.save(new Message(user, "세 번째 (가장 최신)", channel));

        em.flush();

        Instant now = Instant.now();
        em.createQuery("UPDATE Message m SET m.createdAt = :time WHERE m.id = :id")
                .setParameter("time", now.minusSeconds(100)).setParameter("id", msg1.getId()).executeUpdate();
        em.createQuery("UPDATE Message m SET m.createdAt = :time WHERE m.id = :id")
                .setParameter("time", now.minusSeconds(50)).setParameter("id", msg2.getId()).executeUpdate();


        em.flush();
        em.clear();

        Message dbMsg2 = messageRepository.findById(msg2.getId()).orElseThrow();
        Instant cursor = dbMsg2.getCreatedAt();
        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Slice<Message> result = messageRepository.findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(
                channel.getId(), cursor, pageRequest
        );

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("첫 번째 (가장 오래된)");

        assertThat(result.hasNext()).isFalse();
    }

    @Test
    @DisplayName("특정 시간 이전의 메시지들을 최신순으로 커서를 통해 가져오는데 성공하고 다음 메시지가 있으면 hasNext는 true 반환")
    void findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc_hasNext_true() throws InterruptedException{
        // given
        Channel channel = new Channel("채널","설명");
        channel.setType(ChannelType.PUBLIC);
        channelRepository.save(channel);
        User user = userRepository.save(new User("김현재", "fred@naver.com", "1234"));

        // given
        Message msg1 = messageRepository.save(new Message(user, "첫 번째", channel));
        Message msg2 = messageRepository.save(new Message(user, "두 번째", channel));
        Message msg3 = messageRepository.save(new Message(user, "세 번째", channel));
        Message msg4 = messageRepository.save(new Message(user, "네 번째", channel));

        em.flush();

        Instant now = Instant.now();
        em.createQuery("UPDATE Message m SET m.createdAt = :time WHERE m.id = :id")
                .setParameter("time", now.minusSeconds(100)).setParameter("id", msg1.getId()).executeUpdate();
        em.createQuery("UPDATE Message m SET m.createdAt = :time WHERE m.id = :id")
                .setParameter("time", now.minusSeconds(50)).setParameter("id", msg2.getId()).executeUpdate();
        em.createQuery("UPDATE Message m SET m.createdAt = :time WHERE m.id = :id")
                .setParameter("time", now.minusSeconds(10)).setParameter("id", msg3.getId()).executeUpdate();


        em.flush();
        em.clear();

        Message dbMsg4 = messageRepository.findById(msg4.getId()).orElseThrow();
        Instant cursor = dbMsg4.getCreatedAt();
        PageRequest pageRequest = PageRequest.of(0,2);

        // when
        Slice<Message> result = messageRepository.
                findByChannelIdAndCreatedAtLessThanOrderByCreatedAtDesc(channel.getId(), cursor, pageRequest);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("세 번째");
        assertThat(result.getContent().get(1).getContent()).isEqualTo("두 번째");
        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("채널 입장 시: 커서 없이 해당 채널의 최신 메시지들을 내림차순으로 가져와야 한다")
    void findByChannelIdOrderByCreatedAtDesc_success() throws InterruptedException {
        // given
        Channel channel = new Channel("채널","설명");
        channel.setType(ChannelType.PUBLIC);
        channelRepository.save(channel);
        User user = userRepository.save(new User("김현재", "fred@naver.com", "1234"));

        // given
        Message msg1 = messageRepository.save(new Message(user, "첫 번째 (가장 오래된)", channel));
        Thread.sleep(50);
        Message msg2 = messageRepository.save(new Message(user, "두 번째" , channel));
        Thread.sleep(50);
        Message msg3 = messageRepository.save(new Message(user, "세 번째 (커서 기준점)", channel));

        PageRequest pageRequest = PageRequest.of(0, 2);

        // when
        Slice<Message> result = messageRepository.findByChannelIdOrderByCreatedAtDesc(
                channel.getId(),pageRequest
        );

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getContent().get(0).getContent()).isEqualTo("세 번째 (커서 기준점)");
        assertThat(result.getContent().get(1).getContent()).isEqualTo("두 번째");

        assertThat(result.hasNext()).isTrue();
    }

    @Test
    @DisplayName("채널 객체로 조회를 하면 채널이 포함한 모든 메시지를 리스트 반환 성공해야함")
    void findAllByChannel_success() {
        // given
        Channel channel = new Channel("채널","설명");
        channel.setType(ChannelType.PUBLIC);
        channelRepository.save(channel);
        User user = userRepository.save(new User("김현재", "fred@naver.com","123123"));

        Message message1 = messageRepository.save(new Message(user, "메시지1",channel));
        Message message2 = messageRepository.save(new Message(user, "메시지2",channel));

        // when
        List<Message> result = messageRepository.findAllByChannel(channel);
        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting("content")
                .containsExactlyInAnyOrder("메시지1", "메시지2");
    }
}