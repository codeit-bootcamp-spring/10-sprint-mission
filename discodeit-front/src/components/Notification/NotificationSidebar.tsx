import styled from 'styled-components';
import useNotificationStore from '@/stores/notificationStore';
import { NotificationDto } from '@/types/api';
import { theme } from '@/styles/theme';
import dayjs from 'dayjs';
import relativeTime from 'dayjs/plugin/relativeTime';
import 'dayjs/locale/ko';
import {useState} from "react";

// 2. 플러그인 확장 및 한국어 로케일 설정
dayjs.extend(relativeTime);
dayjs.locale('ko');

interface NotificationSidebarProps {
  isOpen: boolean;
  onClose: () => void;
}

const SidebarOverlay = styled.div<{ $isOpen: boolean }>`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  opacity: ${({ $isOpen }) => ($isOpen ? 1 : 0)};
  visibility: ${({ $isOpen }) => ($isOpen ? 'visible' : 'hidden')};
  transition: all 0.3s ease;
  z-index: 1000;
`;

const SidebarContainer = styled.div<{ $isOpen: boolean }>`
  position: fixed;
  top: 0;
  right: 0;
  width: 360px;
  height: 100vh;
  background: ${({ theme }) => theme.colors.background.primary};
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
  transform: translateX(${({ $isOpen }) => ($isOpen ? '0' : '100%')});
  transition: transform 0.3s ease;
  z-index: 1001;
  display: flex;
  flex-direction: column;
`;

const SidebarHeader = styled.div`
  padding: 0px 16px;
  height: 48px;
  font-size: 14px;
  font-weight: bold;
  color: ${theme.colors.text.muted};
  text-transform: uppercase;
  border-bottom: 1px solid ${({ theme }) => theme.colors.border.primary};
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

const SidebarTitle = styled.h2`
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: ${({ theme }) => theme.colors.text.primary};
  text-transform: none;
`;

const CloseButton = styled.button`
  background: none;
  border: none;
  padding: 8px;
  cursor: pointer;
  color: ${({ theme }) => theme.colors.text.muted};
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;

  &:hover {
    background: ${({ theme }) => theme.colors.background.hover};
    color: ${({ theme }) => theme.colors.text.primary};
  }
`;

const NotificationList = styled.div`
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  width: 100%;
  box-sizing: border-box;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-track {
    background: ${({ theme }) => theme.colors.background.primary};
  }

  &::-webkit-scrollbar-thumb {
    background: ${({ theme }) => theme.colors.border.primary};
    border-radius: 3px;
  }

  &::-webkit-scrollbar-thumb:hover {
    background: ${({ theme }) => theme.colors.text.muted};
  }
`;

const NotificationItemContent = styled.div`
  position: relative;
  flex: 1;
`;

const CopyButton = styled.button`
  position: absolute;
  top: 0;
  right: 0;
  background: none;
  border: none;
  padding: 4px;
  cursor: pointer;
  color: ${({ theme }) => theme.colors.text.muted};
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
  opacity: 0;

  &:hover {
    background: ${({ theme }) => theme.colors.background.hover};
    color: ${({ theme }) => theme.colors.text.primary};
  }
`;

const NotificationItem = styled.div`
  background: ${({ theme }) => theme.colors.background.primary};
  border-radius: 8px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.2s ease;
  border-left: 4px solid ${({ theme }) => theme.colors.brand.primary};
  width: 100%;
  box-sizing: border-box;
  position: relative;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

    ${CopyButton} {
      opacity: 1;
    }
  }
`;

const NotificationTitle = styled.h4`
  color: ${({ theme }) => theme.colors.text.primary};
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 600;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 300px;
  text-transform: none;
`;

const NotificationContent = styled.p`
  color: ${({ theme }) => theme.colors.text.secondary};
  margin: 0 0 8px 0;
  font-size: 14px;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 300px;
  word-break: break-word;
  text-transform: none;
`;

const NotificationTime = styled.span`
  color: ${({ theme }) => theme.colors.text.muted};
  font-size: 12px;
`;

const EmptyState = styled.div`
  text-align: center;
  padding: 32px 16px;
  color: ${({ theme }) => theme.colors.text.muted};
  font-size: 14px;
`;

const CopyTooltip = styled.div<{ $show: boolean }>`
  position: absolute;
  top: -30px;
  right: 0;
  background: ${({ theme }) => theme.colors.background.secondary};
  color: ${({ theme }) => theme.colors.text.primary};
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  opacity: ${({ $show }) => ($show ? 1 : 0)};
  transition: opacity 0.2s ease;
  pointer-events: none;
  white-space: nowrap;
`;

const NotificationSidebar = ({ isOpen, onClose }: NotificationSidebarProps) => {
  const { notifications, readNotification } = useNotificationStore();
  const [copyTooltipId, setCopyTooltipId] = useState<string | null>(null);

  const handleNotificationClick = async (notification: NotificationDto) => {
    await readNotification(notification.id);
  };

  const handleCopyClick = async (e: React.MouseEvent, content: string, notificationId: string) => {
    e.stopPropagation(); // 알림 클릭 이벤트 전파 방지
    try {
      await navigator.clipboard.writeText(content);
      setCopyTooltipId(notificationId);
      setTimeout(() => setCopyTooltipId(null), 2000); // 2초 후 툴팁 숨김
    } catch (err) {
      console.error('클립보드 복사 실패:', err);
    }
  };

  return (
      <>
        <SidebarOverlay $isOpen={isOpen} onClick={onClose} />
        <SidebarContainer $isOpen={isOpen}>
          <SidebarHeader>
            <SidebarTitle>
              알림 {notifications.length > 0 && `(${notifications.length})`}
            </SidebarTitle>
            <CloseButton onClick={onClose}>
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
                <line x1="18" y1="6" x2="6" y2="18" />
                <line x1="6" y1="6" x2="18" y2="18" />
              </svg>
            </CloseButton>
          </SidebarHeader>
          <NotificationList>
            {notifications.length === 0 ? (
                <EmptyState>새로운 알림이 없습니다</EmptyState>
            ) : (
                notifications.map((notification) => (
                    <NotificationItem
                        key={notification.id}
                        onClick={() => handleNotificationClick(notification)}
                    >
                      <NotificationItemContent>
                        <NotificationTitle>{notification.title}</NotificationTitle>
                        <NotificationContent>{notification.content}</NotificationContent>
                        <NotificationTime>
                          {dayjs(new Date(notification.createdAt)).fromNow()}
                        </NotificationTime>
                        <CopyButton
                            onClick={(e) => handleCopyClick(e, notification.content, notification.id)}
                            title="내용 복사"
                        >
                          <svg
                              width="16"
                              height="16"
                              viewBox="0 0 24 24"
                              fill="none"
                              stroke="currentColor"
                              strokeWidth="2"
                              strokeLinecap="round"
                              strokeLinejoin="round"
                          >
                            <rect x="9" y="9" width="13" height="13" rx="2" ry="2" />
                            <path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1" />
                          </svg>
                        </CopyButton>
                        <CopyTooltip $show={copyTooltipId === notification.id}>
                          복사되었습니다
                        </CopyTooltip>
                      </NotificationItemContent>
                    </NotificationItem>
                ))
            )}
          </NotificationList>
        </SidebarContainer>
      </>
  );
};

export default NotificationSidebar;