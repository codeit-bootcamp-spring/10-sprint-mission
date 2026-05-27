package com.sprint.mission.discodeit;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.test.context.support.WithSecurityContext;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockDiscodeitUserSecurityContextFactory.class)
public @interface WithMockDiscodeitUser {

    String username() default "달선";

    String role() default "USER";

    String userId() default "00000000-0000-0000-0000-000000000001";
}
