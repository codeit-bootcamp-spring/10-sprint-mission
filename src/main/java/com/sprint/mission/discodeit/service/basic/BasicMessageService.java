package com.sprint.mission.discodeit.service.basic;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.MessagePatchDto;
import com.sprint.mission.discodeit.dto.MessagePostDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.BinaryContentMapper;
import com.sprint.mission.discodeit.mapper.MessageMapper;
import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.MessageRepository;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.MessageService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {
	private final UserRepository userRepository;
	private final ChannelRepository channelRepository;
	private final MessageRepository messageRepository;
	private final BinaryContentRepository binaryContentRepository;

	private final MessageMapper messageMapper;
	private final BinaryContentMapper binaryContentMapper;

	@Override
	public MessageResponseDto create(MessagePostDto messagePostDto) throws RuntimeException {
		// 메시지를 생성 전, 유저가 해당 채널에 속해있는지 확인한다.
		Channel channel = channelRepository.findById(messagePostDto.channelId())
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + messagePostDto.channelId() + "인 채널은 존재하지 않습니다.")
			);
		User user = userRepository.findById(messagePostDto.authorId())
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + messagePostDto.authorId() + "인 유저는 존재하지 않습니다.")
			);

		if (user.getChannelIds().stream()
			.noneMatch(chId -> chId.equals(messagePostDto.channelId()))) {
			throw new IllegalArgumentException(
				user.getUserName() + "님은 " + channel.getName() + " 채널에 속해있지 않아 메시지를 보낼 수 없습니다."
			);
		}

		Message newMessage = messageRepository.save(messageMapper.toMessage(messagePostDto));

		// channel과 user의 messageList에 현재 message를 add
		channel.addMessage(newMessage.getId());
		channelRepository.save(channel);

		user.addMessageId(newMessage.getId());
		userRepository.save(user);

		// 선택적으로 첨부파일 등록
		Optional.ofNullable(messagePostDto.attachments())
			.ifPresent(attachments -> {
					attachments.forEach(attachment ->
						binaryContentRepository.save(
							binaryContentMapper.fromDto(user.getId(), newMessage.getId(), attachment))
					);
				}
			);

		return messageMapper.toResponse(newMessage);
	}

	@Override
	public MessageResponseDto findById(UUID id) {
		return messageMapper.toResponse(messageRepository.findById(id)
			.orElseThrow(() ->
				new NoSuchElementException("id가 " + id + "인 메시지는 존재하지 않습니다.")
			)
		);
	}

	@Override
	public List<MessageResponseDto> findByUser(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + userId + "인 유저는 존재하지 않습니다.")
			);

		return user.getMessageIds().stream()
			.map(messageRepository::findById)
			.flatMap(Optional::stream)
			.map(messageMapper::toResponse)
			.collect(Collectors.toList());
	}

	@Override
	public List<MessageResponseDto> findByChannelId(UUID channelId) {
		return channelRepository.findById(channelId)
			.stream()
			.map(Channel::getMessageIds)
			.flatMap(Collection::stream)
			.map(messageRepository::findById)
			.flatMap(Optional::stream)
			.map(messageMapper::toResponse)
			.collect(Collectors.toList());
	}

	@Override
	public MessageResponseDto updateById(UUID messageId, MessagePatchDto messagePatchDto) {
		Message message = messageRepository.findById(messageId)
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + messageId + "인 메시지는 존재하지 않습니다.")
			);

		message.updateText(messagePatchDto.text());
		messageRepository.save(message);

		return messageMapper.toResponse(message);
	}

	@Override
	public void delete(UUID messageId) {
		Message message = messageRepository.findById(messageId)
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + messageId + "인 메시지는 존재하지 않습니다.")
			);

		// 유저쪽에서 메시지 정보 삭제
		User user = userRepository.findById(message.getAuthorId())
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + message.getAuthorId() + "인 유저는 존재하지 않습니다.")
			);
		user.getMessageIds()
			.removeIf(msgId -> msgId.equals(messageId));
		userRepository.save(user);

		// 채널쪽에 메시지 정보 삭제
		Channel channel = channelRepository.findById(message.getChannelId())
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + message.getChannelId() + "인 채널은 존재하지 않습니다.")
			);
		channel.getMessageIds()
			.removeIf(msgId -> msgId.equals(messageId));
		channelRepository.save(channel);

		// binaryContent도 삭제
		message.getAttachmentIds().forEach(binaryContentRepository::delete);

		// 실제 메시지 객체 삭제
		messageRepository.delete(messageId);
	}
}
