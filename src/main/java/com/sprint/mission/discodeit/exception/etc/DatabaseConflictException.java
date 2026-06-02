package com.sprint.mission.discodeit.exception.etc;

import com.sprint.mission.discodeit.exception.DiscodeitException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import java.util.Map;

public class DatabaseConflictException extends DiscodeitException {

    private DatabaseConflictException(Map<String, Object> details,
            Throwable cause) {
        super(ErrorCode.DATABASE_CONFLICT, details, cause);
    }

    public static DatabaseConflictException withUser(String username, String email, Throwable cause) {
        return new DatabaseConflictException(Map.of(
                "targetEntity", "User",
                "username", username != null ? username : "미변경",
                "email", email != null ? email : "미변경",
                "reason", "Database constraint violation (Likely Race Condition)",
                "requestedAt", java.time.Instant.now().toString()
        ), cause);
    }
}
