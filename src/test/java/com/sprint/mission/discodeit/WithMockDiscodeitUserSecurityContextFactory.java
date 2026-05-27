package com.sprint.mission.discodeit;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.security.Role;
import java.util.UUID;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockDiscodeitUserSecurityContextFactory
        implements WithSecurityContextFactory<WithMockDiscodeitUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockDiscodeitUser annotation) {
        UserDto userDto = new UserDto(
                UUID.fromString(annotation.userId()),
                annotation.username(),
                "test@test.com",
                null,
                false,
                Role.valueOf(annotation.role())
        );
        DiscodeitUserDetails userDetails = new DiscodeitUserDetails(userDto, "password");
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(userDetails, null,
                        userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        return context;
    }
}
