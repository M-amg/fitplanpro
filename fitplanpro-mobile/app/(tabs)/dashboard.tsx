// app/(tabs)/dashboard.tsx
import { router } from "expo-router";
import React from "react";
import { ActivityIndicator, RefreshControl, ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { api } from "../../lib/api";
import type { Plan } from "../../lib/types";

export default function Dashboard() {
  const [loading, setLoading] = React.useState(true);
  const [refreshing, setRefreshing] = React.useState(false);
  const [plan, setPlan] = React.useState<Plan | null>(null);
  const [error, setError] = React.useState<string | null>(null);

  const load = async () => {
    try {
      setError(null);
      const { data } = await api.get<Plan>("/plans/current");
      setPlan(data);
    } catch (e: any) {
      setError(e?.response?.data?.message ?? "Failed to load");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  React.useEffect(() => { load(); }, []);
  const onRefresh = () => { setRefreshing(true); load(); };

  if (loading) return <View style={s.center}><ActivityIndicator /></View>;
  if (error) return (
    <View style={s.center}>
      <Text style={s.err}>{error}</Text>
      <TouchableOpacity style={s.btn} onPress={load}><Text style={s.btnT}>Retry</Text></TouchableOpacity>
    </View>
  );

  const today = new Date();
  const dayIdx = Math.max(1, Math.min(7, today.getDay() || 7)); // 1..7
  const day = plan?.week.days.find(d => d.day === dayIdx);

  return (
    <ScrollView style={{ flex: 1 }} refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}>
      <View style={s.card}>
        <Text style={s.h}>Hello 👋</Text>
        <Text style={s.p}>Here’s your plan snapshot.</Text>
      </View>

      <View style={s.card}>
        <Text style={s.h}>Today (Day {dayIdx})</Text>
        <Text style={s.p}>Calories target: {plan?.macros.calories ?? "-"} kcal</Text>
        <View style={{ flexDirection: "row", gap: 8, marginTop: 10 }}>
          <TouchableOpacity style={s.smallBtn} onPress={() => router.push({ pathname: "/plan/meal/[day]", params: { day: String(dayIdx) } })}>
            <Text style={s.smallBtnT}>View Meals</Text>
          </TouchableOpacity>
          <TouchableOpacity style={s.smallBtn} onPress={() => router.push({ pathname: "/track/workout/[day]", params: { day: String(dayIdx) } })}>
            <Text style={s.smallBtnT}>Log Workout</Text>
          </TouchableOpacity>
          <TouchableOpacity style={s.smallBtn} onPress={() => router.push({ pathname: "/track/meal/[day]", params: { day: String(dayIdx) } })}>
            <Text style={s.smallBtnT}>Log Meal</Text>
          </TouchableOpacity>
        </View>
      </View>

      <View style={s.card}>
        <Text style={s.h}>Next Workout</Text>
        {day?.workout ? (
          <>
            <Text style={s.p}>{day.workout.title}</Text>
            <Text style={s.sub}>Exercises: {day.workout.exercises?.length ?? 0}</Text>
          </>
        ) : (
          <Text style={s.p}>Rest day 🛌</Text>
        )}
      </View>
    </ScrollView>
  );
}

const s = StyleSheet.create({
  center: { flex: 1, alignItems: "center", justifyContent: "center", padding: 20 },
  err: { color: "#dc2626", marginBottom: 10 },
  btn: { backgroundColor: "#111827", paddingVertical: 10, paddingHorizontal: 14, borderRadius: 10 },
  btnT: { color: "white", fontWeight: "700" },

  card: { backgroundColor: "white", margin: 12, padding: 16, borderRadius: 12, borderWidth: 1, borderColor: "#eee", gap: 6 },
  h: { fontSize: 18, fontWeight: "700" },
  p: { color: "#111827" },
  sub: { color: "#6b7280" },

  smallBtn: { backgroundColor: "#111827", paddingVertical: 8, paddingHorizontal: 12, borderRadius: 10 },
  smallBtnT: { color: "white", fontWeight: "600" },
});
