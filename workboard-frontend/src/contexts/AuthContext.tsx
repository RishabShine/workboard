import { createContext, useCallback, useEffect, useState, type ReactNode } from "react";
import { useNavigate } from "react-router-dom";
import { onUnauthorized, TOKEN_STORAGE_KEY } from "@/api/client";
import { authApi } from "@/api/authApi";
import { userApi } from "@/api/userApi";
import type { LoginRequest, RegisterRequest, UserSummaryDto } from "@/types/api";

interface AuthContextValue {
  user: UserSummaryDto | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (payload: LoginRequest) => Promise<void>;
  register: (payload: RegisterRequest) => Promise<string>;
  logout: () => void;
  refreshUser: () => Promise<void>;
}

export const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const navigate = useNavigate();
  const [user, setUser] = useState<UserSummaryDto | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  const logout = useCallback(() => {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    setUser(null);
    navigate("/login");
  }, [navigate]);

  const refreshUser = useCallback(async () => {
    const token = localStorage.getItem(TOKEN_STORAGE_KEY);
    if (!token) {
      setUser(null);
      setIsLoading(false);
      return;
    }
    try {
      const me = await userApi.getMe();
      setUser(me);
    } catch {
      localStorage.removeItem(TOKEN_STORAGE_KEY);
      setUser(null);
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    onUnauthorized(() => {
      setUser(null);
      navigate("/login");
    });
  }, [navigate]);

  useEffect(() => {
    refreshUser();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const login = useCallback(
    async (payload: LoginRequest) => {
      const { token } = await authApi.login(payload);
      localStorage.setItem(TOKEN_STORAGE_KEY, token);
      await refreshUser();
      navigate("/projects");
    },
    [navigate, refreshUser],
  );

  const register = useCallback(async (payload: RegisterRequest) => {
    const result = await authApi.register(payload);
    return result;
  }, []);

  return (
    <AuthContext.Provider
      value={{ user, isAuthenticated: !!user, isLoading, login, register, logout, refreshUser }}
    >
      {children}
    </AuthContext.Provider>
  );
}
