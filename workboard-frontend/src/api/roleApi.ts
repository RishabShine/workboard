import { apiClient } from "./client";
import type { CreateRoleRequest, RoleDto, UpdateRoleRequest } from "@/types/api";

export const roleApi = {
  getRoles: async (projectId: number): Promise<RoleDto[]> => {
    const { data } = await apiClient.get<RoleDto[]>(`/projects/${projectId}/roles`);
    return data;
  },

  createRole: async (projectId: number, payload: CreateRoleRequest): Promise<RoleDto> => {
    const { data } = await apiClient.post<RoleDto>(
      `/projects/${projectId}/roles/createRole`,
      payload,
    );
    return data;
  },

  createRoles: async (
    projectId: number,
    payload: CreateRoleRequest[],
  ): Promise<RoleDto[]> => {
    const { data } = await apiClient.post<RoleDto[]>(
      `/projects/${projectId}/roles/createRoles`,
      payload,
    );
    return data;
  },

  updateRole: async (projectId: number, payload: UpdateRoleRequest): Promise<RoleDto> => {
    const { data } = await apiClient.patch<RoleDto>(
      `/projects/${projectId}/roles/update`,
      payload,
    );
    return data;
  },
};
