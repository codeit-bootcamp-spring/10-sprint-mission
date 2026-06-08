import { create } from 'zustand';
import { getReadStatuses, updateReadStatus as apiUpdateReadStatus, createReadStatus } from '../api/readStatus';
import { ReadStatusDto, UserDto } from '../types/api';
import useAuthStore from './authStore';

interface ReadStatusInfo {
  id: string;
  lastReadAt: string;
}

interface ReadStatusMap {
  [channelId: string]: ReadStatusInfo;
}

interface ReadStatusStore {
  readStatuses: ReadStatusMap;
  fetchReadStatuses: (currentUser?: UserDto) => Promise<void>;
  updateReadStatus: (channelId: string, currentUser?: UserDto) => Promise<void>;
  hasUnreadMessages: (channelId: string, lastMessageAt: string) => boolean;
}

const useReadStatusStore = create<ReadStatusStore>((set, get) => ({
  readStatuses: {},  // { channelId: { id, lastReadAt } } 형태로 저장
  
  fetchReadStatuses: async () => {
    try {
      const { currentUser } = useAuthStore.getState();
      if (!currentUser) return;

      const statuses = await getReadStatuses(currentUser.id);
      
      // 채널 ID를 key로 하는 객체로 변환
      const statusMap = statuses.reduce<ReadStatusMap>((acc, status) => {
        acc[status.channelId] = {
          id: status.id,
          lastReadAt: status.lastReadAt
        };
        return acc;
      }, {});
      
      set({ readStatuses: statusMap });
    } catch (error) {
      console.error('읽음 상태 조회 실패:', error);
    }
  },

  updateReadStatus: async (channelId) => {
    try {
      const { currentUser } = useAuthStore.getState();
      if (!currentUser) return;

      const existingStatus = get().readStatuses[channelId];
      let updatedStatus: ReadStatusDto;

      if (existingStatus) {
        // 이미 존재하는 ReadStatus 업데이트
        updatedStatus = await apiUpdateReadStatus(
          existingStatus.id, 
          new Date().toISOString()
        );
      } else {
        // 새로운 ReadStatus 생성
        updatedStatus = await createReadStatus(
          currentUser.id,
          channelId,
          new Date().toISOString()
        );
      }
      
      set((state) => ({
        readStatuses: {
          ...state.readStatuses,
          [channelId]: {
            id: updatedStatus.id,
            lastReadAt: updatedStatus.lastReadAt
          }
        }
      }));
    } catch (error) {
      console.error('읽음 상태 업데이트 실패:', error);
    }
  },

  hasUnreadMessages: (channelId, lastMessageAt) => {
    const status = get().readStatuses[channelId];
    const lastReadAt = status?.lastReadAt;
    // ReadStatus가 없거나 마지막 메시지가 마지막 읽은 시간보다 이후인 경우
    return !lastReadAt || new Date(lastMessageAt) > new Date(lastReadAt);
  }
}));

export default useReadStatusStore; 