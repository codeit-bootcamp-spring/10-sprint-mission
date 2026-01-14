package com.sprint.mission.discodeit.service.jcf;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.service.ChannelService;
import com.sprint.mission.discodeit.service.MessageService;
import com.sprint.mission.discodeit.validation.ValidationMethods;

import java.util.*;

public class JCFChannelService implements ChannelService {
    private final Map<UUID, Channel> data;
    private final MessageService messageService; // 삭제 기능을 위해 추가
    // 인프라 건설 후 사람 입주의 느낌으로 이해
    // 실행: user->channel->message
    // 조립: message->channel->user

    public JCFChannelService(Map<UUID, Channel> data, MessageService messageService) {
        this.data = data;
        this.messageService = messageService;
    }

    @Override
    public String toString() {
        return "JCFChannelService{" +
//                "data = " + data + ", " +
                "data key = " + data.keySet() + ", " +
                "data value = " + data.values() +
                '}';
    }

    // C. 생성: Channel 생성 후 Channel 객체 반환
    @Override
    public Channel createChannel(User owner, Boolean isPrivate, String channelName, String channelDescription) {
        // owner null 검증
        ValidationMethods.validateObject(owner, "owner");
        // channelName 검증
        ValidationMethods.validateString(channelName, "channelName");

        Channel channel = new Channel(owner, isPrivate, channelName, channelDescription);
        owner.joinChannel(channel);

        data.put(channel.getId(), channel);
        return channel;
    }

    // R. 읽기
    // 특정 채널 정보 읽기
    @Override
    public Optional<Channel> readChannelByChannelId(UUID channelId) {
        // Channel ID null 검증
        ValidationMethods.validateChannelId(channelId);

        return Optional.ofNullable(data.get(channelId));
    }

    // R. 모두 읽기
    // 채널 목록 전체
    @Override
    public List<Channel> readAllChannel() {
        return new ArrayList<>(data.values());
    }

    // 비공개 여부에 따른 채널 목록
    @Override
    public List<Channel> readPublicOrPrivateChannel(Boolean isPrivate) {
        if (isPrivate == null) {
            // null이면 전체 조회
            return readAllChannel();
        } else if (isPrivate) {
            return data.values().stream()
                    .filter(channel -> channel.getPrivate() == true)
                    .toList();
        } else { // !isPrivate
            return data.values().stream()
                    .filter(channel -> channel.getPrivate() == false)
                    .toList();
        }
    }

    // 특정 채널에 속한 모든 유저
    @Override
    public List<User> readAllUsersByChannelId(UUID channelId) {
        // Channel ID null 검증
        ValidationMethods.validateChannelId(channelId);

        Channel channel = data.get(channelId);
        // channel이 null인지 확인
        if (channel == null) {
            // null이면 빈 리스트
            return Collections.emptyList();
        }

        return channel.getChannelMembersList().stream().toList();
    }

    // 특정 채널 이름이 들어간 채널 검색
    @Override
    public List<Channel> searchChannelByChannelName(String partialChannelName) {
        // partialChannelName 검증
        ValidationMethods.validateString(partialChannelName, "partialChannelName");

        return data.values().stream()
                .filter(channel -> channel.getChannelName().contains(partialChannelName))
                .toList();
    }

    // 특정 채널에서 특정 사용자 찾기
    @Override
    public List<User> searchChannelUserByPartialName(UUID channelId, String partialName) {
        // Channel ID null 검증
        ValidationMethods.validateChannelId(channelId);

        Channel channel = data.get(channelId);
        // channel이 null인지 확인
        if (channel == null) {
            // null이면 빈 리스트
            return Collections.emptyList();
        }

        return channel.getChannelMembersList().stream()
                .filter(user -> user.getUserName().contains(partialName) ||
                        user.getNickName().contains(partialName))
                .toList();
    }

    // 특정 채널의 모든 메시지 읽어오기
    @Override
    public List<Message> readChannelMessageByChannelId(UUID channelId) {
        // Channel ID null 검증
        ValidationMethods.validateChannelId(channelId);

        Channel channel = data.get(channelId);
        // channel이 null인지 확인
        if (channel == null) {
            return Collections.emptyList();
        }

        return channel.getChannelMessagesList().stream().toList();
    }

    // 특정 채널에서 원하는 메시지 찾기
    @Override
    public List<Message> searchChannelMessageByChannelIdAndWord(UUID channelId, String partialWord) {
        // Channel ID null 검증
        ValidationMethods.validateChannelId(channelId);

        Channel channel = data.get(channelId);
        // channel이 null인지 확인
        if (channel == null) {
            // null이면 빈 리스트
            return Collections.emptyList();
        }

        return channel.getChannelMessagesList().stream()
                .filter(message -> message.getContent().contains(partialWord))
                        .toList();
    }

    // U. 수정
    // `Duplicated code fragment (12 lines long)`
    // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체와 channel 객체 존재 확인
    public static Channel validateMethods(Map<UUID, Channel> data, UUID requestId, UUID channelId) {
        // request ID null 검증
        ValidationMethods.validateUserId(requestId);
        // Channel ID `null` 검증
        ValidationMethods.validateChannelId(channelId);

        Channel channel = data.get(channelId);
        ValidationMethods.existChannel(channel);

        User owner = channel.getOwner();
        ValidationMethods.existUser(owner);

        // requestId와 owner ID가 동일한지 검증
        ValidationMethods.validateSameId(requestId, owner.getId());

        return channel;
    }

    // 채널 channelName 수정
    @Override
    public Channel updateChannelName(UUID requestId, UUID channelId, String channelName) {
        // ID null 검증 / req ID와 target ID의 동일한지 확인 / user 객체와 channel 객체 존재 확인
        Channel channel = validateMethods(data, requestId, channelId);
        ValidationMethods.validateString(channelName, "channelName");

        channel.updateChannelName(channelName);
        return channel;
    }

    // 채널 isPrivate 수정
    @Override
    public Channel updateChannelIsPrivate(UUID requestId, UUID channelId, Boolean isPrivate) {
        Channel channel = validateMethods(data, requestId, channelId);

        channel.updateIsPrivate(isPrivate);
        return channel;
    }

    // 채널 owner 변경
    @Override
    public Channel updateChannelOwner(UUID requestId, UUID channelId, User owner) {
        Channel channel = validateMethods(data, requestId, channelId);

        // 존재하는 owner인지 확인
        ValidationMethods.existUser(owner);

        channel.changeOwner(channel, owner);
        return channel;
    }

    // 채널 description 수정
    @Override
    public Channel updateChannelDescription(UUID requestId, UUID channelId, String channelDescription) {
        Channel channel = validateMethods(data, requestId, channelId);

        channel.updateChannelDescription(channelDescription);
        return channel;
    }

    // D. 삭제
    @Override
    public void deleteChannel(UUID requestId, UUID channelId) {
        Channel channel = validateMethods(data, requestId, channelId);

        // 연관 관계 정리
        // 해당 채널과 관련된 모든 메시지 삭제 및 채널이 보유한 메세지 리스트 연결 끊기
        channel.getChannelMessagesList()
                .forEach(message ->
                        messageService.deleteMessage(message.getAuthor().getId(), message.getId()));

        // 해당 채널에 참여한 user 찾아서 내보내기
        channel.getChannelMembersList().forEach(user -> user.leaveChannel(channel));
        // 해당 채널을 소유한 소유주(user)를 찾아서 owner 소유권 제거
        channel.getOwner().removeChannelOwner(channel);

        data.remove(channelId);
    }
}
