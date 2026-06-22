import {create} from 'zustand';
import {downloadBinaryContent, getBinaryContent, getBinaryContents} from '../api/binaryContent';
import {BinaryContentDto, BinaryContentStatus} from "@/types/api.ts";

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
  fetchBinaryContent: (id: string) => Promise<BinaryContentInfo | null>;
  fetchBinaryContents: (ids: string[]) => Promise<void>;
  updateBinaryContentStatus: (updated: BinaryContentDto) => Promise<void>;
  clearBinaryContent: (id: string) => void;
  clearBinaryContents: (ids: string[]) => void;
  clearAllBinaryContents: () => void;
}

const useBinaryContentStore = create<BinaryContentStore>((set, get) => ({
  binaryContents: {},
  fetchBinaryContent: async (id) => {
    // 이미 가져온 정보가 성공 상태라면 재사용
    const existing = get().binaryContents[id];
    if (existing && existing.status === BinaryContentStatus.SUCCESS) {
      return existing;
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
  fetchBinaryContents: async (ids) => {
    const missingIds = ids.filter(id => {
      const existing = get().binaryContents[id];
      return !existing || existing.status !== BinaryContentStatus.SUCCESS;
    });
    if (missingIds.length === 0) return;

    try {
      const contents = await getBinaryContents(missingIds);
      const newInfos: Record<string, BinaryContentInfo> = {};

      await Promise.all(contents.map(async (content) => {
        const info: BinaryContentInfo = {
          contentType: content.contentType,
          fileName: content.fileName,
          size: content.size,
          status: content.status,
        };

        if (content.status === BinaryContentStatus.SUCCESS) {
          const downloadResult = await downloadBinaryContent(content.id);
          const imageObjectURL = URL.createObjectURL(downloadResult.blob);
          info.url = imageObjectURL;
          info.revokeUrl = () => URL.revokeObjectURL(imageObjectURL);
        }

        newInfos[content.id] = info;
      }));

      set((state) => ({
        binaryContents: {
          ...state.binaryContents,
          ...newInfos
        }
      }));
    } catch (error) {
      console.error('다중 첨부파일 정보 조회 실패:', error);
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
  },
  updateBinaryContentStatus: async (updated: BinaryContentDto) => {
    if (updated.status === BinaryContentStatus.SUCCESS) {
      console.log(`${updated.id} 상태가 SUCCESS로 변경됨`);
      // 성공 상태가 되면 실제 파일 다운로드
      const downloadResult = await downloadBinaryContent(updated.id);
      const imageObjectURL = URL.createObjectURL(downloadResult.blob);

      set((state) => {

        return ({
          binaryContents: {
            ...state.binaryContents,
            [updated.id]: {
              ...updated,
              url: imageObjectURL,
              status: BinaryContentStatus.SUCCESS,
              revokeUrl: () => URL.revokeObjectURL(imageObjectURL)
            }
          }
        })
      } );

    } else if (updated.status === BinaryContentStatus.FAILED) {
      console.log(`${updated.id} 상태가 FAIL로 변경됨`);
      set((state) => ({
        binaryContents: {
          ...state.binaryContents,
          [updated.id]: {
            ...updated,
            status: BinaryContentStatus.FAILED
          }
        }
      }));

    } else {
      console.log(`${updated.id} 상태가 여전히 PROCESSING임`);
    }
  }
}));

export default useBinaryContentStore; 
