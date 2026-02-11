package com.sprint.mission.discodeit.entity;

public enum ChannelType {
    PUBLIC,  // 공개 채널. 누구나 접근하고 볼 수 있는 개방형 소통 공간 -> 이름이 있는 방을 만들고 누구나 들어오는 개념
    PRIVATE, // 비공개 채널. 특정 사용자들끼리만 소통하는 개인적인 공간 (1:1대화, 그룹 채팅) -> 사람들을 초대해서 방을 만들며, 방의 이름보다는 누구와 대화하는지가 중요한 개념
}
