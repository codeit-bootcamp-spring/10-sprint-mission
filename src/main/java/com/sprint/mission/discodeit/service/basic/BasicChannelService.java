package com.sprint.mission.discodeit.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.ChannelPatchDTO;
import com.sprint.mission.discodeit.dto.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDTO;
import com.sprint.mission.discodeit.dto.PublicChannelPostDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.ReadStatus;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.ChannelMapper;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.ChannelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicChannelService implements ChannelService {
	private final ChannelRepository channelRepository;
	private final UserRepository userRepository;
	private final MessageRepository messageRepository;
	private final ReadStatusRepository readStatusRepository;

	private final ChannelMapper channelMapper;

	@Override
	public Channel createPublicChannel(PublicChannelPostDTO publicChannelPostDTO) {
		return channelRepository.save(
			channelMapper.toChannel(publicChannelPostDTO)
		);
	}

	@Override
	public Channel createPrivateChannel(PrivateChannelPostDTO privateChannelPostDTO) {
		Channel channel = channelRepository.save(
			channelMapper.toChannel(privateChannelPostDTO)
		);

		// 채널에 참여하는 User의 정보를 받아 User 별 ReadStatus 정보를 생성
		privateChannelPostDTO.userIds().stream()
			.forEach(
				userId -> {
					readStatusRepository.save(
						new ReadStatus(userId, channel.getId(), Instant.now())
					);
				}
			);

		return channel;
	}

	@Override
	public ChannelResponseDTO findById(UUID channelId) {
		Channel channel = channelRepository.findById(channelId).orElseThrow(
			() -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다.")
		);

		Instant lastMessageTime = channel.getMessageIds().stream()
			.map(messageRepository::findById)
			.flatMap(Optional::stream)
			.map(Message::getCreatedAt)
			.max(Instant::compareTo)
			.orElse(null);

		return channelMapper.fromChannel(
			channel,
			lastMessageTime
		);
	}

	@Override
	public List<ChannelResponseDTO> findAllByUserId(UUID userId) {
		return channelRepository.findAll().stream()
			.filter(
				channel -> channel.getChannelType() == ChannelType.PUBLIC || (
					channel.getChannelType() == ChannelType.PRIVATE &&
						channel.getUserIds().contains(userId)
				)
			)
			.map(channel -> {
					Instant lastMessageTime = channel.getMessageIds().stream()
						.map(messageRepository::findById)
						.flatMap(Optional::stream)
						.map(Message::getCreatedAt)
						.max(Instant::compareTo)
						.orElse(null);

					return channelMapper.fromChannel(
						channel,
						lastMessageTime
					);
				}
			).collect(Collectors.toList());
	}

	@Override
	public Channel updateName(ChannelPatchDTO channelPatchDTO) {
		Channel updateChannel = channelRepository.findById(channelPatchDTO.channelId())
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + channelPatchDTO.channelId() + "인 채널을 찾을 수 없습니다.")
			);

		// PRIVATE 채널은 수정할 수 없음
		if (updateChannel.getChannelType() == ChannelType.PRIVATE)
			throw new RuntimeException("private 채널은 수정할 수 없습니다.");

		// 수정
		Optional.ofNullable(channelPatchDTO.name())
			.ifPresent(updateChannel::updateName);
		Optional.ofNullable(channelPatchDTO.description())
			.ifPresent(updateChannel::updateDescription);

		channelRepository.save(updateChannel);

		return updateChannel;
	}

	@Override
	public Channel addUser(UUID channelId, UUID userId) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다.")
			);
		User user = userRepository.findById(userId)
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + userId + "인 유저를 찾을 수 없습니다.")
			);

		channel.addUserId(userId);
		user.addChannelId(channelId);

		channelRepository.save(channel);
		userRepository.save(user);

		return channel;
	}

	@Override
	public boolean deleteUser(UUID channelId, UUID userId) {
		// 채널 객체를 찾는다.
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다.")
			);
		User user = userRepository.findById(userId)
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + userId + "인 유저를 찾을 수 없습니다.")
			);

		// 해당 채널에 유저가 속해있는지 확인한 후 내보낸다.
		// 유저쪽도 참여한 채녈 목록에서 삭제한다.
		if (this.isUserInvolved(channelId, userId)) {
			channel.getUserIds().remove(user);
			user.getChannelIds().remove(channel);

			channelRepository.save(channel);
			userRepository.save(user);

			return true;
		}

		return false;
	}

	@Override
	public void delete(UUID channelId) {
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다.")
			);

		// message와 readStatus도 삭제한다.
		channel.getMessageIds().forEach(messageRepository::delete);
		readStatusRepository.findByChannelId(channelId).forEach(readStatus -> {
				readStatusRepository.delete(readStatus.getId());
			}
		);

		// 채널 삭제
		channelRepository.delete(channelId);
	}

	@Override
	public boolean isUserInvolved(UUID channelId, UUID userId) {
		// 채널과 유저 객체를 찾는다.
		Channel channel = channelRepository.findById(channelId)
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다.")
			);
		User user = userRepository.findById(userId)
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + userId + "인 유저를 찾을 수 없습니다.")
			);

		return channel.getUserIds().contains(user);
	}
}
