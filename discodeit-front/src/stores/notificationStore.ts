import { getNotifications, readNotification } from "@/api/notification";
import { NotificationDto } from "@/types/api";
import { create } from "zustand";


interface NotificationStore {
  notifications: NotificationDto[];
  fetchNotifications: () => Promise<void>;
  readNotification: (id: string) => Promise<void>;
}

const useNotificationStore = create<NotificationStore>((set) => ({
  notifications: [],
  fetchNotifications: async () => {
    const notifications = await getNotifications();
    set({ notifications });
  },
  readNotification: async (id: string) => {
    await readNotification(id);
    set((state) => ({
      notifications: state.notifications.filter((notification) => notification.id !== id),
    }));
  },
}));

export default useNotificationStore;