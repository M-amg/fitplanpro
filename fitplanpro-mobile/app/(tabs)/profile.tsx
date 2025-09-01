// app/(tabs)/profile.tsx
import { router } from "expo-router";
import React from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { useAuth } from "../../lib/auth";

export default function Profile() {
  const { user, logout } = useAuth();

  const onLogout = async () => {
    await logout();
    router.replace("/(auth)/login");
  };

  return (
    <View style={s.c}>
      <Text style={s.t}>Profile</Text>
      <View style={s.card}>
        <Text style={s.k}>Name</Text>
        <Text style={s.v}>{user?.name ?? "-"}</Text>
      </View>
      <View style={s.card}>
        <Text style={s.k}>Email</Text>
        <Text style={s.v}>{user?.email ?? "-"}</Text>
      </View>

      <TouchableOpacity style={s.logout} onPress={onLogout}>
        <Text style={s.logoutT}>Log Out</Text>
      </TouchableOpacity>
    </View>
  );
}

const s = StyleSheet.create({
  c: { flex: 1, padding: 20 },
  t: { fontSize: 22, fontWeight: "800", textAlign: "center", marginBottom: 12 },
  card: { backgroundColor: "white", padding: 16, borderRadius: 12, borderWidth: 1, borderColor: "#eee", marginBottom: 10 },
  k: { color: "#6b7280", marginBottom: 4 },
  v: { fontWeight: "700" },
  logout: { marginTop: 16, backgroundColor: "#111827", padding: 14, borderRadius: 12, alignItems: "center" },
  logoutT: { color: "white", fontWeight: "700" },
});
