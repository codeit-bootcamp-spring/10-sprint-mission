import client from './client';
import { ReadStatusDto, ReadStatusCreateRequest, ReadStatusUpdateRequest } from '../types/api';

export const getReadStatuses = async (userId: string): Promise<ReadStatusDto[]> => {
  const response = await client.get<ReadStatusDto[]>('/readStatuses', {
    params: { userId }
  });
  return response.data;
};

export const updateReadStatus = async (readStatusId: string, {newLastReadAt, newNotificationEnabled}: {newLastReadAt: string | null, newNotificationEnabled: boolean | null}): Promise<ReadStatusDto> => {
  const updateRequest: ReadStatusUpdateRequest = {
    newLastReadAt,
    newNotificationEnabled
  };
  const response = await client.patch<ReadStatusDto>(`/readStatuses/${readStatusId}`, updateRequest);
  return response.data;
};

export const createReadStatus = async (userId: string, channelId: string, lastReadAt: string): Promise<ReadStatusDto> => {
  const createRequest: ReadStatusCreateRequest = {
    userId,
    channelId,
    lastReadAt
  };
  const response = await client.post<ReadStatusDto>('/readStatuses', createRequest);
  return response.data;
}; 