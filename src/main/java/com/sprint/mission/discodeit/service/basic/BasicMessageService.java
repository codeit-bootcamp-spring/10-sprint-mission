package com.sprint.mission.discodeit.service.basic;

import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sprint.mission.discodeit.dto.MessagePatchDTO;
import com.sprint.mission.discodeit.dto.MessagePostDTO;
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
	public Message create(MessagePostDTO messagePostDTO) throws RuntimeException {
		// 메시지를 생성 전, 유저가 해당 채널에 속해있는지 확인한다.
		Channel channel = channelRepository.findById(messagePostDTO.channelId())
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + messagePostDTO.channelId() + "인 채널은 존재하지 않습니다.")
			);
		User user = userRepository.findById(messagePostDTO.userId())
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + messagePostDTO.userId() + "인 유저는 존재하지 않습니다.")
			);

		if (user.getChannelIds().stream()
			.noneMatch(chId -> chId.equals(messagePostDTO.channelId()))) {
			throw new IllegalArgumentException(
				user.getUserName() + "님은 " + channel.getName() + " 채널에 속해있지 않아 메시지를 보낼 수 없습니다."
			);
		}

		Message newMessage = messageRepository.save(messageMapper.toMessage(messagePostDTO));

		// channel과 user의 messageList에 현재 message를 add
		channel.addMessage(newMessage.getId());
		channelRepository.save(channel);

		user.addMessageId(newMessage.getId());
		userRepository.save(user);

		// 선택적으로 첨부파일 등록
		Optional.ofNullable(messagePostDTO.attachments())
			.ifPresent(attachments -> {
					attachments.forEach(attachment ->
						binaryContentRepository.save(
							binaryContentMapper.fromDto(user.getId(), newMessage.getId(), attachment))
					);
				}
			);

		return newMessage;
	}

	@Override
	public Message findById(UUID id) {
		return messageRepository.findById(id)
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + id + "인 메시지는 존재하지 않습니다.")
			);
	}

	@Override
	public List<Message> findByUser(UUID userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + userId + "인 유저는 존재하지 않습니다.")
			);

		return user.getMessageIds().stream()
			.map(messageRepository::findById)
			.flatMap(Optional::stream)
			.collect(Collectors.toList());
	}

	@Override
	public List<Message> findByChannelId(UUID channelId) {
		return channelRepository.findById(channelId)
			.stream()
			.map(Channel::getMessageIds)
			.flatMap(Collection::stream)
			.map(messageRepository::findById)
			.flatMap(Optional::stream)
			.collect(Collectors.toList());
	}

	@Override
	public Message updateById(MessagePatchDTO messagePatchDTO) {
		Message message = messageRepository.findById(messagePatchDTO.messageId())
			.orElseThrow(
				() -> new NoSuchElementException("id가 " + messagePatchDTO.messageId() + "인 메시지는 존재하지 않습니다.")
			);

		message.updateText(messagePatchDTO.text());
		messageRepository.save(message);

		return message;
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
