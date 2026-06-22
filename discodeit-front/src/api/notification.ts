import { NotificationDto } from '@/types/api';
import client from './client';

export const getNotifications = async (): Promise<NotificationDto[]> => {
  const response = await client.get<NotificationDto[]>('/notifications');
  return response.data;
};

export const readNotification = async (id: string): Promise<void> => {
  await client.delete(`/notifications/${id}`);
};
