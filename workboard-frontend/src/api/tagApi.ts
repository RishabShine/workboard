import { apiClient } from "./client";
import type { CreateTagRequest, TagDto } from "@/types/api";

export const tagApi = {
  getTags: async (projectId: number): Promise<TagDto[]> => {
    const { data } = await apiClient.get<TagDto[]>(`/projects/${projectId}/tags`);
    return data;
  },

  createTag: async (projectId: number, payload: CreateTagRequest): Promise<TagDto> => {
    const { data } = await apiClient.post<TagDto>(
      `/projects/${projectId}/tags/create`,
      payload,
    );
    return data;
  },
};
