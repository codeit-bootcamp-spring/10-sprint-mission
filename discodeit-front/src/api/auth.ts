import client from './client';
import { UserDto, Role, UserRoleUpdateRequest } from '../types/api';

export const login = async (username: string, password: string, rememberMe?: boolean): Promise<UserDto> => {
  const formData = new FormData();
  formData.append('username', username);
  formData.append('password', password);
  const response = await client.post<UserDto>('/auth/login', formData, {params: {'remember-me': rememberMe ? 'true' : 'false'}, headers: {'Content-Type': 'multipart/form-data'}});
  return response.data;
};

export const signup = async (formData: FormData): Promise<UserDto> => {
  const response = await client.post<UserDto>('/users', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
  return response.data;
};

export const getCsrfToken = async (): Promise<void> => {
  await client.get('/auth/csrf-token');
};

export const me = async (): Promise<UserDto> => {
  const response = await client.get<UserDto>('/auth/me');
  return response.data;
};

export const logout = async (): Promise<void> => {
  await client.post('/auth/logout');
};

export const updateUserRole = async (userId: string, role: Role): Promise<void> => {
  const request: UserRoleUpdateRequest = {
    userId,
    newRole: role
  };
  const response = await client.put<void>(`/auth/role`, request);
  return response.data;
};