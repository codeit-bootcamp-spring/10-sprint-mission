package com.sprint.mission.discodeit.exception.auth;

import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class JwtGenerationException extends JwtException {
    public JwtGenerationException(String reason) {
        super(ErrorCode.JWT_GENERATION_FAILED, Map.of("reason", reason));
    }
}
