import { create } from 'zustand';
import { getUsers } from '../api/user';
import { UserDto } from '../types/api';


interface UserListStore {
  users: UserDto[];
  fetchUsers: () => Promise<void>;
  updateUserStatus: (userId: string) => void;
}

const useUserListStore = create<UserListStore>((set) => ({
  users: [],
  fetchUsers: async () => {
    try {
      const users = await getUsers();
      set({users});
    } catch (error) {
      console.error('사용자 목록 조회 실패:', error);
    }
  },
  updateUserStatus: (userId: string) => {
    set((state) => ({
      users: state.users.map((user) =>
          user.id === userId
              ? { ...user, status: 'OFFLINE' }
              : user
      ),
    }));
  },
}));

export default useUserListStore; 
