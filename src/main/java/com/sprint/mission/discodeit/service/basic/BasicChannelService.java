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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        return channelMapper.toDto(
            channelRepository.save(channelMapper.toEntity(publicChannelPostDto))
        );
    }

    @Override
    public ChannelDto createPrivateChannel(PrivateChannelPostDto privateChannelPostDto) {
        List<User> users = privateChannelPostDto.participantIds().stream()
            .map(userRepository::findById)
            .flatMap(Optional::stream)
            .toList();

        Channel channel = channelMapper.toEntity(privateChannelPostDto);

        for (User user : users) {
            channel.addUser(user);
        }

        channelRepository.save(channel);
        return channelMapper.toDto(channel);
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelDto findById(UUID channelId) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(
            () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
        );

        return channelMapper.toDto(channel);
    }

    // 특정 유저가 속해있는 채널 목록을 조회
    @Override
    @Transactional(readOnly = true)
    public List<ChannelDto> findAllByUserId(UUID userId) {
        return channelRepository.findByUserId(userId).stream()
            .map(channelMapper::toDto)
            .toList();
    }

    @Override
    public ChannelDto update(UUID channelId, ChannelPatchDto channelPatchDto) {
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

        return channelMapper.toDto(updateChannel);
    }

    @Override
    public ChannelDto addUser(UUID channelId, UUID userId) {
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

        return channelMapper.toDto(channel);
    }

    @Override
    public boolean deleteUser(UUID channelId, UUID userId) {
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
        if (!channelRepository.existsById(channelId)) {
            throw new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND, channelId);
        }

        channelRepository.deleteById(channelId);
    }

    public boolean isUserInvolved(UUID channelId, UUID userId) {
        // 채널과 유저 객체를 찾는다.
//        Channel channel = channelRepository.findById(channelId)
//            .orElseThrow(
//                () -> new BusinessLogicException(ExceptionCode.CHANNEL_NOT_FOUND)
//            );
//        User user = userRepository.findById(userId)
//            .orElseThrow(
//                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
//            );
//
//        return channel.getUserList().contains(user);
        return false;
    }
}
