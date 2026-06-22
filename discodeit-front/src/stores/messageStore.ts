import { create } from 'zustand';
import { getMessages, createMessage as apiCreateMessage, updateMessage as apiUpdateMessage, deleteMessage as apiDeleteMessage } from '../api/message';
import useReadStatusStore from './readStatusStore';
import { MessageDto, MessageCreateRequest, Pageable } from '../types/api';
import useBinaryContentStore from './binaryContentStore';


interface CursorPagination {
  nextCursor: string | null;
  pageSize: number;
  hasNext: boolean;
}

interface MessageStore {
  messages: MessageDto[];
  newMessages: MessageDto[];
  lastMessageId: string | null;
  pagination: CursorPagination;
  fetchMessages: (channelId: string, cursor: string | null, pageable?: Pageable) => Promise<boolean>;
  loadMoreMessages: (channelId: string) => Promise<void>;
  createMessage: (messageData: MessageCreateRequest, attachments?: File[]) => Promise<MessageDto>;
  updateMessage: (messageId: string, newContent: string) => Promise<MessageDto>;
  deleteMessage: (messageId: string) => Promise<void>;
  isCreating: boolean;
  addNewMessage: (newMessage: MessageDto) => void;
  updateMessageLocally: (messageId: string, payload: any) => void;
  deleteMessageLocally: (messageId: string) => void;
  clear: () => void;
}

const defaultPageable: Pageable = {
  size: 50,
  sort: ["createdAt,desc"]
};

const useMessageStore = create<MessageStore>((set, get) => ({
  messages: [],
  newMessages: [],
  lastMessageId: null,  // 마지막 메시지 ID 저장
  pagination: {
    nextCursor: null,
    pageSize: 50,
    hasNext: false,
  },
  isCreating: false,

  fetchMessages: async (channelId, cursor, pageable = defaultPageable) => {
    try {
      if (get().isCreating) return Promise.resolve(true);
      const response = await getMessages(channelId, cursor, pageable);
      
      const messageList = response.content;
      const lastMessage = messageList.length > 0 ? messageList[0] : null;
      const hasNewMessages = lastMessage?.id !== get().lastMessageId;
      
      set((state) => {
        // ID 기반 중복 체크 추가
        const existingMessageIds = new Set(state.messages.map(msg => msg.id));
        const loadedMessages = messageList.filter(message => !existingMessageIds.has(message.id));
        const updatedMessages = [...state.messages, ...loadedMessages];
        const pagination = {
          nextCursor: response.nextCursor,
          pageSize: response.size,
          hasNext: response.hasNext
        };
          
        return {
          messages: updatedMessages,
          lastMessageId: lastMessage?.id || null,
          pagination
        };
      });

      return hasNewMessages;
    } catch (error) {
      console.error('메시지 목록 조회 실패:', error);
      return false;
    }
  },

  loadMoreMessages: async (channelId) => {
    const { pagination } = get();
    
    if (!pagination.hasNext) return;
    
    await get().fetchMessages(channelId, pagination.nextCursor, {
      ...defaultPageable
    });
  },

  addNewMessage: (newMessage: MessageDto) => {
    set((state) => ({
      newMessages: [...state.newMessages, newMessage],
    }));
  },

  createMessage: async (messageData) => {
    try {
      set({ isCreating: true }); // 메시지 생성 시작 상태 설정
      const newMessage = await apiCreateMessage(messageData);

      // 메시지 전송 성공 시 readStatus 업데이트
      const updateReadStatus = useReadStatusStore.getState().updateReadStatus;
      await updateReadStatus(messageData.channelId);

      set((state) => {
        // 중복 체크 추가
        const messageExists = state.messages.some(msg => msg.id === newMessage.id);
        if (messageExists) {
          return state; // 이미 존재하는 메시지면 상태 변경 없음
        }
        return {
          ...state,
          lastMessageId: newMessage.id // 마지막 메시지 ID 업데이트
        };
      });
      return newMessage;
    } catch (error) {
      console.error('메시지 생성 실패:', error);
      throw error;
    } finally {
      set({ isCreating: false }); // 메시지 생성 완료 상태 설정
    }
  },
  updateMessage: async (messageId, newContent) => {
    try {
      const updatedMessage = await apiUpdateMessage(messageId, { newContent });
      set((state) => ({
        messages: state.messages.map(msg =>
          msg.id === messageId ? { ...msg, content: newContent } : msg
        )
      }));
      return updatedMessage;
    } catch (error) {
      console.error('메시지 업데이트 실패:', error);
      throw error;
    }
  },
  updateMessageLocally: (messageId, payload) => {
    const updateMsg = (msg: any) => {
      if (msg.id !== messageId) return msg;
      let newAttachments = msg.attachments;
      if (payload.attachmentIds && payload.attachmentStatus) {
        if (payload.attachmentStatus === 'SUCCESS') {
          useBinaryContentStore.getState().fetchBinaryContents(payload.attachmentIds);
        }
        newAttachments = msg.attachments.map((att: any) => 
          payload.attachmentIds.includes(att.id) ? { ...att, status: payload.attachmentStatus } : att
        );
      }
      return {
        ...msg,
        content: payload.content !== null && payload.content !== undefined ? payload.content : msg.content,
        attachments: newAttachments
      };
    };
    set((state) => ({
      messages: state.messages.map(updateMsg),
      newMessages: state.newMessages.map(updateMsg)
    }));
  },
  deleteMessageLocally: (messageId) => {
    set((state) => ({
      messages: state.messages.filter(msg => msg.id !== messageId),
      newMessages: state.newMessages.filter(msg => msg.id !== messageId)
    }));
  },

  deleteMessage: async (messageId) => {
    try {
      await apiDeleteMessage(messageId); // 빈 내용으로 메시지 삭제
      set((state) => ({
        messages: state.messages.filter(msg => msg.id !== messageId)
      }));
    } catch (error) {
      console.error('메시지 삭제 실패:', error);
      throw error;
    }
  },
  clear: () => {
    set({ messages: [], newMessages: [],   pagination: {
        nextCursor: null,
        pageSize: 50,
        hasNext: false,
      } });
  }
}));

export default useMessageStore; 
