package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
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

    @Override
    public ChannelDto.ChannelResponsePublic createPublic(ChannelDto.ChannelRequest request) {
        // 단체톡방
        Objects.requireNonNull(request.name(), "채널 이름은 필수입니다.");
        Objects.requireNonNull(request.description(), "채널 설명은 필수입니다.");

        Channel channel = new Channel(request);
        channelRepository.save(channel);
        return ChannelDto.ChannelResponsePublic.from(channel);
    }

    @Override
    public ChannelDto.ChannelResponsePrivate createPrivate(List<UUID> userIds) {
        // 익명톡방
        List<User> users = userIds.stream().map(userId -> Objects.requireNonNull(userRepository.findById(userId))).toList();

        Channel channel = new Channel(users);

        users.forEach(user -> {
            ReadStatus readStatus = new ReadStatus(user, channel);
            readStatusRepository.save(readStatus);
        });
        channelRepository.save(channel);
        return ChannelDto.ChannelResponsePrivate.from(channel);
    }

    @Override
    public ChannelDto.ChannelResponse findById(UUID channelId) {
        // 채널 유형에 따른 반환타입 분리 -- PRIVATE 이면 name, description 대신에 userId리스트
        Channel channel = Objects.requireNonNull(channelRepository.findById(channelId));
        if (channel.getType().equals(Channel.channelType.PUBLIC))
            return ChannelDto.ChannelResponsePublic.from(channel);
        return ChannelDto.ChannelResponsePrivate.from(channel);
    }

    @Override
    public List<ChannelDto.ChannelResponse> findAllByUserId(UUID userId) {
        // 유저 ID를 받아서 해당 유저가 볼 수 있는 채널 목록만 표시
        return channelRepository.findAll().stream()
                .<ChannelDto.ChannelResponse>map(channel -> {
                    if (channel.getType().equals(Channel.channelType.PRIVATE) &&
                            (channel.getUsers().get(0).getId().equals(userId) || channel.getUsers().get(1).getId().equals(userId)))
                        return ChannelDto.ChannelResponsePrivate.from(channel);
                    else return ChannelDto.ChannelResponsePublic.from(channel);
                }).toList();
    }

    @Override
    public ChannelDto.ChannelResponsePublic update(UUID channelId, ChannelDto.ChannelRequest request) {
        // PUBLIC 채널만 수정 가능
        if (channelRepository.findById(channelId).getType().equals(Channel.channelType.PRIVATE))
            throw new IllegalStateException("개인 대화방 정보는 수정할 수 없습니다.");
        Channel channel = channelRepository.findById(channelId);
        Optional.ofNullable(request.name()).ifPresent(channel::updateName);
        Optional.ofNullable(request.description()).ifPresent(channel::updateDescription);
        channelRepository.save(channel);
        return ChannelDto.ChannelResponsePublic.from(channel);
    }

    @Override
    public void delete(UUID channelId) {
        // 입력값 검증
        Channel channel = Objects.requireNonNull(channelRepository.findById(channelId), "해당 채널이 존재하지 않습니다.");
        // 채널에 참가한 유저 리스트에서 채널 삭제
        channel.getUsers().forEach(user -> user.leaveChannel(channel));
        // 채널에 작성되었던 메세지 객체 전체 삭제
        channel.getMessages().forEach(message -> messageRepository.delete(message.getId()));
        // 연결된 ReadStatus 삭제
        readStatusRepository.findAllByChannelId(channelId).forEach(readStatus ->
                readStatusRepository.delete(readStatus.getId()));
        // 채널 삭제
        channelRepository.delete(channelId);
    }
}