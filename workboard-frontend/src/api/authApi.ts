import { apiClient } from "./client";
import type { LoginRequest, LoginResponse, RegisterRequest } from "@/types/api";

export const authApi = {
  login: async (payload: LoginRequest): Promise<LoginResponse> => {
    const { data } = await apiClient.post<LoginResponse>("/auth/login", payload);
    return data;
  },

  // Backend returns a plain string on register (not a DTO), so we surface it
  // as-is; the caller decides whether to redirect to /login or elsewhere.
  register: async (payload: RegisterRequest): Promise<string> => {
    const { data } = await apiClient.post<string>("/auth/register", payload);
    return data;
  },
};
