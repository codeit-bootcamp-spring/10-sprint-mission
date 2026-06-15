import client, {clientWithoutAuthorization} from './client';
import {JwtDto, Role, UserCreateRequest, UserDto, UserRoleUpdateRequest} from '../types/api';

export const login = async (username: string, password: string): Promise<JwtDto> => {
  const formData = new FormData();
  formData.append('username', username);
  formData.append('password', password);
  const response = await clientWithoutAuthorization.post<JwtDto>('/auth/login', formData, {headers: {'Content-Type': 'multipart/form-data'}});
  return response.data;
};

export const signup = async (formData: UserCreateRequest): Promise<UserDto> => {
  const response = await clientWithoutAuthorization.post<UserDto>('/users', formData, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return response.data;
};

export const getCsrfToken = async (): Promise<void> => {
  await clientWithoutAuthorization.get('/auth/csrf-token');
};

export const logout = async (): Promise<void> => {
  await clientWithoutAuthorization.post('/auth/logout');
};

export const refreshToken = async (): Promise<JwtDto> => {
  const response = await clientWithoutAuthorization.post('/auth/refresh');
  return response.data;
};

export const updateUserRole = async (userId: string, role: Role): Promise<void> => {
  const request: UserRoleUpdateRequest = {
    userId,
    newRole: role
  };
  const response = await client.put<void>(`/auth/role`, request);
  return response.data;
};
