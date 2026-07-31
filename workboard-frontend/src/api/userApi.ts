import { apiClient } from "./client";
import type { UpdateUserRequest, UserSummaryDto } from "@/types/api";

export const userApi = {
  getMe: async (): Promise<UserSummaryDto> => {
    const { data } = await apiClient.get<UserSummaryDto>("/users/me");
    return data;
  },

  getUser: async (userId: number): Promise<UserSummaryDto> => {
    const { data } = await apiClient.get<UserSummaryDto>(`/users/${userId}`);
    return data;
  },

  updateProfile: async (payload: UpdateUserRequest): Promise<UserSummaryDto> => {
    const { data } = await apiClient.patch<UserSummaryDto>("/users/updateProfile", payload);
    return data;
  },

  searchUsers: async (query: string): Promise<UserSummaryDto[]> => {
    const { data } = await apiClient.get<UserSummaryDto[]>(
      `/users/search/${encodeURIComponent(query)}`,
    );
    return data;
  },
};
