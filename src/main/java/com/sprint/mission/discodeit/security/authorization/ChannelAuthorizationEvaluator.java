package com.sprint.mission.discodeit.security.authorization;

import com.sprint.mission.discodeit.entity.Channel;
import com.sprint.mission.discodeit.entity.ChannelType;
import com.sprint.mission.discodeit.exception.channel.ChannelNotFoundException;
import com.sprint.mission.discodeit.exception.common.InvalidInputException;
import com.sprint.mission.discodeit.repository.ChannelRepository;
import com.sprint.mission.discodeit.repository.ReadStatusRepository;
import com.sprint.mission.discodeit.security.userdetails.DiscodeitUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.UUID;

// 채널 권한 판단 클래스
@Component
@RequiredArgsConstructor
public class ChannelAuthorizationEvaluator {

    private final ChannelRepository channelRepository;
    private final ReadStatusRepository readStatusRepository;

    private final RoleHierarchy roleHierarchy;

    // 채널 삭제할 수 있는 권한인지 확인
    public boolean canDelete(UUID channelId, Authentication authentication) {
        if (channelId == null) {
            throw new InvalidInputException("channelId", null);
        }

        // 인증된 사용자 null 여부
        if (authentication == null) {
            return false;
        }

        // 채널 조회
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new ChannelNotFoundException(channelId));

        // ChannelType이 PUBLIC일 때, CHANNEL_MANAGER 권한일 경우 삭제
        if (ChannelType.PUBLIC.equals(channel.getType())) {
            Collection<? extends GrantedAuthority> authorities =
                    roleHierarchy.getReachableGrantedAuthorities(authentication.getAuthorities());

            return authorities.stream().anyMatch(auth ->
                            auth.getAuthority().equals("ROLE_CHANNEL_MANAGER"));
        }

        DiscodeitUserDetails principal = (DiscodeitUserDetails) authentication.getPrincipal();

        // ChannelType이 PRIVATE일 때, 해당 채널 참여자만 삭제 가능
        return readStatusRepository.existsReadStatusByUserIdAndChannelId(
                principal.getUserDto().id(),
                channelId
        );
    }
}
