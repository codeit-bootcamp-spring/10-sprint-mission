package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JCFUserService implements UserService {
    private final Map<UUID, User> data;

    public JCFUserService() {
        this.data = new HashMap<>();
    }

    @Override
    public User createUser(String nickname, String email) {
        // 새로운 유저 생성
        User user = new User(nickname, email);
        data.put(user.getId(), user);
        return user;
    }

    @Override
    public User findUserById(UUID userId) {
        // 존재하는 유저인지 검색 및 검증, 존재하지 않으면 예외 발생
        User user = data.get(userId);
        if (user == null) {
            throw new RuntimeException("유저가 존재하지 않습니다.");
        }

        return user;
    }

    @Override
    public List<User> findAll() {
        // 전체 유저 목록 반환
        return new ArrayList<>(data.values());
    }

    @Override
    public User updateUserNickname(UUID userId, String newNickname) {
        // 수정 대상 유저가 실제로 존재하는지 검색 및 검증
        User user = findUserById(userId);
        // 유저 닉네임 수정
        return user.updateUserNickname(newNickname);
    }

    @Override
    public void deleteUser(UUID userId) {
        // 삭제 대상 유저가 실제로 존재하는지 검증
        User user = findUserById(userId);

        // 채널을 소유한 상태인지 확인, 채널을 소유한 상태에선 탈퇴 불가
        List<Channel> channels = user.getChannels();
        for (Channel channel : channels) {
            if (channel.getOwner().equals(user)) {
                throw new RuntimeException("채널을 소유한 상태에서는 탈퇴를 할 수 없습니다.");
            }
        }

        // 가입한 모든 채널에서 유저가 작성한 메시지 제거 및 유저 제거
        for (Channel channel : channels) {
            for (Message message : channel.getMessages()) {
                if (message.getUser().equals(user)) {
                    message.removeFromChannelAndUser();
                }
            }
            channel.removeUser(user);
        }

        // 유저 탈퇴, 저장소에서 제거
        data.remove(userId);
    }

    @Override
    public void removeChannelFromJoinedUsers(Channel channel) {
        // 채널 null 체크
        if (channel == null) {
            throw new RuntimeException("채널이 존재하지 않습니다.");
        }

        // 채널 삭제 시, 해당 채널에 가입된 모든 유저를 탈퇴 처리
        for (User user : data.values()) {
            user.leaveChannel(channel);
        }
    }
}
