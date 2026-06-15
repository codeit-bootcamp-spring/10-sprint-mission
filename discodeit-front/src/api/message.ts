import client from './client';
import {
  MessageCreateRequest,
  MessageDto,
  MessageUpdateRequest,
  Pageable,
  PageResponse
} from '../types/api';

export const getMessages = async (channelId: string, cursor: string | null, pageable: Pageable): Promise<PageResponse<MessageDto>> => {
  const response = await client.get<PageResponse<MessageDto>>(`/messages`, {
    params: {
      channelId,
      cursor,
      size: pageable.size,
      sort: pageable.sort?.join(',')
    }
  });
  return response.data;
};

export const createMessage = async (messageData: MessageCreateRequest): Promise<MessageDto> => {
  const response = await client.post<MessageDto>('/messages', messageData, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const updateMessage = async (messageId: string, updateData: MessageUpdateRequest): Promise<MessageDto> => {
  const response = await client.patch<MessageDto>(`/messages/${messageId}`, updateData);
  return response.data;
};

export const deleteMessage = async (messageId: string): Promise<void> => {
  await client.delete(`/messages/${messageId}`);
}; 
