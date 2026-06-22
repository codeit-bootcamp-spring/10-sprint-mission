import client from "@/api/client.ts";
import {BinaryContentDto} from "@/types/api.ts";

export const uploadFiles = async (files: Array<File>): Promise<Array<BinaryContentDto>> => {
  const formData = new FormData();
  files.forEach((file) => {
    formData.append('files', file);
  });

  const response = await client.post<Array<BinaryContentDto>>('/binaryContents', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
  return response.data;
};

export const uploadProfile = async (file: File): Promise<BinaryContentDto> => {
  const profileDto = await uploadFiles([file]);
  return profileDto[0];
};
