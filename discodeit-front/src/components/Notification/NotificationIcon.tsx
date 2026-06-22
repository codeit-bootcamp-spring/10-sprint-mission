import styled from 'styled-components';
import useNotificationStore from '@/stores/notificationStore';
import { useEffect, useState } from 'react';
import NotificationSidebar from './NotificationSidebar';
import WebSocket from '@/components/WebSocket/WebSocket';
import { WS_DESTINATIONS } from '@/constants/websocket';
import useAuthStore from '@/stores/authStore';
import { useCallback } from 'react';

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
  const { notifications, fetchNotifications, newNotifications, addNewNotification } = useNotificationStore();
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const { currentUser } = useAuthStore();

  useEffect(() => {
    fetchNotifications();
  }, [fetchNotifications]);

  const unreadCount = notifications.length + newNotifications.length;

  const handleNewNotification = useCallback((data: any) => {
    addNewNotification(data);
  }, [addNewNotification]);

  return (
      <>
        {currentUser && (
          <WebSocket
            destination={WS_DESTINATIONS.SUB_NOTIFICATION(currentUser.id)}
            subscribeCallback={handleNewNotification}
          />
        )}
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
