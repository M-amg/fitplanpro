import axios from "axios";
import * as Haptics from "expo-haptics";
import { router } from "expo-router";

export const api = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 8000,
});

// Utility for manual token injection
export function setAuthToken(token?: string) {
  if (token) {
    api.defaults.headers.common.Authorization = `Bearer ${token}`;
  } else {
    delete api.defaults.headers.common.Authorization;
  }
}

// Interceptor: catch 401 globally
api.interceptors.response.use(
  (res) => res,
  async (err) => {
    const status = err?.response?.status;
    if (status === 401) {
      // Small vibration feedback
      try {
        await Haptics.notificationAsync(Haptics.NotificationFeedbackType.Error);
      } catch {}

      // Clear token header
      delete api.defaults.headers.common.Authorization;

      // Kick user to login
      router.replace("/(auth)/login");
    }
    return Promise.reject(err);
  }
);
