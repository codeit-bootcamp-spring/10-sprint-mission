import { create } from 'zustand';
import {downloadBinaryContent, getBinaryContent} from '../api/binaryContent';

export interface BinaryContentInfo {
  url: string;
  contentType: string;
  fileName: string;
  size: number;
  revokeUrl?: () => void;
}

interface BinaryContentStore {
  binaryContents: Record<string, BinaryContentInfo>;
  fetchBinaryContent: (id: string) => Promise<BinaryContentInfo | null>;
  clearBinaryContent: (id: string) => void;
  clearBinaryContents: (ids: string[]) => void;
  clearAllBinaryContents: () => void;
}

const useBinaryContentStore = create<BinaryContentStore>((set, get) => ({
  binaryContents: {},
  fetchBinaryContent: async (id) => {
    // 이미 가져온 정보가 있다면 재사용
    if (get().binaryContents[id]) {
      return get().binaryContents[id];
    }

    try {
      const binaryContent = await getBinaryContent(id);
      const { contentType, fileName, size  } = binaryContent;
      const downloadResult = await downloadBinaryContent(id);
      const imageObjectURL = URL.createObjectURL(downloadResult.blob);

      const binaryContentInfo: BinaryContentInfo = {
        url: imageObjectURL,
        contentType,
        fileName,
        size,
        revokeUrl: () => URL.revokeObjectURL(imageObjectURL)
      };

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