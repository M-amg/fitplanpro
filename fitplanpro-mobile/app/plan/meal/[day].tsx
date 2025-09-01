import { useLocalSearchParams } from "expo-router";
import React from "react";
import { ActivityIndicator, ScrollView, StyleSheet, Text, View } from "react-native";
import { api } from "../../../lib/api";
import type { Meal, Plan } from "../../../lib/types";

export default function MealPlanDetails() {
  const { day } = useLocalSearchParams<{ day: string }>();
  const dayNum = Number(day);

  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState<string | null>(null);
  const [meals, setMeals] = React.useState<Meal[] | null>(null);
  const [kcal, setKcal] = React.useState<number | null>(null);

  React.useEffect(() => {
    const load = async () => {
      try {
        setError(null);
        const { data } = await api.get<Plan>("/plans/current");
        const target = data.week.days.find((d) => d.day === dayNum);
        if (!target) throw new Error("Day not found");
        setMeals(target.meals);
        // Optional daily total
        const total = target.meals.reduce((sum, m) => sum + (m.kcal ?? 0), 0);
        setKcal(total || null);
      } catch (e: any) {
        setError(e?.response?.data?.message ?? e?.message ?? "Failed to load meals");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [dayNum]);

  if (loading) return <View style={s.center}><ActivityIndicator /></View>;
  if (error) return <View style={s.center}><Text style={s.err}>{error}</Text></View>;
  if (!meals) return <View style={s.center}><Text>No meals.</Text></View>;

  return (
    <ScrollView style={{ flex: 1 }}>
      <View style={s.card}>
        <Text style={s.h}>Day {dayNum} — Meals</Text>
        {kcal !== null && <Text style={{ marginBottom: 8 }}>Estimated total: {kcal} kcal</Text>}

        {meals.map((meal, idx) => (
          <View key={idx} style={s.mealCard}>
            <Text style={s.mealTitle}>{meal.slot}</Text>
            {meal.items?.length ? meal.items.map((it, i) => (
              <View key={i} style={s.row}>
                <Text style={s.foodName}>{it.name}</Text>
                <Text style={s.foodMeta}>
                  {it.grams ? `${it.grams}g` : ""}{it.kcal ? `  ·  ${it.kcal} kcal` : ""}
                </Text>
              </View>
            )) : <Text style={{ color: "#6b7280" }}>—</Text>}
          </View>
        ))}
      </View>
    </ScrollView>
  );
}

const s = StyleSheet.create({
  center: { flex: 1, alignItems: "center", justifyContent: "center", padding: 20 },
  err: { color: "#dc2626" },
  card: { backgroundColor: "white", margin: 12, padding: 16, borderRadius: 12, borderColor: "#eee", borderWidth: 1 },
  h: { fontSize: 18, fontWeight: "700", marginBottom: 6 },
  mealCard: { borderTopWidth: 1, borderColor: "#f3f4f6", paddingVertical: 10 },
  mealTitle: { fontWeight: "700", marginBottom: 6 },
  row: { flexDirection: "row", justifyContent: "space-between", marginBottom: 4 },
  foodName: { color: "#111827" },
  foodMeta: { color: "#6b7280" },
});
