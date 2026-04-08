package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.ChannelDto;
import com.sprint.mission.discodeit.dto.ChannelPatchDto;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDto;
import com.sprint.mission.discodeit.dto.PublicChannelPostDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.BusinessLogicException;
import com.sprint.mission.discodeit.exception.ExceptionCode;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ReadStatusRepository readStatusRepository;

    private final ChannelMapper channelMapper;

    @Override
    public ChannelDto createPublicChannel(PublicChannelPostDto publicChannelPostDto) {
        log.trace("[CHANNEL] createPublicChannel 메서드 호출: name = {}, description = {}",
            publicChannelPostDto.name(), publicChannelPostDto.description());

        Channel newPublicChannel = channelRepository.save(
            channelMapper.toEntity(publicChannelPostDto));

        log.info("[CHANNEL] public 채널 생성 완료: id = {}", newPublicChannel.getId());

        return channelMapper.toDto(newPublicChannel);
    }

    @Override
    public ChannelDto createPrivateChannel(PrivateChannelPostDto privateChannelPostDto) {
        log.trace("[CHANNEL] createPrivateChannel 메서드 호출: participantIds = {}",
            privateChannelPostDto.participantIds());

        List<User> users = privateChannelPostDto.participantIds().stream()
            .map(userRepository::findById)
            .flatMap(Optional::stream)
            .toList();

        Channel channel = channelMapper.toEntity(privateChannelPostDto);

        for (User user : users) {
            log.trace("[CHANNEL] 새로운 private 채널에 유저 추가 중: userId = {}", user.getId());
            channel.addUser(user);
        }

        channelRepository.save(channel);

        log.info("[CHANNEL] private 채널 생성 완료: id = {}", channel.getId());

        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto findById(UUID channelId) {
        log.trace("[CHANNEL] findById 메서드 호출: id = {}", channelId);

        Channel channel = channelRepository.findById(channelId).orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
        );

        return channelMapper.toDto(channel);
    }

    // 특정 유저가 속해있는 채널 목록을 조회
    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {
        log.trace("[CHANNEL] findAllByUserId 메서드 호출: userId = {}", userId);

        return channelRepository.findByUserId(userId).stream()
            .map(channelMapper::toDto)
            .toList();
    }

    @Override
    public ChannelDto update(UUID channelId, ChannelPatchDto channelPatchDto) {
        log.trace("[CHANNEL] update 메서드 호출: id = {}, newName = {}, newDescription = {}", channelId,
            channelPatchDto.newName(), channelPatchDto.newDescription());

        Channel updateChannel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
            );

        // PRIVATE 채널은 수정할 수 없음
        if (updateChannel.getType() == ChannelType.PRIVATE) {
            throw new BusinessLogicException(ExceptionCode.PRIVATE_CHANNEL_MODIFY_EXCEPTION);
        }

        // 수정
        Optional.ofNullable(channelPatchDto.newName())
            .ifPresent(updateChannel::updateName);
        Optional.ofNullable(channelPatchDto.newDescription())
            .ifPresent(updateChannel::updateDescription);

        log.info("[CHANNEL] 채널 수정 완료: id = {}, newName = {}, newDescription = {}", channelId,
            channelPatchDto.newName(), channelPatchDto.newDescription());

        return channelMapper.toDto(updateChannel);
    }

    @Override
    public ChannelDto addUser(UUID channelId, UUID userId) {
        log.trace("[CHANNEL] addUser 메서드 호출: channelId = {}, userId = {}", channelId, userId);

        Channel channel = channelRepository.findById(channelId)
            .orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
            );
        User user = userRepository.findById(userId)
            .orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
            );

        // todo: addUser 메서드 수정
//        channel.addUser(user);
//        user.addChannel(channel);

        channelRepository.save(channel);
        userRepository.save(user);

        log.info("[CHANNEL] 유저 추가 완료: channelId = {}, userId = {}", channelId, userId);

        return channelMapper.toDto(channel);
    }

    @Override
    public boolean deleteUser(UUID channelId, UUID userId) {
        log.trace("[CHANNEL] deleteUser 메서드 호출: channelId = {}, userId = {}", channelId, userId);
//        Channel channel = channelRepository.findById(channelId)
//            .orElseThrow(
//                () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
//            );
//        User user = userRepository.findById(userId)
//            .orElseThrow(
//                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
//            );

        return false;
    }

    @Override
    public void delete(UUID channelId) {
        log.trace("[CHANNEL] delete 메서드 호출: id = {}", channelId);

        if (!channelRepository.existsById(channelId)) {
            throw new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND, channelId);
        }

        log.info("[CHANNEL] 채널 삭제 완료: id = {}", channelId);

        channelRepository.deleteById(channelId);
    }
}
