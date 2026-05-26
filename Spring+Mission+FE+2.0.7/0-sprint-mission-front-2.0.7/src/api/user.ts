import client from './client';
import {UserDto} from '../types/api';

export const updateUser = async (userId: string, formData: FormData): Promise<UserDto> => {
  const response = await client.patch<UserDto>(`/users/${userId}`, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
  return response.data;
};

export const getUsers = async (): Promise<UserDto[]> => {
  const response = await client.get<UserDto[]>('/users');
  return response.data;
};