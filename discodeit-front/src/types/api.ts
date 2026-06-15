// User 관련 타입
export interface UserDto {
  id: string; // UUID
  username: string;
  email: string;
  profile?: BinaryContentDto;
  online: boolean;
  role: Role;
}

export enum Role {
  USER = 'USER',
  CHANNEL_MANAGER = 'CHANNEL_MANAGER',
  ADMIN = 'ADMIN'
}

export interface UserCreateRequest {
  username: string;
  email: string;
  password: string;
  profileId?: string;
}

export interface UserUpdateRequest {
  newUsername?: string;
  newEmail?: string;
  newPassword?: string;
  newProfileId?: string;
}

// Channel 관련 타입
export interface ChannelDto {
  id: string; // UUID
  type: 'PUBLIC' | 'PRIVATE';
  name: string;
  description: string;
  participants: UserDto[];
  lastMessageAt: string;
}

export interface PublicChannelCreateRequest {
  name: string;
  description: string;
}

export interface PrivateChannelCreateRequest {
  participantIds: string[]; // UUID 배열
}

export interface PublicChannelUpdateRequest {
  newName?: string;
  newDescription?: string;
}

// Message 관련 타입
export interface MessageDto {
  id: string; // UUID
  createdAt: string;
  updatedAt: string;
  content: string;
  channelId: string; // UUID
  author: UserDto;
  attachments: BinaryContentDto[];
}

export interface MessageCreateRequest {
  content: string;
  channelId: string; // UUID
  authorId: string; // UUID
  attachmentIds?: string[];
}

export interface MessageUpdateRequest {
  newContent: string;
}

// ReadStatus 관련 타입
export interface ReadStatusDto {
  id: string; // UUID
  userId: string; // UUID
  channelId: string; // UUID
  lastReadAt: string;
  notificationEnabled: boolean; // 알림 설정 여부
}

export interface ReadStatusCreateRequest {
  userId: string; // UUID
  channelId: string; // UUID
  lastReadAt: string;
}

export interface ReadStatusUpdateRequest {
  newLastReadAt: string | null;
  newNotificationEnabled: boolean | null;
}

// BinaryContent 관련 타입
export interface BinaryContentDto {
  id: string; // UUID
  fileName: string;
  size: number;
  contentType: string;
  status: BinaryContentStatus;
}

export enum BinaryContentStatus {
  PROCESSING= 'PROCESSING',
  SUCCESS= 'SUCCESS',
  FAIL= 'FAIL',
  DELETED = 'DELETED',
}

// Auth 관련 타입
export interface LoginRequest {
  username: string;
  password: string;
}

// 페이징 관련 타입
export interface Pageable {
  size: number;
  sort?: string[];
}

export interface PageResponse<T> {
  content: T[];
  nextCursor: string | null;
  size: number;
  hasNext: boolean;
  totalElements: number;
} 

export interface UserRoleUpdateRequest {
  userId: string;
  newRole: Role;
}

export interface JwtDto {
  userDto: UserDto,
  accessToken: string;
}

export interface NotificationDto {
  id: string;
  createdAt: string;
  receiverId: string;
  title: string;
  content: string;
}
