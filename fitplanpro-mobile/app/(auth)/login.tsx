import { api } from "@/lib/api";
import { useAuth } from "@/lib/auth";
import { ApiResponse, LoginData } from "@/lib/types";
import { zodResolver } from "@hookform/resolvers/zod";
import { Link, router } from "expo-router";
import React from "react";
import { Controller, SubmitHandler, useForm } from "react-hook-form";
import {
  Alert,
  KeyboardAvoidingView,
  Platform,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
} from "react-native";
import { z } from "zod";

const schema = z.object({
  emailOrPhone: z.string().email(),
  password: z.string().min(6),
});
type FormData = z.infer<typeof schema>;

export default function LoginScreen() {
  const { login } = useAuth();
  const {
    control,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { emailOrPhone: "", password: "" },
  });

  const onSubmit: SubmitHandler<FormData> = async (values) => {
    try {
      const res = await api.post<ApiResponse<LoginData>>("/auth/login", values);
      const { token, user, hasProfile } = res.data.data; // <- matches your backend
      await login(token, user, hasProfile);
      // If user has no profile yet, send to onboarding; else dashboard
      if (hasProfile) router.replace("/dashboard");
      else router.replace("/personal-info"); // or your first onboarding screen
    } catch (e: any) {
      Alert.alert(
        "Login failed",
        e?.response?.data?.message ?? "Invalid credentials"
      );
    }
  };

  return (
    <KeyboardAvoidingView
      behavior={Platform.select({ ios: "padding", android: undefined })}
      style={s.c}
    >
      <Text style={s.title}>Welcome back</Text>

      <Text style={s.label}>Email or Phone</Text>
      <Controller
        control={control}
        name="emailOrPhone"
        render={({ field: { value, onChange } }) => (
          <TextInput
            style={s.input}
            autoCapitalize="none"
            keyboardType="email-address"
            value={value}
            onChangeText={onChange}
            placeholder="you@example.com"
          />
        )}
      />
      {errors.emailOrPhone && (
        <Text style={s.err}>{errors.emailOrPhone.message}</Text>
      )}

      <Text style={s.label}>Password</Text>
      <Controller
        control={control}
        name="password"
        render={({ field: { value, onChange } }) => (
          <TextInput
            style={s.input}
            secureTextEntry
            value={value}
            onChangeText={onChange}
            placeholder="••••••••"
          />
        )}
      />
      {errors.password && <Text style={s.err}>{errors.password.message}</Text>}

      <TouchableOpacity
        style={[s.btn, isSubmitting && { opacity: 0.7 }]}
        onPress={handleSubmit(onSubmit)}
        disabled={isSubmitting}
      >
        <Text style={s.btnT}>{isSubmitting ? "Signing in..." : "Sign In"}</Text>
      </TouchableOpacity>

      <Text style={s.mini}>
        No account?{" "}
        <Link href="/(auth)/register" style={s.link}>
          Create one
        </Link>
      </Text>
    </KeyboardAvoidingView>
  );
}

const s = StyleSheet.create({
  c: { flex: 1, padding: 20, justifyContent: "center", gap: 8 },
  title: {
    fontSize: 28,
    fontWeight: "800",
    textAlign: "center",
    marginBottom: 16,
  },
  label: { fontWeight: "700", marginTop: 8 },
  input: {
    borderWidth: 1,
    borderColor: "#e5e7eb",
    borderRadius: 12,
    padding: 12,
    backgroundColor: "white",
  },
  btn: {
    marginTop: 12,
    backgroundColor: "#111827",
    borderRadius: 12,
    padding: 14,
    alignItems: "center",
  },
  btnT: { color: "white", fontWeight: "700" },
  err: { color: "#dc2626" },
  mini: { textAlign: "center", marginTop: 12, color: "#6b7280" },
  link: { color: "#2563eb", fontWeight: "700" },
});
