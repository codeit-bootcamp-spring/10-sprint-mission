package com.sprint.mission.discodeit.service;

import java.util.List;
import java.util.UUID;

import com.sprint.mission.discodeit.dto.MessagePatchDto;
import com.sprint.mission.discodeit.dto.MessagePostDto;
import com.sprint.mission.discodeit.dto.MessageResponseDto;

/**
 * 수정, 삭제 메서드를 작성할 때 파라미터로 객체 자체 보다는 id를 받는 것이 좋다.
 * 객체의 경우 출처를 알기 어렵고 id만 같은 가짜 객체가 넘어올 수도 있다.
 * (equals, hashCode에서 id를 보는 경우)
 * id만 전달하여 서비스 메서드가 해당 id를 갖는 객체를 직접 찾도록 한다.
 * 또한 id의 본래 역할(식별자)을 생각하면 id 전달히 더 타당하다고 볼 수 있다.
 */
public interface MessageService {
	MessageResponseDto create(MessagePostDto messagePostDTO);

	MessageResponseDto findById(UUID id); // uuid로 단일 메시지 조회하기

	List<MessageResponseDto> findByUser(UUID userId); // 특정 유저의 전체 메시지 조회하기

	List<MessageResponseDto> findByChannelId(UUID channelId); // 특정 채닐의 전체 메시지 조회하기

	MessageResponseDto updateById(UUID messageId, MessagePatchDto messagePatchDTO); // 메시지 내용 수정

	void delete(UUID messageId);
}
