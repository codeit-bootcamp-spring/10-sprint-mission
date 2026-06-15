import {create} from 'zustand';
import {downloadBinaryContent, getBinaryContent} from '../api/binaryContent';
import {BinaryContentStatus} from "@/types/api.ts";

export interface BinaryContentInfo {
  url?: string;
  contentType: string;
  fileName: string;
  size: number;
  status: BinaryContentStatus;
  revokeUrl?: () => void;
}

interface BinaryContentStore {
  binaryContents: Record<string, BinaryContentInfo>;
  pollingIds: Set<string>;
  fetchBinaryContent: (id: string) => Promise<BinaryContentInfo | null>;
  startPolling: (id: string) => void;
  stopPolling: (id: string) => void;
  clearAllPolling: () => void;
  clearBinaryContent: (id: string) => void;
  clearBinaryContents: (ids: string[]) => void;
  clearAllBinaryContents: () => void;
}

let pollingIntervals: Record<string, NodeJS.Timeout> = {};

const useBinaryContentStore = create<BinaryContentStore>((set, get) => ({
  binaryContents: {},
  pollingIds: new Set<string>(),
  fetchBinaryContent: async (id) => {
    // 이미 가져온 정보가 있다면 재사용
    if (get().binaryContents[id]) {
      return get().binaryContents[id];
    }

    try {
      const binaryContent = await getBinaryContent(id);
      const { contentType, fileName, size, status  } = binaryContent;

      const binaryContentInfo: BinaryContentInfo = {
        contentType,
        fileName,
        size,
        status,
      };

      if (status === BinaryContentStatus.SUCCESS) {
        const downloadResult = await downloadBinaryContent(id);
        const imageObjectURL = URL.createObjectURL(downloadResult.blob);

        binaryContentInfo.url = imageObjectURL;
        binaryContentInfo.revokeUrl = () => URL.revokeObjectURL(imageObjectURL);
      }

      set((state) => ({
        binaryContents: {
          ...state.binaryContents,
          [id]: binaryContentInfo
        }
      }));

      return binaryContentInfo;
    } catch (error) {
      console.error('첨부파일 정보 조회 실패:', error);
      return null;
    }
  },
  startPolling: (id) => {
    // 이미 polling 중이면 중복 시작하지 않음
    if (pollingIntervals[id]) {
      return;
    }

    const pollingInterval = setInterval(async () => {
      try {
        const binaryContent = await getBinaryContent(id);
        const { status } = binaryContent;

        if (status === BinaryContentStatus.SUCCESS) {
          console.log(`Polling: ${id} 상태가 SUCCESS로 변경됨`);
          // 성공 상태가 되면 실제 파일 다운로드
          const downloadResult = await downloadBinaryContent(id);
          const imageObjectURL = URL.createObjectURL(downloadResult.blob);

          set((state) => ({
            binaryContents: {
              ...state.binaryContents,
              [id]: {
                ...state.binaryContents[id],
                url: imageObjectURL,
                status: BinaryContentStatus.SUCCESS,
                revokeUrl: () => URL.revokeObjectURL(imageObjectURL)
              }
            }
          }));

          // polling 중지
          get().stopPolling(id);
        } else if (status === BinaryContentStatus.FAIL) {
          console.log(`Polling: ${id} 상태가 FAIL로 변경됨`);
          // 실패 상태가 되면 상태만 업데이트하고 polling 중지
          set((state) => ({
            binaryContents: {
              ...state.binaryContents,
              [id]: {
                ...state.binaryContents[id],
                status: BinaryContentStatus.FAIL
              }
            }
          }));

          get().stopPolling(id);
        } else {
          console.log(`Polling: ${id} 상태가 여전히 PROCESSING임`);
        }
      } catch (error) {
        console.error('polling 중 오류:', error);
        get().stopPolling(id);
      }
    }, 2000); // 2초마다 체크

    pollingIntervals[id] = pollingInterval;
    set((state) => ({
      pollingIds: new Set([...state.pollingIds, id])
    }));
  },
  stopPolling: (id) => {
    if (pollingIntervals[id]) {
      clearInterval(pollingIntervals[id]);
      delete pollingIntervals[id];
    }

    set((state) => {
      const newPollingIds = new Set(state.pollingIds);
      newPollingIds.delete(id);
      return { pollingIds: newPollingIds };
    });
  },
  clearAllPolling: () => {
    Object.values(pollingIntervals).forEach(interval => {
      clearInterval(interval);
    });
    pollingIntervals = {};
    set({ pollingIds: new Set() });
  },
  clearBinaryContent: (id) => {
    const { binaryContents } = get();
    const content = binaryContents[id];
    if (content?.revokeUrl) {
      content.revokeUrl();
      set((state) => {
        const { [id]: removed, ...rest } = state.binaryContents;
        return { binaryContents: rest };
      });
    }
  },
  clearBinaryContents: (ids) => {
    const { binaryContents } = get();
    const idsToRevoke: string[] = [];
    
    // First pass: find existing IDs and revoke URLs
    ids.forEach(id => {
      const content = binaryContents[id];
      if (content) {
        if (content.revokeUrl) {
          content.revokeUrl();
        }
        idsToRevoke.push(id);
      }
    });
    
    // Only update state if some content was actually revoked
    if (idsToRevoke.length > 0) {
      set((state) => {
        const newBinaryContents = { ...state.binaryContents };
        idsToRevoke.forEach(id => {
          delete newBinaryContents[id];
        });
        return { binaryContents: newBinaryContents };
      });
    }
  },
  clearAllBinaryContents: () => {
    const { binaryContents } = get();
    Object.values(binaryContents).forEach(content => {
      if (content.revokeUrl) {
        content.revokeUrl();
      }
    });
    set({ binaryContents: {} });
  }
}));

export default useBinaryContentStore; 