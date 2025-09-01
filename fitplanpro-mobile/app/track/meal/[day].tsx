import { useLocalSearchParams } from "expo-router";
import React from "react";
import {
    ActivityIndicator,
    Alert,
    ScrollView,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View
} from "react-native";
import { api } from "../../../lib/api";
import type { Meal, MealItem, Plan, TrackedMeal } from "../../../lib/types";

type Slot = "BREAKFAST" | "SNACK1" | "LUNCH" | "SNACK2" | "DINNER";
const SLOTS: Slot[] = ["BREAKFAST", "SNACK1", "LUNCH", "SNACK2", "DINNER"];

export default function MealLogging() {
  const { day } = useLocalSearchParams<{ day: string }>();
  const dayNum = Number(day);

  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState<string | null>(null);

  // plan + defaults
  const [planId, setPlanId] = React.useState<string | null>(null);
  const [templateMeals, setTemplateMeals] = React.useState<Meal[]>([]);

  // form state
  const [slot, setSlot] = React.useState<Slot>("BREAKFAST");
  const [items, setItems] = React.useState<MealItem[]>([
    { name: "", grams: undefined, kcal: undefined },
  ]);
  const [submitting, setSubmitting] = React.useState(false);

  React.useEffect(() => {
    const load = async () => {
      try {
        setError(null);
        const { data } = await api.get<Plan>("/plans/current");
        setPlanId(data.planId);
        const dayPlan = data.week.days.find((d) => d.day === dayNum);
        setTemplateMeals(dayPlan?.meals ?? []);
      } catch (e: any) {
        setError(e?.response?.data?.message ?? "Failed to load plan");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [dayNum]);

  const addItemRow = () =>
    setItems((prev) => [...prev, { name: "", grams: undefined, kcal: undefined }]);

  const removeItemRow = (idx: number) =>
    setItems((prev) => prev.filter((_, i) => i !== idx));

  const updateItem = (idx: number, patch: Partial<MealItem>) =>
    setItems((prev) => prev.map((it, i) => (i === idx ? { ...it, ...patch } : it)));

  const useTemplateForSlot = (s: Slot) => {
    const found = templateMeals.find((m) => m.slot === s);
    if (found?.items?.length) {
      setItems(
        found.items.map((it) => ({
          name: it.name ?? "",
          grams: it.grams,
          kcal: it.kcal,
          protein: it.protein,
          carbs: it.carbs,
          fat: it.fat,
        }))
      );
    } else {
      setItems([{ name: "", grams: undefined, kcal: undefined }]);
    }
  };

  const submit = async () => {
    // Basic validation
    const cleaned = items
      .map((it) => ({ ...it, name: (it.name ?? "").trim() }))
      .filter((it) => it.name.length > 0);

    if (!cleaned.length) {
      Alert.alert("Missing items", "Please add at least one food item.");
      return;
    }

    try {
      setSubmitting(true);
      const payload: TrackedMeal = {
        date: new Date().toISOString().slice(0, 10), // yyyy-mm-dd
        planId: planId ?? undefined,
        day: dayNum,
        mealSlot: slot,
        items: cleaned,
      };
      await api.post("/tracking/meals", payload);
      Alert.alert("Saved", "Meal logged.");
    } catch (e: any) {
      Alert.alert("Error", e?.response?.data?.message ?? "Failed to save meal");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <View style={s.center}><ActivityIndicator /></View>;
  if (error) return <View style={s.center}><Text style={s.err}>{error}</Text></View>;

  return (
    <ScrollView style={{ flex: 1 }}>
      {/* Slot picker */}
      <View style={s.card}>
        <Text style={s.h}>Day {dayNum} — Log Meal</Text>
        <View style={s.rowWrap}>
          {SLOTS.map((slt) => {
            const sel = slt === slot;
            return (
              <TouchableOpacity
                key={slt}
                style={[s.pill, sel && s.pillA]}
                onPress={() => { setSlot(slt); useTemplateForSlot(slt); }}
              >
                <Text style={[s.pillT, sel && s.pillTA]}>{slt}</Text>
              </TouchableOpacity>
            );
          })}
        </View>
        <TouchableOpacity style={s.outlineBtn} onPress={() => useTemplateForSlot(slot)}>
          <Text style={s.outlineBtnT}>Use Plan’s {slot} as Template</Text>
        </TouchableOpacity>
      </View>

      {/* Items */}
      <View style={s.card}>
        <Text style={s.h}>Items</Text>
        {items.map((it, idx) => (
          <View key={idx} style={s.itemRow}>
            <TextInput
              style={[s.input, { flex: 1 }]}
              placeholder="Food name"
              value={it.name ?? ""}
              onChangeText={(t) => updateItem(idx, { name: t })}
            />
            <TextInput
              style={[s.input, s.num]}
              keyboardType="number-pad"
              placeholder="g"
              value={it.grams === undefined ? "" : String(it.grams)}
              onChangeText={(t) => updateItem(idx, { grams: t ? Number(t.replace(/[^\d]/g, "")) : undefined })}
            />
            <TextInput
              style={[s.input, s.num]}
              keyboardType="number-pad"
              placeholder="kcal"
              value={it.kcal === undefined ? "" : String(it.kcal)}
              onChangeText={(t) => updateItem(idx, { kcal: t ? Number(t.replace(/[^\d]/g, "")) : undefined })}
            />
            <TouchableOpacity onPress={() => removeItemRow(idx)} style={s.delBtn}>
              <Text style={s.delBtnT}>✕</Text>
            </TouchableOpacity>
          </View>
        ))}

        <TouchableOpacity style={s.outlineBtn} onPress={addItemRow}>
          <Text style={s.outlineBtnT}>Add Item</Text>
        </TouchableOpacity>
      </View>

      {/* Submit */}
      <View style={{ padding: 12 }}>
        <TouchableOpacity style={[s.btn, submitting && { opacity: 0.7 }]} onPress={submit} disabled={submitting}>
          <Text style={s.btnT}>{submitting ? "Saving..." : "Save Meal"}</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}

const s = StyleSheet.create({
  center: { flex: 1, alignItems: "center", justifyContent: "center", padding: 20 },
  err: { color: "#dc2626" },

  card: { backgroundColor: "white", margin: 12, padding: 16, borderRadius: 12, borderColor: "#eee", borderWidth: 1, gap: 8 },
  h: { fontSize: 18, fontWeight: "700" },

  rowWrap: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
  pill: { paddingVertical: 8, paddingHorizontal: 12, borderWidth: 1, borderColor: "#d1d5db", borderRadius: 9999 },
  pillA: { backgroundColor: "#111827", borderColor: "#111827" },
  pillT: { color: "#111827", fontWeight: "600" },
  pillTA: { color: "white" },

  itemRow: { flexDirection: "row", alignItems: "center", gap: 8, marginBottom: 8 },
  input: { borderWidth: 1, borderColor: "#e5e7eb", borderRadius: 10, padding: 10, backgroundColor: "white" },
  num: { width: 80 },

  outlineBtn: { borderWidth: 1, borderColor: "#111827", padding: 12, borderRadius: 10, alignItems: "center" },
  outlineBtnT: { color: "#111827", fontWeight: "700" },

  btn: { backgroundColor: "#111827", padding: 14, borderRadius: 12, alignItems: "center" },
  btnT: { color: "white", fontWeight: "700" },

  delBtn: { paddingHorizontal: 8, paddingVertical: 6, borderRadius: 8, backgroundColor: "#fee2e2" },
  delBtnT: { color: "#991b1b", fontWeight: "700" },
});
