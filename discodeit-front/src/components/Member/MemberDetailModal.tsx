import { useEffect, useState } from 'react';
import styled from 'styled-components';
import { theme } from '@/styles/theme'
import { Role, UserDto } from '@/types/api';
import defaultProfile from '@/assets/default_profile.png';
import useBinaryContentStore from '@/stores/binaryContentStore';
import useAuthStore from '@/stores/authStore';

interface MemberDetailModalProps {
  member: UserDto;
  onClose: () => void;
}

function MemberDetailModal({ member, onClose }: MemberDetailModalProps) {
  const { binaryContents, fetchBinaryContent } = useBinaryContentStore();
  const { currentUser, updateUserRole } = useAuthStore();
  const [role, setRole] = useState(member.role);
  const [isRoleEditing, setIsRoleEditing] = useState(false);
  
  useEffect(() => {
    if (member.profile?.id && !binaryContents[member.profile.id]) {
      fetchBinaryContent(member.profile.id);
    }
  }, [member.profile?.id, binaryContents, fetchBinaryContent]);

  const roleDisplay = {
    [Role.USER]: {
      name: '사용자',
      color: '#2ed573'
    },
    [Role.CHANNEL_MANAGER]: {
      name: '채널 관리자',
      color: '#ff4757'
    },
    [Role.ADMIN]: {
      name: '어드민',
      color: '#0097e6'
    }
  }

  const handleRoleChange = (role: Role) => {
    setRole(role);
    setIsRoleEditing(true);
  }

  const handleSaveRole = () => {
    updateUserRole(member.id, role);
    setIsRoleEditing(false);
  }

  return (
    <StyledModal onClick={onClose}>
      <ModalContent onClick={e => e.stopPropagation()}>
        <h2>사용자 정보</h2>
        <ProfileSection>
          <ProfileImage 
            src={member.profile?.id ? binaryContents[member.profile.id]?.url || defaultProfile : defaultProfile}
            alt={member.username} 
          />
          <UserName>{member.username}</UserName>
          <UserEmail>{member.email}</UserEmail>
          <StatusBadge $online={member.online}>
            {member.online ? '온라인' : '오프라인'}
          </StatusBadge>
          {currentUser?.role === Role.ADMIN ? (
            <Select 
              value={role}
              onChange={(e) => handleRoleChange(e.target.value as Role)}
            >
              {Object.entries(roleDisplay).map(([role, display]) => (
                <option key={role} value={role} style={{ marginTop: '8px', textAlign: 'center' }}>
                  {display.name}
                </option>
              ))}
            </Select>
          ) : (
            <RoleBadge style={{ backgroundColor: roleDisplay[member.role].color }}>
              {roleDisplay[member.role].name}
            </RoleBadge>
          )}

        </ProfileSection>
        <ButtonGroup>
            {
                currentUser?.role === Role.ADMIN && isRoleEditing && (
                    <Button onClick={handleSaveRole} disabled={!isRoleEditing} $secondary={!isRoleEditing}>저장</Button>
                )
            }
        </ButtonGroup>
      </ModalContent>
    </StyledModal>
  );
}


const RoleBadge = styled.div`
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  color: white;
  margin-top: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.3px;
`;

const Select = styled.select`
  padding: 10px 16px;
  border-radius: 8px;
  border: 1.5px solid ${theme.colors.border.primary};
  background: ${theme.colors.background.primary};
  color: ${theme.colors.text.primary};
  font-size: 14px;
  width: 140px;
  cursor: pointer;
  transition: all 0.2s ease;
  margin-top: 12px;
  font-weight: 500;

  &:hover {
    border-color: ${theme.colors.brand.primary};
  }

  &:focus {
    outline: none;
    border-color: ${theme.colors.brand.primary};
    box-shadow: 0 0 0 2px ${theme.colors.brand.primary}20;
  }

  option {
    background: ${theme.colors.background.primary};
    color: ${theme.colors.text.primary};
    padding: 12px;
  }
`;


const StyledModal = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
`;

const ModalContent = styled.div`
  background: ${theme.colors.background.secondary};
  padding: 40px;
  border-radius: 16px;
  width: 100%;
  max-width: 420px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);

  h2 {
    color: ${theme.colors.text.primary};
    margin-bottom: 32px;
    text-align: center;
    font-size: 26px;
    font-weight: 600;
    letter-spacing: -0.5px;
  }
`;

const ProfileSection = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 32px;
  padding: 24px;
  background: ${theme.colors.background.primary};
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
`;

const ProfileImage = styled.img`
  width: 140px;
  height: 140px;
  border-radius: 50%;
  margin-bottom: 20px;
  object-fit: cover;
  border: 4px solid ${theme.colors.background.secondary};
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
`;

const UserName = styled.div`
  font-size: 22px;
  font-weight: 600;
  color: ${theme.colors.text.primary};
  margin-bottom: 8px;
  letter-spacing: -0.3px;
`;

const UserEmail = styled.div`
  font-size: 14px;
  color: ${theme.colors.text.muted};
  margin-bottom: 16px;
  font-weight: 500;
`;

const StatusBadge = styled.div<{ $online: boolean }>`
  padding: 6px 16px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 600;
  background-color: ${({ $online, theme }) => 
    $online ? theme.colors.status.online : theme.colors.status.offline};
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.3px;
`;


const ButtonGroup = styled.div`
  display: flex;
  gap: 12px;
  margin-top: 24px;
`;

interface ButtonProps {
    $secondary?: boolean;
}


const Button = styled.button<ButtonProps>`
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 8px;
  background: ${({ $secondary, theme }) => 
    $secondary ? 'transparent' : theme.colors.brand.primary};
  color: ${({ $secondary, theme }) => 
    $secondary ? theme.colors.text.primary : 'white'};
  cursor: pointer;
  font-weight: 600;
  font-size: 15px;
  transition: all 0.2s ease;
  border: ${({ $secondary, theme }) => 
    $secondary ? `1.5px solid ${theme.colors.border.primary}` : 'none'};
  
  &:hover {
    background: ${({ $secondary, theme }) => 
      $secondary ? theme.colors.background.hover : theme.colors.brand.hover};
    transform: translateY(-1px);
  }

  &:active {
    transform: translateY(0);
  }
`;

export default MemberDetailModal;
