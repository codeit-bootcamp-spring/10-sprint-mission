package com.sprint.mission.discodeit.interceptor;

import com.sprint.mission.discodeit.auth.jwt.JwtRegistry;
import com.sprint.mission.discodeit.auth.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider  jwtTokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public @Nullable Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            String bearerToken = accessor.getFirstNativeHeader("Authorization");
            String token = resolveToken(bearerToken);
            if(!jwtTokenProvider.validateToken(token)){
                throw new IllegalArgumentException("Invalid Token");
            }
            if(jwtRegistry.hasActiveJwtInformationByAccessToken(token)){
                UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) jwtTokenProvider.getAuthentication(token);
                accessor.setUser(authentication);
            }
        }
        return message;
    }

    private String resolveToken(String bearerToken){
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}
