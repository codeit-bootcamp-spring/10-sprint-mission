package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.NotFoundException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.helper.EntityFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final UserRepository userRepository;
    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;
    private final MessageRepository messageRepository;
    private final EntityFinder entityFinder;

    @Override
    public ChannelDto.ChannelResponsePublic createPublic(ChannelDto.ChannelRequest request) {
        // 단체톡방
        Objects.requireNonNull(request.name(), "채널 이름은 필수입니다.");
        Objects.requireNonNull(request.description(), "채널 설명은 필수입니다.");

        // 중복 검사
        channelRepository.findAll().stream()
                .filter(channel -> channel.getName().equals(request.name()))
                .findFirst().ifPresent(channel -> {
                    if (channel.getName().equals(request.name())) throw new IllegalArgumentException("이미 존재하는 이름입니다.");
                });


        Channel channel = new Channel(request);
        channelRepository.save(channel);
        return ChannelDto.ChannelResponsePublic.from(channel);
    }

    @Override
    public ChannelDto.ChannelResponsePrivate createPrivate(List<UUID> userIds) {
        // 익명톡방
        Objects.requireNonNull(userIds, "유효한 유저 목록을 입력해주세요.");
        List<User> users = userIds.stream().map(entityFinder::getUser).toList();

        Channel channel = new Channel(userIds);

        users.forEach(user -> {
            ReadStatus readStatus = new ReadStatus(user.getId(), channel.getId());
            readStatusRepository.save(readStatus);
            user.joinChannel(channel);
            userRepository.save(user);
        });
        channelRepository.save(channel);
        return ChannelDto.ChannelResponsePrivate.from(channel);
    }

    @Override
    public ChannelDto.ChannelResponse findById(UUID channelId) {
        // 채널 유형에 따른 반환타입 분리 -- PRIVATE 이면 name, description 대신에 userId리스트
        Objects.requireNonNull(channelId, "유저 Id가 유효하지 않습니다.");
        Channel channel = entityFinder.getChannel(channelId);
        if (channel.getType().equals(Channel.channelType.PUBLIC))
            return ChannelDto.ChannelResponsePublic.from(channel);
        return ChannelDto.ChannelResponsePrivate.from(channel);
    }

    @Override
    public List<ChannelDto.ChannelResponse> findAllByUserId(UUID userId) {
        // 유저 ID를 받아서 해당 유저가 볼 수 있는 채널 목록만 표시
        User user = entityFinder.getUser(userId);
        return channelRepository.findAll().stream().filter(channel ->
                        !(channel.getType().equals(Channel.channelType.PRIVATE) &&
                                channel.getUserIds().stream().noneMatch(userId1 -> userId1.equals(userId))))
                .<ChannelDto.ChannelResponse>map(channel -> {
                    if (channel.getType().equals(Channel.channelType.PUBLIC))
                        return ChannelDto.ChannelResponsePublic.from(channel);
                    else return ChannelDto.ChannelResponsePrivate.from(channel);
                }).toList();
    }

    @Override
    public ChannelDto.ChannelResponsePublic update(UUID channelId, ChannelDto.ChannelRequest request) {
        // PUBLIC 채널만 수정 가능
        if (entityFinder.getChannel(channelId).getType().equals(Channel.channelType.PRIVATE))
            throw new IllegalStateException("개인 대화방 정보는 수정할 수 없습니다.");
        Channel channel = entityFinder.getChannel(channelId);

        // 자기 자신을 제외한 중복 검사
        channelRepository.findAll().stream()
                .filter(channel1 -> channel1.getType().equals(Channel.channelType.PUBLIC))
                .filter(channel1 -> !channel1.getId().equals(channelId))
                .filter(channel1 -> channel1.getName().equals(request.name()))
                .findFirst().ifPresent(channel1 -> {
                    if (channel1.getName().equals(request.name())) throw new IllegalArgumentException("이미 존재하는 이름입니다.");
                });
        Optional.ofNullable(request.name()).ifPresent(channel::updateName);
        Optional.ofNullable(request.description()).ifPresent(channel::updateDescription);
        channelRepository.save(channel);
        return ChannelDto.ChannelResponsePublic.from(channel);
    }

    @Override
    public void delete(UUID channelId) {
        // 입력값 검증
        Channel channel = entityFinder.getChannel(channelId);
        // 채널에 참가한 유저 리스트에서 채널 삭제
        channel.getUserIds().forEach(userId -> {
            entityFinder.getUser(userId).leaveChannel(channel);
            userRepository.save(entityFinder.getUser(userId));
        });
        // 채널에 작성되었던 메세지 객체 전체 삭제
        channel.getMessageIds().forEach(messageRepository::delete);
        // 연결된 ReadStatus 삭제
        readStatusRepository.findAllByChannelId(channelId).forEach(readStatus ->
                readStatusRepository.delete(readStatus.getId()));
        // 채널 삭제
        channelRepository.delete(channelId);
    }

    // 채널 체크 헬퍼 메서드
    public Channel CheckChannel(String name) {
        return channelRepository.findAll().stream()
                .filter(channel -> channel.getName().equals(name))
                .findFirst().orElseThrow(() -> new RuntimeException("해당하는 채널이 없습니다."));
    }
}