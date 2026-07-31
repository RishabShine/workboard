import { apiClient } from "./client";
import type { CreateProjectInviteRequest, MemberDto, ProjectInviteDto } from "@/types/api";

export const membershipApi = {
  getMembers: async (projectId: number): Promise<MemberDto[]> => {
    const { data } = await apiClient.get<MemberDto[]>(`/membership/${projectId}`);
    return data;
  },

  createInvite: async (
    projectId: number,
    payload: CreateProjectInviteRequest,
  ): Promise<ProjectInviteDto> => {
    const { data } = await apiClient.post<ProjectInviteDto>(
      `/membership/${projectId}/createInvite`,
      payload,
    );
    return data;
  },

  getMyInvites: async (): Promise<ProjectInviteDto[]> => {
    const { data } = await apiClient.get<ProjectInviteDto[]>("/membership/invites");
    return data;
  },

  acceptInvite: async (inviteId: number): Promise<void> => {
    await apiClient.post(`/membership/${inviteId}/accept`);
  },

  rejectInvite: async (inviteId: number): Promise<void> => {
    await apiClient.post(`/membership/${inviteId}/reject`);
  },
};
