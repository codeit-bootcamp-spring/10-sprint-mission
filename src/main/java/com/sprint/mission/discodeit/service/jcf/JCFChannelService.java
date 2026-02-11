package com.sprint.mission.discodeit.service.jcf;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.ChannelPatchDTO;
import com.sprint.mission.discodeit.dto.ChannelResponseDTO;
import com.sprint.mission.discodeit.dto.PrivateChannelPostDTO;
import com.sprint.mission.discodeit.dto.PublicChannelPostDTO;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

public class JCFChannelService implements ChannelService {
	private static ChannelService instance;
	private final List<Channel> data;

	private final UserService userService;

	private JCFChannelService() {
		data = new ArrayList<Channel>();
		userService = JCFUserService.getInstance();
	}

	public static ChannelService getInstance() {
		if (instance == null)
			instance = new JCFChannelService();
		return instance;
	}

	@Override
	public Channel createPublicChannel(PublicChannelPostDTO publicChannelPostDTO) {
		return null;
	}

	@Override
	public Channel createPrivateChannel(PrivateChannelPostDTO channelPostDTO) {
		return null;
	}

	@Override
	public ChannelResponseDTO findById(UUID channelId) {
		// return data.stream()
		// 	.filter(ch -> ch.getId().equals(channelId))
		// 	.findFirst()
		// 	.orElseThrow(
		// 		() -> new NoSuchElementException(channelId + " 은(는) 존재하지 않는 채널 id입니다.")
		// 	);
		return null;
	}

	@Override
	public List<ChannelResponseDTO> findAllByUserId(UUID userId) {
		// return data;
		return null;
	}

	@Override
	public Channel updateName(ChannelPatchDTO channelPatchDTO) {
		// Channel channel = data.stream()
		// 	.filter(ch -> ch.getId().equals(channelId))
		// 	.findFirst()
		// 	.orElseThrow(() -> new NoSuchElementException("id가 " + channelId + "인 채널은 존재하지 않습니다."));
		//
		// channel.updateName(name);
		//
		// return channel;

		return null;
	}

	@Override
	public Channel addUser(UUID channelId, UUID userId) {
		// Channel channel = data.stream()
		// 	.filter(ch -> ch.getId().equals(channelId))
		// 	.findFirst()
		// 	.orElseThrow(() -> new NoSuchElementException("id가 " + channelId + "인 채널은 존재하지 않습니다."));
		//
		// User user = userService.findById(userId);
		//
		// channel.addUserId(user);
		//
		// return channel;
		return null;
	}

	@Override
	public boolean deleteUser(UUID channelId, UUID userId) {
		// // 채널 객체를 찾는다.
		// Channel channel = data.stream()
		// 	.filter(ch -> ch.getId().equals(channelId))
		// 	.findFirst()
		// 	.orElseThrow(() -> new NoSuchElementException("id가 " + channelId + "인 채널은 존재하지 않습니다."));
		//
		// // 해당 채널에 유저가 속해있는지 확인한 후 내보낸다.
		// if (this.isUserInvolved(channelId, userId)) {
		// 	return channel.getUserIds().removeIf(u -> u.getId().equals(userId));
		// }
		//
		return false;
	}

	@Override
	public void delete(UUID channelId) {
		if (!data.removeIf(user -> user.getId().equals(channelId)))
			throw new NoSuchElementException("id가 " + channelId + "인 채널은 존재하지 않습니다.");
	}

	@Override
	public boolean isUserInvolved(UUID channelId, UUID userId) {
		// // 채널과 유저 객체를 찾는다.
		// Channel channel = data.stream()
		// 	.filter(ch -> ch.getId().equals(channelId))
		// 	.findFirst()
		// 	.orElseThrow(() -> new NoSuchElementException("id가 " + channelId + "인 채널은 존재하지 않습니다."));
		//
		// User user = userService.findById(userId);
		//
		// return channel.getUserIds().contains(user);
		return false;
	}
}
