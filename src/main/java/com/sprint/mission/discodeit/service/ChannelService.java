package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChannelService {
    // CRUD(생성, 읽기, 모두 읽기, 수정, 삭제 기능)
    // C. 생성: channelId와 owner 기타 등등 출력
    Channel createChannel(UUID ownerId, Boolean isPrivate, String channelName, String channelDescription);

    // R. 읽기
    // 특정 채널 정보 읽기
    Optional<Channel> readChannelByChannelId(UUID channelId);

    // R. 모두 읽기
    // 채널 목록 전체
    List<Channel> readAllChannel();
    // 비공개 여부에 따른 채널 목록
    List<Channel> readPublicOrPrivateChannel(Boolean isPrivate);
    // 특정 채널에 속한 모든 유저
    List<User> readAllUsersByChannelId(UUID channelId);
    // 특정 채널 이름이 들어간 채널 검색
    List<Channel> searchChannelByChannelName(String partialChannelName);
    // 특정 채널에서 특정 사용자 찾기
    List<User> searchChannelUserByPartialName(UUID channelId, String partialName);
    // 특정 채널의 모든 메시지 읽어오기
    List<Message> readChannelMessageByChannelId(UUID channelId);
    // 특정 채널에서 원하는 메시지 찾기
    List<Message> searchChannelMessageByChannelIdAndWord(UUID channelId, String partialWord);

    // U. 수정
    // 채널 channelName 수정
    Channel updateChannelName(UUID requestId, UUID channelId, String channelName);
    // 채널 isPrivate 수정
    Channel updateChannelIsPrivate(UUID requestId, UUID channelId, Boolean isPrivate);
    // 채널 owner 수정
    Channel updateChannelOwner(UUID requestId, UUID channelId, User owner);
    // 채널 description 수정
    Channel updateChannelDescription(UUID requestId,UUID channelId, String channelDescription);

    // D. 삭제
    void deleteChannel(UUID requestId, UUID channelId);
}
