package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.MessagePatchDto;
import com.sprint.mission.discodeit.dto.MessagePostDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.service.UserService;

public class JCFMessageService implements MessageService {
	private static MessageService instance;
	private final List<Message> data;

	private final UserService userService;
	private final ChannelService channelService;

	private JCFMessageService() {
		this.data = new ArrayList<Message>();
		this.userService = JCFUserService.getInstance();
		this.channelService = JCFChannelService.getInstance();
	}

	public static MessageService getInstance() {
		if (instance == null)
			instance = new JCFMessageService();
		return instance;
	}

	// data 필드를 활용해 생성, 조회, 수정, 삭제하는 메소드를 구현하세요.
	@Override
	public MessageResponseDto create(MessagePostDto messagePostDTO) {
		// // 메시지를 생성 전, 유저가 해당 채널에 속해있는지 확인한다.
		// Channel channel = channelService.findById(channelId);
		// User user = userService.findById(authorId);
		//
		// if (user.getChannelIds().stream()
		// 	.noneMatch(ch -> ch.getId().equals(channelId))) {
		// 	throw new IllegalArgumentException(
		// 		user.getUserName() + "님은 " + channel.getName() + " 채널에 속해있지 않아 메시지를 보낼 수 없습니다."
		// 	);
		// }
		//
		// Message newMessage = new Message(user, text, channel);
		// data.add(newMessage);
		//
		// // channel과 user의 messageList에 현재 message를 add
		// channel.addMessage(newMessage);
		// user.addMessageId(newMessage);
		//
		// return newMessage;
		return null;
	}

	@Override
	public MessageResponseDto findById(UUID id) {
		// return data.stream()
		// 	.filter(message -> message.getId().equals(id))
		// 	.findFirst()
		// 	.orElseThrow(
		// 		() -> new NoSuchElementException("id가 " + id + "인 메시지는 존재하지 않습니다.")
		// 	);
		return null;
	}

	@Override
	public List<MessageResponseDto> findByUser(UUID userId) {
		// 해당 id를 갖는 user 객체의 메시지 리스트를 반환하도록 변경
		// return userService.findById(authorId).getMessageList();
		return null;
	}

	@Override
	public List<MessageResponseDto> findByChannelId(UUID channelId) {
		// 해당 id를 갖는 channel 객체의 메시지 리스트를 반환하도록 변경
		// return channelService.findById(channelId).getMessageIds();
		return null;
	}

	@Override
	public MessageResponseDto updateById(UUID messageId, MessagePatchDto messagePatchDTO) {
		// Message message = data.stream()
		// 	.filter(msg -> msg.getId().equals(messageId))
		// 	.findFirst()
		// 	.orElseThrow(() -> new NoSuchElementException("id가 " + messageId + "인 메시지는 존재하지 않습니다."));
		//
		// message.updateText(text);
		//
		// return message;
		return null;
	}

	@Override
	public void delete(UUID messageId) {
		if (!data.removeIf(message -> message.getId().equals(messageId)))
			throw new NoSuchElementException("id가 " + messageId + "인 메시지는 존재하지 않습니다.");
	}
}
