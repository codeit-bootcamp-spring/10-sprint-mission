import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';
import useUserListStore from './userListStore';
import { updateUser } from '../api/user';
import {UserDto, UserUpdateRequest} from '../types/api';

interface UserStore {
  setCurrentUser: (user: UserDto) => void;
  currentUserId: string | null;
  logout: () => void;
  updateUser: (userId: string, request: UserUpdateRequest) => Promise<UserDto>;
}

const useUserStore = create<UserStore>()(
  persist(
    (set) => ({
      currentUserId: null,
      setCurrentUser: (user) => set({ currentUserId: user.id }),
      logout: () => {
        set({ currentUserId: null });
      },
      updateUser: async (userId, request) => {
        try {
          const userData = await updateUser(userId, request);
          await useUserListStore.getState().fetchUsers();
          return userData;
        } catch (error) {
          console.error('사용자 정보 수정 실패:', error);
          throw error;
        }
      },
    }),
    {
      name: 'user-storage',
      storage: createJSONStorage(() => sessionStorage),
    }
  )
);

export default useUserStore; 
