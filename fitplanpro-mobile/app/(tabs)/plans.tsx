import { router } from "expo-router";
import React from "react";
import { ActivityIndicator, RefreshControl, ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { api } from "../../lib/api";
import type { Plan } from "../../lib/types";

export default function PlansHub() {
  const [loading, setLoading] = React.useState(true);
  const [refreshing, setRefreshing] = React.useState(false);
  const [error, setError] = React.useState<string | null>(null);
  const [plan, setPlan] = React.useState<Plan | null>(null);

  const load = async () => {
    try {
      setError(null);
      const { data } = await api.get<Plan>("/plans/current");
      setPlan(data);
    } catch (e: any) {
      setError(e?.response?.data?.message ?? "Failed to load plan");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  React.useEffect(() => {
    load();
  }, []);

  const onRefresh = () => {
    setRefreshing(true);
    load();
  };

  if (loading) {
    return (
      <View style={s.center}><ActivityIndicator /></View>
    );
  }

  if (error) {
    return (
      <View style={s.center}>
        <Text style={s.err}>{error}</Text>
        <TouchableOpacity style={s.btn} onPress={load}>
          <Text style={s.btnT}>Retry</Text>
        </TouchableOpacity>
      </View>
    );
  }

  if (!plan) {
    return (
      <View style={s.center}>
        <Text>No active plan yet.</Text>
        <TouchableOpacity style={s.btn} onPress={async () => {
          await api.post("/plans:generate", { useCache: true, force: false });
          load();
        }}>
          <Text style={s.btnT}>Generate Plan</Text>
        </TouchableOpacity>
      </View>
    );
  }

  return (
    <ScrollView style={{ flex: 1 }} refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}>
      {/* Macros */}
      <View style={s.card}>
        <Text style={s.h}>Weekly Macros</Text>
        <Text>Calories: {plan.macros.calories} kcal</Text>
        <Text>Protein: {plan.macros.protein} g</Text>
        <Text>Carbs: {plan.macros.carbs} g</Text>
        <Text>Fat: {plan.macros.fat} g</Text>
      </View>

      {/* Days */}
      <View style={s.card}>
        <Text style={s.h}>Days</Text>
        {plan.week.days.map((d) => (
          <View key={d.day} style={s.row}>
            <Text style={s.dayLabel}>Day {d.day}</Text>
            <View style={s.rowBtns}>
              <TouchableOpacity
                style={s.smallBtn}
                onPress={() => router.push({ pathname: "/plan/meal/[day]", params: { day: String(d.day) } })}
              >
                <Text style={s.smallBtnT}>Meals</Text>
              </TouchableOpacity>
              <TouchableOpacity
                style={s.smallBtn}
                onPress={() => router.push({ pathname: "/track/workout/[day]", params: { day: String(d.day) } })}
              >
                <Text style={s.smallBtnT}>Workout</Text>
              </TouchableOpacity>
            </View>
          </View>
        ))}
      </View>

      {/* Explanations (optional) */}
      {plan.explanations && (
        <View style={s.card}>
          <Text style={s.h}>Why this plan?</Text>
          {plan.explanations.workoutRationale ? <Text style={s.p}>{plan.explanations.workoutRationale}</Text> : null}
          {plan.explanations.nutritionScience ? <Text style={s.p}>{plan.explanations.nutritionScience}</Text> : null}
        </View>
      )}
    </ScrollView>
  );
}

const s = StyleSheet.create({
  center: { flex: 1, alignItems: "center", justifyContent: "center", padding: 20 },
  err: { color: "#dc2626", marginBottom: 10 },
  btn: { backgroundColor: "#111827", padding: 12, borderRadius: 10 },
  btnT: { color: "white", fontWeight: "700" },

  card: { backgroundColor: "white", margin: 12, padding: 16, borderRadius: 12, borderColor: "#eee", borderWidth: 1, gap: 6 },
  h: { fontSize: 18, fontWeight: "700", marginBottom: 6 },
  row: { flexDirection: "row", alignItems: "center", justifyContent: "space-between", paddingVertical: 8, borderTopWidth: 1, borderColor: "#f3f4f6" },
  rowBtns: { flexDirection: "row", gap: 8 },
  dayLabel: { fontWeight: "600" },
  smallBtn: { backgroundColor: "#111827", paddingVertical: 8, paddingHorizontal: 12, borderRadius: 10 },
  smallBtnT: { color: "white", fontWeight: "600" },
  p: { color: "#374151" },
});
