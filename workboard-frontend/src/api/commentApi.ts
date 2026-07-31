import { apiClient } from "./client";
import type { CommentDto, CreateCommentRequest } from "@/types/api";

export const commentApi = {
  getComments: async (ticketId: number): Promise<CommentDto[]> => {
    const { data } = await apiClient.get<CommentDto[]>(`/tickets/${ticketId}/comments`);
    return data;
  },

  addComment: async (ticketId: number, payload: CreateCommentRequest): Promise<CommentDto> => {
    const { data } = await apiClient.post<CommentDto>(
      `/tickets/${ticketId}/addComment`,
      payload,
    );
    return data;
  },
};
