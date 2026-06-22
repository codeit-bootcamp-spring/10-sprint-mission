import { getNotifications, readNotification } from "@/api/notification";
import { NotificationDto } from "@/types/api";
import { create } from "zustand";


interface NotificationStore {
  notifications: NotificationDto[];
  newNotifications: NotificationDto[];
  fetchNotifications: () => Promise<void>;
  readNotification: (id: string) => Promise<void>;
  addNewNotification: (notification: NotificationDto) => void;
  clear: () => void;
}

const useNotificationStore = create<NotificationStore>((set) => ({
  notifications: [],
  newNotifications: [],
  fetchNotifications: async () => {
    const notifications = await getNotifications();
    set({ notifications });
  },
  readNotification: async (id: string) => {
    await readNotification(id);
    set((state) => ({
      notifications: state.notifications.filter((notification) => notification.id !== id),
      newNotifications: state.newNotifications.filter((notification) => notification.id !== id),
    }));
  },
  addNewNotification: (notification: NotificationDto) => {
    set((state) => ({
      newNotifications: [notification,...state.newNotifications],
    }));
  },
  clear: () => {
    set({ newNotifications: [], notifications: [] });
  },
}));

export default useNotificationStore;