import { apiClient } from "./client";
import type {
  CreateProjectRequest,
  ProjectDetailDto,
  ProjectListDto,
  ProjectOverviewDto,
  UpdateProjectRequest,
} from "@/types/api";

export const projectApi = {
  listMyProjects: async (): Promise<ProjectListDto> => {
    const { data } = await apiClient.get<ProjectListDto>("/projects");
    return data;
  },

  getProject: async (projectId: number): Promise<ProjectDetailDto> => {
    const { data } = await apiClient.get<ProjectDetailDto>(`/projects/${projectId}`);
    return data;
  },

  createProject: async (payload: CreateProjectRequest): Promise<ProjectOverviewDto> => {
    const { data } = await apiClient.post<ProjectOverviewDto>("/projects", payload);
    return data;
  },

  // NOTE: no PATCH /projects/{projectId} endpoint was found in the backend
  // controller yet. Wired up to the conventional path so it "just works"
  // once the backend adds it; UI can be hidden until then.
  updateProject: async (
    projectId: number,
    payload: UpdateProjectRequest,
  ): Promise<ProjectDetailDto> => {
    const { data } = await apiClient.patch<ProjectDetailDto>(`/projects/${projectId}`, payload);
    return data;
  },
};
