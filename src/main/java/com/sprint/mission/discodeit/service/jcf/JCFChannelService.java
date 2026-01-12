package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;

import java.util.ArrayList;
import java.util.UUID;

import static com.sprint.mission.discodeit.Main.userService;
import static com.sprint.mission.discodeit.service.util.ValidationUtil.validateField;

public class JCFChannelService implements ChannelService {
    public static final ArrayList<Channel> channels = new ArrayList<>();        // 사용자 한 명당 가지는 채널

    // 채널 생성
    @Override
    public Channel createChannel(String channelName, User user, ChannelType channelType) {
        User owner = userService.searchUser(user.getId());

        Channel newChannel = new Channel(channelName, owner, channelType);
        channels.add(newChannel);
        return newChannel;
    }

    // 채널 단건 조회
    @Override
    public Channel searchChannel(UUID targetChannelId) {
        for (Channel channel : channels) {
            if (channel.getId().equals(targetChannelId)) {
                return channel;
            }
        }
        throw new IllegalArgumentException("해당 채널이 존재하지 않습니다.");
    }

    // 채널 다건 조회
    @Override
    public ArrayList<Channel> searchChannelAll() {      return channels;        }

    // 채널 정보 수정
    @Override
    public void updateChannel(UUID targetChannelId, String newChannelName) {
        Channel targetChannel = searchChannel(targetChannelId);

        validateField(newChannelName, "[채널명 변경 실패] 올바른 채널명이 아닙니다.");

        targetChannel.updateChannelName(newChannelName);
    }

    // 채널 삭제
    @Override
    public void deleteChannel(UUID targetChannelId) {
        Channel targetChannel = searchChannel(targetChannelId);

        channels.remove(targetChannel);
    }

    // 채널 참가자 초대
    @Override
    public void inviteMembers(UUID targetUserId, ArrayList<UUID> memberIds) {
//        // 1. 추가하려는 사용자 조회
//        User newUser = userService.searchUser(targetUserId);
//
//        // 2. 이미 채널에 존재하는지 확인
//        for (UUID memberId : memberIds) {
//            // 존재한다면 종료
//            if (memberId.equals(newUser.getId())) {
//                System.out.println("해당 채널에 이미 존재하는 사용자입니다.");
//                return;
//            }
//        }
//        // 존재하지 않는다면, 사용자 추가
//        memberIds.add(newUser.getId());
    }
}