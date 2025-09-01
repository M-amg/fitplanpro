// app/(auth)/register.tsx
import { zodResolver } from "@hookform/resolvers/zod";
import { Link, router } from "expo-router";
import React from "react";
import { Controller, SubmitHandler, useForm } from "react-hook-form";
import { Alert, KeyboardAvoidingView, Platform, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { z } from "zod";
import { api } from "../../lib/api";
import { useAuth } from "../../lib/auth";
import { getLanguage } from "../../lib/lang";
import type { ApiResponse, LanguagePreference, LoginData } from "../../lib/types";

const phoneE164 = z.string().regex(/^\+[1-9]\d{1,14}$/, "Use E.164 format, e.g. +14155552671");
const schema = z.object({
  email: z.string().email(),
  phone: phoneE164,
  password: z.string().min(6),
  confirm: z.string().min(6),
  languagePreference: z.enum(["ENGLISH", "SPANISH", "FRENCH", "ARABIC"]),
}).refine((v) => v.password === v.confirm, { message: "Passwords do not match", path: ["confirm"] });

type FormData = z.infer<typeof schema>;

export default function RegisterScreen() {
  const { login } = useAuth();
  const [initialLang, setInitialLang] = React.useState<LanguagePreference>("ENGLISH");

  React.useEffect(() => {
    (async () => {
      const saved = await getLanguage();
      if (saved) setInitialLang(saved);
    })();
  }, []);

  const { control, handleSubmit, formState: { errors, isSubmitting }, setValue } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { email: "", phone: "", password: "", confirm: "", languagePreference: initialLang },
  });

  React.useEffect(() => { setValue("languagePreference", initialLang); }, [initialLang]);

  const onSubmit: SubmitHandler<FormData> = async (values) => {
    try {
      // payload required by your backend
      const payload = {
        email: values.email,
        phone: values.phone,
        password: values.password,
        languagePreference: values.languagePreference,
      };

      const res = await api.post<ApiResponse<LoginData>>("/auth/register", payload);
      const { token, user, hasProfile } = res.data.data;

      await login(token, user, hasProfile);

      if (hasProfile) router.replace("/dashboard");
      else router.replace("/personal-info"); // adjust to your first onboarding screen
    } catch (e: any) {
      const msg = e?.response?.data?.message ?? "Could not create account";
      Alert.alert("Register failed", String(msg));
    }
  };

  return (
    <KeyboardAvoidingView behavior={Platform.select({ ios: "padding", android: undefined })} style={s.c}>
      <Text style={s.title}>Create Account</Text>

      <Text style={s.label}>Email</Text>
      <Controller control={control} name="email" render={({ field: { value, onChange } }) => (
        <TextInput style={s.input} autoCapitalize="none" keyboardType="email-address" value={value} onChangeText={onChange} placeholder="you@example.com" />
      )} />
      {errors.email && <Text style={s.err}>{errors.email.message}</Text>}

      <Text style={s.label}>Phone</Text>
      <Controller control={control} name="phone" render={({ field: { value, onChange } }) => (
        <TextInput style={s.input} keyboardType="phone-pad" value={value} onChangeText={onChange} placeholder="+14155552671" />
      )} />
      {errors.phone && <Text style={s.err}>{errors.phone.message}</Text>}

      <Text style={s.label}>Password</Text>
      <Controller control={control} name="password" render={({ field: { value, onChange } }) => (
        <TextInput style={s.input} secureTextEntry value={value} onChangeText={onChange} placeholder="••••••••" />
      )} />
      {errors.password && <Text style={s.err}>{errors.password.message}</Text>}

      <Text style={s.label}>Confirm Password</Text>
      <Controller control={control} name="confirm" render={({ field: { value, onChange } }) => (
        <TextInput style={s.input} secureTextEntry value={value} onChangeText={onChange} placeholder="••••••••" />
      )} />
      {errors.confirm && <Text style={s.err}>{errors.confirm.message}</Text>}

      <Text style={s.label}>Language</Text>
      <Controller
        control={control}
        name="languagePreference"
        render={({ field: { value, onChange } }) => (
          <View style={s.row}>
            {(["ENGLISH", "SPANISH", "FRENCH", "ARABIC"] as LanguagePreference[]).map((lang) => {
              const sel = value === lang;
              const short =
                lang === "ENGLISH" ? "EN" :
                lang === "SPANISH" ? "ES" :
                lang === "FRENCH"  ? "FR" : "AR";
              return (
                <TouchableOpacity key={lang} style={[s.pill, sel && s.pillA]} onPress={() => onChange(lang)}>
                  <Text style={[s.pillT, sel && s.pillTA]}>{short}</Text>
                </TouchableOpacity>
              );
            })}
          </View>
        )}
      />
      {errors.languagePreference && <Text style={s.err}>{errors.languagePreference.message}</Text>}

      <TouchableOpacity style={[s.btn, isSubmitting && { opacity: 0.7 }]} onPress={handleSubmit(onSubmit)} disabled={isSubmitting}>
        <Text style={s.btnT}>{isSubmitting ? "Creating..." : "Sign Up"}</Text>
      </TouchableOpacity>

      <Text style={s.mini}>
        Already have an account? <Link href="/login" style={s.link}>Log in</Link>
      </Text>
    </KeyboardAvoidingView>
  );
}

const s = StyleSheet.create({
  c: { flex: 1, padding: 20, justifyContent: "center", gap: 8 },
  title: { fontSize: 24, fontWeight: "800", textAlign: "center", marginBottom: 12 },
  label: { fontWeight: "700", marginTop: 8 },
  input: { borderWidth: 1, borderColor: "#e5e7eb", borderRadius: 12, padding: 12, backgroundColor: "white" },
  row: { flexDirection: "row", gap: 8, marginTop: 6 },
  pill: { paddingVertical: 8, paddingHorizontal: 12, borderWidth: 1, borderColor: "#d1d5db", borderRadius: 9999 },
  pillA: { backgroundColor: "#111827", borderColor: "#111827" },
  pillT: { color: "#111827", fontWeight: "700" },
  pillTA: { color: "white" },
  btn: { marginTop: 12, backgroundColor: "#111827", borderRadius: 12, padding: 14, alignItems: "center" },
  btnT: { color: "white", fontWeight: "700" },
  err: { color: "#dc2626" },
  mini: { textAlign: "center", marginTop: 12, color: "#6b7280" },
  link: { color: "#2563eb", fontWeight: "700" },
});
