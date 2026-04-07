package com.sprint.mission.discodeit.service.basic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BasicMessageServiceTest {

  @Test
  @DisplayName("메시지는 정상적으로 생성되어야 합니다.")
  void create_success() {
    // given

    // when

    // then
  }

  @Test
  @DisplayName("존재하지 않는 채널에 메시지 생성 시 예외가 발생해야 합니다.")
  void create_fail_channel_not_found() {
    // given

    // when, then
  }

  @Test
  @DisplayName("메시지 내용은 정상적으로 수정되어야 합니다.")
  void update_success() {
    // given

    // when

    // then
  }

  @Test
  @DisplayName("존재하지 않는 메시지 수정 시 예외가 발생해야 합니다.")
  void update_fail_not_found() {
    // given

    // when, then
  }

  @Test
  @DisplayName("메시지는 정상적으로 삭제되어야 합니다.")
  void delete_success() {
    // given

    // when

    // then
  }

  @Test
  @DisplayName("존재하지 않는 메시지 삭제 시 예외가 발생해야 합니다.")
  void delete_fail_not_found() {
    // given

    // when, then
  }

  @Test
  @DisplayName("채널 ID로 메시지 목록을 정상적으로 조회해야 합니다.")
  void findAllByChannelId_success() {
    // given

    // when

    // then
  }

  @Test
  @DisplayName("존재하지 않는 채널의 메시지 목록 조회 시 빈 목록 또는 예외를 반환해야 합니다.")
  void findAllByChannelId_fail_channel_not_found() {
    // given

    // when, then
  }
}