package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.List;

public record BinaryContentRequestDto(
        // 유저와 채널에 데이터를 전달하기 위한 dto.
        // 유저 프로필 등록 목적일 경우 0번 인덱스만 사용함.(프로필 사진은 하나) 메시지 전송 시 리스트로 활용하기 위해 리스트로 정의함.
        List<byte[]> content
) {
}
