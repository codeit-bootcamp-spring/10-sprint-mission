package com.sprint.mission.discodeit.binarycontent.exception;

import com.sprint.mission.discodeit.exception.BusinessException;

public class BinaryContentNotFoundException extends BusinessException {
    public BinaryContentNotFoundException() {
        super("해당 콘텐츠를 찾을 수 없습니다.");
    }
}
