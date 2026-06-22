import defaultProfile from '@/assets/default_profile.png';
import useBinaryContentStore from '@/stores/binaryContentStore';
import { ChannelDto, Role } from '@/types/api';
import { Avatar, StatusDot } from '../../styles/common';
import {
    GroupAvatarContainer,
    ParticipantCount,
    PrivateChannelAvatar,
    PrivateChannelName,
    PrivateChannelWrapper,
    StyledChannelItem,
    TextContainer,
    ChannelMenuContainer,
    ChannelMenuButton,
    ChannelMenuDropdown,
    ChannelMenuItem,
} from './styles';
import useAuthStore from '@/stores/authStore';
import useChannelStore from '@/stores/channelStore';
import { useState, useEffect } from 'react';
import ChannelEditModal from './ChannelEditModal';

interface ChannelItemProps {
  channel: ChannelDto;
  isActive: boolean;
  onClick: () => void;
  hasUnread: boolean;
}

export function ChannelItem({ channel, isActive, onClick, hasUnread }: ChannelItemProps): JSX.Element {
  const { currentUser } = useAuthStore();
  const { binaryContents } = useBinaryContentStore();
  const { deleteChannel } = useChannelStore();
  const [openMenuId, setOpenMenuId] = useState<string | null>(null);
  const [isEditModalOpen, setIsEditModalOpen] = useState(false);

  // Check if current user has permission to manage channels
  const canManageChannel = currentUser?.role === Role.ADMIN || currentUser?.role === Role.CHANNEL_MANAGER;

  // Close menu when clicking outside
  useEffect(() => {
    const handleClickOutside = () => {
      if (openMenuId) {
        setOpenMenuId(null);
      }
    };

    if (openMenuId) {
      document.addEventListener('click', handleClickOutside);
      return () => document.removeEventListener('click', handleClickOutside);
    }
  }, [openMenuId]);

  const handleMenuToggle = (channelId: string) => {
    setOpenMenuId(openMenuId === channelId ? null : channelId);
  };

  const handleUpdateChannel = () => {
    setOpenMenuId(null);
    setIsEditModalOpen(true);
  };

  const handleEditSuccess = (updatedChannel: ChannelDto) => {
    setIsEditModalOpen(false);
    console.log('Channel updated successfully:', updatedChannel);
  };

  const handleCloseEditModal = () => {
    setIsEditModalOpen(false);
  };

  const handleDeleteChannel = async (channelId: string) => {
    setOpenMenuId(null);
    
    const channelName = channel.type === 'PUBLIC' 
      ? channel.name 
      : channel.type === 'PRIVATE' && channel.participants.length > 2
        ? `그룹 채팅 (멤버 ${channel.participants.length}명)`
        : channel.participants.filter(p => p.id !== currentUser?.id)[0]?.username || '1:1 채팅';
    
    if (confirm(`"${channelName}" 채널을 삭제하시겠습니까?`)) {
      try {
        await deleteChannel(channelId);
        console.log('Channel deleted successfully:', channelId);
      } catch (error) {
        console.error('Channel delete failed:', error);
        alert('채널 삭제에 실패했습니다. 다시 시도해주세요.');
      }
    }
  };

  let channelComponent: JSX.Element;

  if (channel.type === 'PUBLIC') {
    channelComponent = (
      <StyledChannelItem $isActive={isActive} onClick={onClick} $hasUnread={hasUnread}>
        # {channel.name}
        {canManageChannel && (
          <ChannelMenuContainer>
            <ChannelMenuButton
              onClick={(e) => {
                e.stopPropagation();
                handleMenuToggle(channel.id);
              }}
            >
              ⋯
            </ChannelMenuButton>
            {openMenuId === channel.id && (
              <ChannelMenuDropdown onClick={(e) => e.stopPropagation()}>
                <ChannelMenuItem
                  onClick={() => handleUpdateChannel()}
                >
                  ✏️ 수정
                </ChannelMenuItem>
                <ChannelMenuItem
                  onClick={() => handleDeleteChannel(channel.id)}
                >
                  🗑️ 삭제
                </ChannelMenuItem>
              </ChannelMenuDropdown>
            )}
          </ChannelMenuContainer>
        )}
      </StyledChannelItem>
    );
  }

  else {
    const participants = channel.participants;
    // 그룹 채팅인 경우
    if (participants.length > 2) {
      const usernames = participants.filter(p => p.id !== currentUser?.id).map(p => p.username).join(', ');
      channelComponent = (
        <PrivateChannelWrapper $isActive={isActive} onClick={onClick}>
          <GroupAvatarContainer>
            {participants.filter(p => p.id !== currentUser?.id).slice(0, 2).map((participant, index) => (
              <Avatar 
                key={participant.id}
                src={participant.profile ? binaryContents[participant.profile.id]?.url : defaultProfile}
                style={{ 
                  position: 'absolute',
                  left: index * 16,
                  zIndex: 2 - index,
                  width: '24px',
                  height: '24px',
                  border: '2px solid #2a2a2a'
                }}
              />
            ))}
          </GroupAvatarContainer>
          <TextContainer>
            <PrivateChannelName $hasUnread={hasUnread}>{usernames}</PrivateChannelName>
            <ParticipantCount>멤버 {participants.length}명</ParticipantCount>
          </TextContainer>
          {canManageChannel && (
            <ChannelMenuContainer>
              <ChannelMenuButton
                onClick={(e) => {
                  e.stopPropagation();
                  handleMenuToggle(channel.id);
                }}
              >
                ⋯
              </ChannelMenuButton>
              {openMenuId === channel.id && (
                <ChannelMenuDropdown onClick={(e) => e.stopPropagation()}>
                  <ChannelMenuItem
                    onClick={() => handleDeleteChannel(channel.id)}
                  >
                    🗑️ 삭제
                  </ChannelMenuItem>
                </ChannelMenuDropdown>
              )}
            </ChannelMenuContainer>
          )}
        </PrivateChannelWrapper>
      );
    }

    // 1:1 채팅인 경우
    else {
      const participant = participants.filter(p => p.id !== currentUser?.id)[0];
      channelComponent = (
        participant ? (
        <PrivateChannelWrapper $isActive={isActive} onClick={onClick}>
          <PrivateChannelAvatar>
            <Avatar 
              src={participant.profile ? binaryContents[participant.profile.id]?.url : defaultProfile} 
              alt="profile" 
            />
            <StatusDot $online={participant.online} />
          </PrivateChannelAvatar>
          <TextContainer>
            <PrivateChannelName $hasUnread={hasUnread}>{participant.username}</PrivateChannelName>
          </TextContainer>
          {canManageChannel && (
            <ChannelMenuContainer>
              <ChannelMenuButton
                onClick={(e) => {
                  e.stopPropagation();
                  handleMenuToggle(channel.id);
                }}
              >
                ⋯
              </ChannelMenuButton>
              {openMenuId === channel.id && (
                <ChannelMenuDropdown onClick={(e) => e.stopPropagation()}>
                  <ChannelMenuItem
                    onClick={() => handleDeleteChannel(channel.id)}
                  >
                    🗑️ 삭제
                  </ChannelMenuItem>
                </ChannelMenuDropdown>
              )}
            </ChannelMenuContainer>
          )}
        </PrivateChannelWrapper>
        ) : <div></div>
      );
    }
  }

  return (
    <>
      {channelComponent}
      <ChannelEditModal
        isOpen={isEditModalOpen}
        channel={channel}
        onClose={handleCloseEditModal}
        onUpdateSuccess={handleEditSuccess}
      />
    </>
  );
} 