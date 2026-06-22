import { getCsrfToken, login, logout, updateUserRole, refreshToken } from "@/api/auth";
import { Role, UserDto } from "@/types/api";
import { create } from "zustand";


interface AuthStore {
    currentUser: UserDto | null;
    accessToken: string | null;
    login: (username: string, password: string) => Promise<void>;
    logout: () => Promise<void>;
    fetchCsrfToken: () => Promise<void>;
    refreshToken: () => Promise<void>;
    clear: () => void;
    updateUserRole: (userId: string, role: Role) => Promise<void>;
    updateCurrentUser: (user: UserDto) => void;
}

const useAuthStore = create<AuthStore>((set, get) => ({
    currentUser: null,
    accessToken: null,
    login: async (username: string, password: string) => {
        const {userDto, accessToken} = await login(username, password);
        await get().fetchCsrfToken();
        set({ currentUser: userDto, accessToken });
    },
    logout: async () => {
        await logout();
        get().clear();
        get().fetchCsrfToken();
    },
    fetchCsrfToken: async () => {
        await getCsrfToken();
    },
    refreshToken: async () => {
        get().clear();
        const {userDto, accessToken} = await refreshToken();
        set({ currentUser: userDto, accessToken });
    },
    clear: () => {
        set({ currentUser: null, accessToken: null });
    },
    updateUserRole: async (userId: string, role: Role) => {
        await updateUserRole(userId, role);
    },
    updateCurrentUser: (user: UserDto) => {
        set({ currentUser: user });
    },
}));

export default useAuthStore;

