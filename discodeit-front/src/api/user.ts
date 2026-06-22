import client from './client';
import {UserDto, UserUpdateRequest} from '../types/api';

export const updateUser = async (userId: string, data: UserUpdateRequest): Promise<UserDto> => {
  const response = await client.patch<UserDto>(`/users/${userId}`, data, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const getUsers = async (): Promise<UserDto[]> => {
  const response = await client.get<UserDto[]>('/users');
  return response.data;
};
