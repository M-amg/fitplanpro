import AsyncStorage from "@react-native-async-storage/async-storage";
import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { api } from "./api";
import type { User } from "./types";

type AuthCtx = {
  user: User | null;
  token: string | null;
  hasProfile: boolean;
  loading: boolean;
  login: (token: string, user: User, hasProfile: boolean) => Promise<void>;
  logout: () => Promise<void>;
  setUser: (u: User | null) => void;
  setHasProfile: (v: boolean) => void;
};

const AuthContext = createContext<AuthCtx | undefined>(undefined);

const TOKEN_KEY = "fitplanpro.auth.token";
const USER_KEY = "fitplanpro.auth.user";
const HAS_PROFILE_KEY = "fitplanpro.auth.hasProfile";

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [token, setToken] = useState<string | null>(null);
  const [user, setUser] = useState<User | null>(null);
  const [hasProfile, setHasProfile] = useState<boolean>(false);
  const [loading, setLoading] = useState(true);

  // Hydrate from storage (no /auth/me needed)
  useEffect(() => {
    (async () => {
      try {
        const [t, u, hp] = await Promise.all([
          AsyncStorage.getItem(TOKEN_KEY),
          AsyncStorage.getItem(USER_KEY),
          AsyncStorage.getItem(HAS_PROFILE_KEY),
        ]);
        if (t) {
          setToken(t);
          api.defaults.headers.common.Authorization = `Bearer ${t}`;
        }
        if (u) setUser(JSON.parse(u));
        if (hp) setHasProfile(hp === "true");
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  const login = async (t: string, u: User, hp: boolean) => {
    setToken(t);
    api.defaults.headers.common.Authorization = `Bearer ${t}`;
    setUser(u);
    setHasProfile(hp);

    await Promise.all([
      AsyncStorage.setItem(TOKEN_KEY, t),
      AsyncStorage.setItem(USER_KEY, JSON.stringify(u)),
      AsyncStorage.setItem(HAS_PROFILE_KEY, String(hp)),
    ]);
  };

  const logout = async () => {
    setUser(null);
    setToken(null);
    setHasProfile(false);
    delete api.defaults.headers.common.Authorization;
    await Promise.all([
      AsyncStorage.removeItem(TOKEN_KEY),
      AsyncStorage.removeItem(USER_KEY),
      AsyncStorage.removeItem(HAS_PROFILE_KEY),
    ]);
  };

  const value = useMemo(
    () => ({ user, token, hasProfile, loading, login, logout, setUser, setHasProfile }),
    [user, token, hasProfile, loading]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}
