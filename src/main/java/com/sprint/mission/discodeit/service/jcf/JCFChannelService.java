package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.UserService;

import java.util.ArrayList;
import java.util.UUID;

public class JCFChannelService implements ChannelService {
    private final ArrayList<Channel> channels = new ArrayList<>();        // user 한 명당 가지는 채널
    // private final UserService userService;

    // 채널 생성
    @Override
    public Channel createChannel(String channelName, UUID ownerId, ChannelType channelType) {
        // 1. 유효성 검증 필요

        // 2. 채널 생성
        Channel newChannel = new Channel(channelName, ownerId, channelType);
        channels.add(newChannel);
        return newChannel;
    }

    // 채널 단건 조회
    @Override
    public Channel searchChannel(UUID targetChannelId) {
        // 1. 채널 탐색
        for (Channel channel : channels) {
            // 있으면 해당 채널 반환
            if (channel.getId().equals(targetChannelId)) {
                return channel;
            }
        }
        // 없으면 널 반환
        System.out.println("해당 채널이 존재하지 않습니다.");
        return null;
    }

    // 채널 다건 조회
    @Override
    public ArrayList<Channel> searchChannelAll() {
        return channels;
    }

    // 채널 정보 수정
    @Override
    public void updateChannel(UUID targetChannelId, String newChannelName) {
        // 1. 채널 탐색
        Channel targetChannel = searchChannel(targetChannelId);

        // 2. 채널 정보 수정
        if ((targetChannel != null && newChannelName != null) || newChannelName.isBlank()) {
            targetChannel.updateChannelName(newChannelName);
        }
        else  {
            System.out.println("잘못된 채널 이름입니다.");
        }
    }

    // 채널 삭제
    @Override
    public void deleteChannel(UUID targetChannelId) {
        // 1. 채널 탐색
        Channel targetChannel = searchChannel(targetChannelId);

        // 2. 채널 삭제
        if (targetChannel != null) {
            channels.remove(targetChannel);
        }
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