import { apiClient } from "./client";
import type { CreateMilestoneRequest, MilestoneDto, UpdateMilestoneRequest } from "@/types/api";

export const milestoneApi = {
  getMilestonesByProject: async (projectId: number): Promise<MilestoneDto[]> => {
    const { data } = await apiClient.get<MilestoneDto[]>(`/milestones/${projectId}`);
    return data;
  },

  createMilestone: async (payload: CreateMilestoneRequest): Promise<MilestoneDto> => {
    const { data } = await apiClient.post<MilestoneDto>("/milestones", payload);
    return data;
  },

  updateMilestone: async (payload: UpdateMilestoneRequest): Promise<MilestoneDto> => {
    const { data } = await apiClient.patch<MilestoneDto>("/milestones/update", payload);
    return data;
  },
};
