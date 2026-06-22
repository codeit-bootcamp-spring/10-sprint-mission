import { create } from 'zustand';
import { createPrivateChannel, createPublicChannel, getChannels, updateChannel, deleteChannel } from '../api/channel';
import { ChannelDto, PublicChannelCreateRequest, PublicChannelUpdateRequest } from '../types/api';
import useReadStatusStore from './readStatusStore';

interface ChannelStore {
  channels: ChannelDto[];
  loading: boolean;
  error: any;
  fetchChannels: (userId: string) => Promise<ChannelDto[]>;
  createPublicChannel: (channelData: PublicChannelCreateRequest) => Promise<ChannelDto>;
  createPrivateChannel: (participantIds: string[]) => Promise<ChannelDto>;
  updatePublicChannel: (channelId: string, updateData: PublicChannelUpdateRequest) => Promise<ChannelDto>;
  deleteChannel: (channelId: string) => Promise<void>;
  replaceChannel: (channel: ChannelDto) => void;
  removeChannel: (channelId: string) => void;
}

const useChannelStore = create<ChannelStore>((set, get) => ({
  channels: [],
  loading: false,
  error: null,

  fetchChannels: async (userId: string) => {
    set({ loading: true, error: null });
    try {
      // 채널 목록 가져오기
      const channels = await getChannels(userId);
      
      // 중복 채널 방지를 위해 ID 기반 필터링 추가
      set((state) => {
        // 기존 채널 ID 맵 생성
        const existingChannelIds = new Set(state.channels.map(channel => channel.id));
        
        // 새로운 채널만 추가
        const uniqueNewChannels = channels.filter(channel => !existingChannelIds.has(channel.id));
        const existingChannels = state.channels.filter(channel => 
          channels.some(newChannel => newChannel.id === channel.id)
        );
        
        // 기존 채널과 새 채널 병합 (중복 제거)
        const mergedChannels = [...existingChannels, ...uniqueNewChannels];
        
        return { 
          channels: mergedChannels, 
          loading: false 
        };
      });
      
      // 읽음 상태 가져오기
      const { fetchReadStatuses } = useReadStatusStore.getState();
      fetchReadStatuses();
      return channels;
    } catch (error) {
      set({ error, loading: false });
      return [];
    }
  },

  createPublicChannel: async (channelData) => {
    try {
      const newChannel = await createPublicChannel(channelData);
      
      set((state) => {
        // 중복 채널 체크
        const channelExists = state.channels.some(channel => channel.id === newChannel.id);
        if (channelExists) {
          return state; // 이미 존재하는 채널이면 상태 변경 없음
        }
        
        return {
          channels: [...state.channels, {
            ...newChannel,
            participantIds: [],  // 공개 채널은 빈 배열로 초기화
            lastMessageAt: new Date().toISOString()   // 생성 시간을 lastMessageAt으로 사용
          } as ChannelDto]
        };
      });
      return newChannel;
    } catch (error) {
      console.error('공개 채널 생성 실패:', error);
      throw error;
    }
  },
  
  createPrivateChannel: async (participantIds) => {
    try {
      const newChannel = await createPrivateChannel(participantIds);
      
      set((state) => {
        // 중복 채널 체크
        const channelExists = state.channels.some(channel => channel.id === newChannel.id);
        if (channelExists) {
          return state; // 이미 존재하는 채널이면 상태 변경 없음
        }
        
        return {
          channels: [...state.channels, {
            ...newChannel,
            participantIds,  // 생성 시 전달받은 participantIds 사용
            lastMessageAt: new Date().toISOString()  // 생성 시간을 lastMessageAt으로 사용
          } as ChannelDto]
        };
      });
      return newChannel;
    } catch (error) {
      console.error('비공개 채널 생성 실패:', error);
      throw error;
    }
  },

  updatePublicChannel: async (channelId, updateData) => {
    try {
      const updatedChannel = await updateChannel(channelId, updateData);
      
      set((state) => ({
        channels: state.channels.map(channel => 
          channel.id === channelId ? { ...channel, ...updatedChannel } : channel
        )
      }));
      
      return updatedChannel;
    } catch (error) {
      console.error('채널 수정 실패:', error);
      throw error;
    }
  },

  deleteChannel: async (channelId) => {
    try {
      await deleteChannel(channelId);
      
      get().removeChannel(channelId);
    } catch (error) {
      console.error('채널 삭제 실패:', error);
      throw error;
    }
  },
  replaceChannel: channel => {
    const { channels } = get();
    if (channels.some(c => c.id === channel.id)) {
      set((state) => ({
        channels: state.channels.map(_channel =>
            _channel.id === channel.id ? channel : _channel
        )
      }));
    } else {
      set((state) => ({
        channels: [channel, ...state.channels, ]
      }));
    }
  } ,
  removeChannel: channelId => {
    set((state) => ({
      channels: state.channels.filter(channel => channel.id !== channelId)
    }));
  }
}));

export default useChannelStore; 
