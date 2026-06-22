import { create } from 'zustand';
import { getUsers } from '../api/user';
import { UserDto } from '../types/api';


interface UserListStore {
  users: UserDto[];
  fetchUsers: () => Promise<void>;
  replaceUser: (user: UserDto) => void;
  removeUser: (userId: string) => void;
}

const useUserListStore = create<UserListStore>((set, get) => ({
  users: [],
  fetchUsers: async () => {
    try {
      const users = await getUsers();
      set({users});
    } catch (error) {
      console.error('사용자 목록 조회 실패:', error);
    }
  },
  replaceUser: user => {
    const { users } = get();
    if (users.some(u => u.id === user.id)) {
      set(state => ({
        users: state.users.map(u => (u.id === user.id ? user : u))
      }));
    } else {
      set(state => ({
        users: [user, ...state.users]
      }));
    }
  },
  removeUser: userId => {
    set(state => ({
      users: state.users.filter(user => user.id !== userId)
    }))
  }
}));

export default useUserListStore; 