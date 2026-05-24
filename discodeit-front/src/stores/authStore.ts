import { getCsrfToken, login, logout, me, updateUserRole } from "@/api/auth";
import { Role, UserDto } from "@/types/api";
import { create } from "zustand";


interface AuthStore {
    currentUser: UserDto | null;
    login: (username: string, password: string, rememberMe: boolean) => Promise<void>;
    logout: () => Promise<void>;
    fetchCsrfToken: () => Promise<void>;
    fetchMe: () => Promise<void>;
    clear: () => void;
    updateUserRole: (userId: string, role: Role) => Promise<void>;
}

const useAuthStore = create<AuthStore>((set, get) => ({
    currentUser: null,
    login: async (username: string, password: string, rememberMe: boolean = false) => {
        const response = await login(username, password, rememberMe);
        await get().fetchCsrfToken();
        set({ currentUser: response });
    },
    logout: async () => {
        await logout();
        get().clear();
        get().fetchCsrfToken();
    },
    fetchCsrfToken: async () => {
        await getCsrfToken();
    },
    fetchMe: async () => {
        const response = await me();
        set({ currentUser: response });
    },
    clear: () => {
        set({ currentUser: null });
    },
    updateUserRole: async (userId: string, role: Role) => {
        await updateUserRole(userId, role);
    },
}));

export default useAuthStore;

