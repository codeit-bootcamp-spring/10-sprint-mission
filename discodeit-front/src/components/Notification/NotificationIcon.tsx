import styled from 'styled-components';
import useNotificationStore from '@/stores/notificationStore';
import { useEffect, useState } from 'react';
import NotificationSidebar from './NotificationSidebar';

const IconContainer = styled.div`
  position: relative;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: background-color 0.2s ease;

  &:hover {
    background-color: ${({ theme }) => theme.colors.background.hover};
  }
`;

const NotificationBadge = styled.div`
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: ${({ theme }) => theme.colors.status.error};
  color: white;
  font-size: 12px;
  font-weight: 600;
  min-width: 18px;
  height: 18px;
  border-radius: 9px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 4px;
  transform: translate(25%, -25%);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
`;

const NotificationIcon = () => {
  const { notifications, fetchNotifications } = useNotificationStore();
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  useEffect(() => {
    fetchNotifications();
    const pollingInterval = setInterval(fetchNotifications, 10000);
    return () => clearInterval(pollingInterval);
  }, [fetchNotifications]);

  const unreadCount = notifications.length;

  return (
      <>
        <IconContainer onClick={() => setIsSidebarOpen(true)}>
          <svg
              width="24"
              height="24"
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
          {unreadCount > 0 && (
              <NotificationBadge>
                {unreadCount > 99 ? '99+' : unreadCount}
              </NotificationBadge>
          )}
        </IconContainer>
        <NotificationSidebar
            isOpen={isSidebarOpen}
            onClose={() => setIsSidebarOpen(false)}
        />
      </>
  );
};

export default NotificationIcon;