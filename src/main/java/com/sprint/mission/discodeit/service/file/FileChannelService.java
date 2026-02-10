package com.sprint.mission.discodeit.service.file;

import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import com.sprint.mission.discodeit.dto.ChannelResponseDto;
import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.util.FileIo;

public class FileChannelService {
	private static ChannelService instance;
	private final FileIo<Channel> channelFileIo;
	private final FileIo<User> userFileIo;
	private final UserService userService;

	private FileChannelService(@Value("${discodeit.repository.file-directory}") String directory) {
		channelFileIo = new FileIo<>(Paths.get(directory).resolve(Channel.class.getSimpleName().toLowerCase()));
		this.channelFileIo.init();
		userFileIo = new FileIo<>(Paths.get(directory).resolve(User.class.getSimpleName().toLowerCase()));
		this.userFileIo.init();

		userService = FileUserService.getInstance();
	}

	public static ChannelService getInstance() {
		// if (instance == null)
		// 	instance = new FileChannelService();
		return instance;
	}

	public Channel create(String name) {
		//        Channel newChannel = new Channel(name);
		//
		//        List<Channel> channels = channelFileIo.load();
		//        channels.add(newChannel);
		//        channelFileIo.save(newChannel.getId(), newChannel);
		//
		//        return newChannel;
		return null;
	}

	public ChannelResponseDto findById(UUID channelId) {
		//        return channelFileIo.load().stream()
		//            .filter(ch -> ch.getId().equals(channelId))
		//            .findFirst()
		//            .orElseThrow(
		//                () -> new NoSuchElementException("id가 " + channelId + "인 채널을 찾을 수 없습니다.")
		//            );
		return null;
	}

	public List<ChannelResponseDto> findAllChannel() {
		//        return channelFileIo.load();
		return null;
	}

	public Channel updateName(UUID channelId, String name) {
		//        Channel updateChannel = this.findById(channelId);
		//
		//        updateChannel.updateName(name);
		//        channelFileIo.save(channelId, updateChannel);
		//
		//        return updateChannel;
		return null;
	}

	public Channel addUser(UUID channelId, UUID userId) {
		//        Channel channel = this.findById(channelId);
		//        User user = userService.findById(authorId);
		//
		//        channel.addUserId(user);
		//        channelFileIo.save(channelId, channel);
		//        userFileIo.save(authorId, user);
		//
		//        return channel;

		return null;
	}

	public boolean deleteUser(UUID channelId, UUID userId) {
		//        boolean result = false;
		//        Channel channel = this.findById(channelId);
		//        User user = userService.findById(authorId);
		//
		//        // 해당 채널에 유저가 속해있는지 확인한 후 내보낸다.
		//        // 이때 유저 쪽도 채널 정보를 제거해야 한다.
		//        if (this.isUserInvolved(channelId, authorId)) {
		//            result = channel.getUserIds().removeIf(u -> u.getId().equals(authorId));
		//            result = result && user.getChannelList().removeIf(ch -> ch.getId().equals(channelId));
		//
		//            channelFileIo.save(channelId, channel);
		//            userFileIo.save(authorId, user);
		//        }

		return false;
	}

	public void delete(UUID channelId) {
		try {
			channelFileIo.delete(channelId);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean isUserInvolved(UUID channelId, UUID userId) {
		//        Channel channel = this.findById(channelId);
		//        User user = userService.findById(authorId);
		//
		//        return channel.getUserIds().contains(user);

		return false;
	}
}
