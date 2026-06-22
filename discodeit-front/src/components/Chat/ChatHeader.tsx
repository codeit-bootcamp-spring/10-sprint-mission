import defaultProfile from '@/assets/default_profile.png';
import useBinaryContentStore from '@/stores/binaryContentStore';
import useUserListStore from '@/stores/userListStore';
import { ChannelDto, UserDto } from '@/types/api';
import { Avatar } from '../../styles/common';
import {
    GroupAvatarContainer,
    HeaderChannelName,
    HeaderLeft,
    HeaderPrivateInfo,
    HeaderStatusDot,
    NotificationIconButton,
    ParticipantCount,
    PrivateAvatarContainer,
    StyledChatHeader,
} from './styles';
import useAuthStore from '@/stores/authStore';
import useReadStatusStore from '@/stores/readStatusStore';
import { useCallback, useEffect, useState } from 'react';

interface ChatHeaderProps {
  channel: ChannelDto;
}

function ChatHeader({ channel }: ChatHeaderProps): JSX.Element | null {
  const { currentUser } = useAuthStore();
  const users = useUserListStore((state) => state.users);
  const binaryContents = useBinaryContentStore((state) => state.binaryContents);
  const { readStatuses, updateNotificationEnabled } = useReadStatusStore();
  const [notificationEnabled, setNotificationEnabled] = useState(false);

  useEffect(() => {
    if (readStatuses[channel?.id]) {
      setNotificationEnabled(readStatuses[channel.id].notificationEnabled);
    }
  }, [readStatuses, channel]);

  const toggleNotification = useCallback(async () => {
    if (!currentUser || !channel) return;

    const newNotificationEnabled = !notificationEnabled;
    setNotificationEnabled(newNotificationEnabled);

    try {
      await updateNotificationEnabled(channel.id, newNotificationEnabled);
    } catch (error) {
      console.error('알림 설정 업데이트 실패:', error);
      setNotificationEnabled(notificationEnabled); // 실패 시 이전 상태로 복구
    }
  }, [currentUser, channel, notificationEnabled, updateNotificationEnabled]);

  if (!channel) return null;

  // PUBLIC 채널
  if (channel.type === 'PUBLIC') {
    return (
      <StyledChatHeader>
        <HeaderLeft>
          <HeaderChannelName># {channel.name}</HeaderChannelName>
        </HeaderLeft>
        <NotificationIconButton onClick={toggleNotification} $enabled={notificationEnabled}>
          <svg
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
            <path d="M13.73 21a2 2 0 0 1-3.46 0" />
          </svg>
        </NotificationIconButton>
      </StyledChatHeader>
    );
  }

  // participants를 사용하여 실제 사용자 정보 가져오기
  const participants = channel.participants
    .map(participant => users.find(user => user.id === participant.id))
    .filter(Boolean) as UserDto[];

  const filteredParticipants = participants.filter(p => p.id !== currentUser?.id);

  // PRIVATE 채널
  const isGroup = participants.length > 2;
  const usernames = participants.filter(p => p.id !== currentUser?.id).map(p => p.username).join(', ');

  return (
    <StyledChatHeader>
      <HeaderLeft>
        <HeaderPrivateInfo>
          {isGroup ? (
            // 그룹 채팅
            <GroupAvatarContainer>
              {filteredParticipants.slice(0, 2).map((participant, index) => (
                <Avatar 
                  key={participant.id}
                  src={participant.profile ? binaryContents[participant.profile.id]?.url : defaultProfile}
                  style={{ 
                    position: 'absolute',
                    left: index * 16,
                    zIndex: 2 - index,
                    width: '24px',
                    height: '24px'
                  }}
                />
              ))}
            </GroupAvatarContainer>
          ) : (
            // 1:1 채팅
            <PrivateAvatarContainer>
              <Avatar 
                src={filteredParticipants[0].profile ? binaryContents[filteredParticipants[0].profile.id]?.url : defaultProfile} 
              />
              <HeaderStatusDot $online={filteredParticipants[0].online} />
            </PrivateAvatarContainer>
          )}
          <div>
            <HeaderChannelName>{usernames}</HeaderChannelName>
            {isGroup && (
              <ParticipantCount>멤버 {participants.length}명</ParticipantCount>
            )}
          </div>
        </HeaderPrivateInfo>
      </HeaderLeft>
      <NotificationIconButton onClick={toggleNotification} $enabled={notificationEnabled}>
        <svg
          width="20"
          height="20"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          strokeLinecap="round"
          strokeLinejoin="round"
        >
          <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
          <path d="M13.73 21a2 2 0 0 1-3.46 0" />
        </svg>
      </NotificationIconButton>
    </StyledChatHeader>
  );
}

export default ChatHeader; 