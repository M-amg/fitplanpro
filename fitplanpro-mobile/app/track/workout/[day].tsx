import { useLocalSearchParams } from "expo-router";
import React from "react";
import { ActivityIndicator, Alert, ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { api } from "../../../lib/api";
import type { Plan, TrackedWorkout, TrackedWorkoutSet } from "../../../lib/types";

export default function WorkoutLogging() {
  const { day } = useLocalSearchParams<{ day: string }>();
  const dayNum = Number(day);

  const [loading, setLoading] = React.useState(true);
  const [error, setError] = React.useState<string | null>(null);
  const [planId, setPlanId] = React.useState<string | null>(null);
  const [title, setTitle] = React.useState<string | null>(null);
  const [sets, setSets] = React.useState<TrackedWorkoutSet[]>([]);
  const [completed, setCompleted] = React.useState(false);
  const [submitting, setSubmitting] = React.useState(false);

  React.useEffect(() => {
    const load = async () => {
      try {
        setError(null);
        const { data } = await api.get<Plan>("/plans/current");
        setPlanId(data.planId);
        const target = data.week.days.find((d) => d.day === dayNum);
        if (!target || !target.workout) {
          setTitle(null);
          setSets([]);
        } else {
          setTitle(target.workout.title);
          // initialize with first set of each exercise if you want:
          const initial: TrackedWorkoutSet[] = [];
          target.workout.exercises.forEach((ex) => {
            const first = ex.sets?.[0];
            initial.push({
              exerciseName: ex.name,
              set: 1,
              reps: first?.reps ?? 0,
              weightKg: first?.weightKg ?? undefined,
            });
          });
          setSets(initial);
        }
      } catch (e: any) {
        setError(e?.response?.data?.message ?? "Failed to load workout");
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [dayNum]);

  const updateSet = (idx: number, patch: Partial<TrackedWorkoutSet>) => {
    setSets((prev) => prev.map((s, i) => (i === idx ? { ...s, ...patch } : s)));
  };

  const addSetRow = () => setSets((prev) => [...prev, { exerciseName: "", set: 1, reps: 0 }]);
  const removeSetRow = (idx: number) => setSets((prev) => prev.filter((_, i) => i !== idx));

  const submit = async () => {
    try {
      setSubmitting(true);
      const payload: TrackedWorkout = {
        date: new Date().toISOString().slice(0, 10), // yyyy-mm-dd
        planId: planId ?? undefined,
        day: dayNum,
        completed,
        sets: sets.length ? sets : undefined,
      };
      await api.post("/tracking/workouts", payload);
      Alert.alert("Saved", "Workout logged.");
    } catch (e: any) {
      Alert.alert("Error", e?.response?.data?.message ?? "Failed to save workout");
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <View style={s.center}><ActivityIndicator /></View>;
  if (error) return <View style={s.center}><Text style={s.err}>{error}</Text></View>;

  return (
    <ScrollView style={{ flex: 1 }}>
      <View style={s.card}>
        <Text style={s.h}>Day {dayNum} — Workout</Text>
        <Text style={s.sub}>{title ?? "Rest / No workout planned"}</Text>
      </View>

      <View style={s.card}>
        <Text style={s.h}>Completed?</Text>
        <View style={s.row}>
          <TouchableOpacity style={[s.pill, completed && s.pillA]} onPress={() => setCompleted(true)}>
            <Text style={[s.pillT, completed && s.pillTA]}>Yes</Text>
          </TouchableOpacity>
          <TouchableOpacity style={[s.pill, !completed && s.pillA]} onPress={() => setCompleted(false)}>
            <Text style={[s.pillT, !completed && s.pillTA]}>No</Text>
          </TouchableOpacity>
        </View>
      </View>

      <View style={s.card}>
        <Text style={s.h}>Sets (optional)</Text>
        {sets.map((sRow, idx) => (
          <View key={idx} style={s.setRow}>
            <TextInput
              style={[s.input, { flex: 1 }]}
              placeholder="Exercise"
              value={sRow.exerciseName}
              onChangeText={(t) => updateSet(idx, { exerciseName: t })}
            />
            <TextInput
              style={[s.input, s.num]}
              keyboardType="number-pad"
              placeholder="Set"
              value={String(sRow.set ?? "")}
              onChangeText={(t) => updateSet(idx, { set: Number(t.replace(/[^\d]/g, "")) || 0 })}
            />
            <TextInput
              style={[s.input, s.num]}
              keyboardType="number-pad"
              placeholder="Reps"
              value={String(sRow.reps ?? "")}
              onChangeText={(t) => updateSet(idx, { reps: Number(t.replace(/[^\d]/g, "")) || 0 })}
            />
            <TextInput
              style={[s.input, s.num]}
              keyboardType="number-pad"
              placeholder="Kg"
              value={sRow.weightKg === undefined ? "" : String(sRow.weightKg)}
              onChangeText={(t) => updateSet(idx, { weightKg: t ? Number(t.replace(/[^\d.]/g, "")) : undefined })}
            />
            <TouchableOpacity onPress={() => removeSetRow(idx)} style={s.delBtn}>
              <Text style={s.delBtnT}>✕</Text>
            </TouchableOpacity>
          </View>
        ))}

        <TouchableOpacity style={s.outlineBtn} onPress={addSetRow}>
          <Text style={s.outlineBtnT}>Add Set</Text>
        </TouchableOpacity>
      </View>

      <View style={{ padding: 12 }}>
        <TouchableOpacity style={[s.btn, submitting && { opacity: 0.7 }]} onPress={submit} disabled={submitting}>
          <Text style={s.btnT}>{submitting ? "Saving..." : "Save Workout"}</Text>
        </TouchableOpacity>
      </View>
    </ScrollView>
  );
}

const s = StyleSheet.create({
  center: { flex: 1, alignItems: "center", justifyContent: "center", padding: 20 },
  err: { color: "#dc2626" },

  card: { backgroundColor: "white", margin: 12, padding: 16, borderRadius: 12, borderColor: "#eee", borderWidth: 1, gap: 6 },
  h: { fontSize: 18, fontWeight: "700" },
  sub: { color: "#374151" },

  row: { flexDirection: "row", gap: 8 },
  pill: { paddingVertical: 8, paddingHorizontal: 12, borderWidth: 1, borderColor: "#d1d5db", borderRadius: 9999 },
  pillA: { backgroundColor: "#111827", borderColor: "#111827" },
  pillT: { color: "#111827", fontWeight: "600" },
  pillTA: { color: "white" },

  setRow: { flexDirection: "row", alignItems: "center", gap: 8, marginBottom: 8 },
  input: { borderWidth: 1, borderColor: "#e5e7eb", borderRadius: 10, padding: 10, backgroundColor: "white" },
  num: { width: 70 },
  delBtn: { paddingHorizontal: 8, paddingVertical: 6, borderRadius: 8, backgroundColor: "#fee2e2" },
  delBtnT: { color: "#991b1b", fontWeight: "700" },

  outlineBtn: { borderWidth: 1, borderColor: "#111827", padding: 12, borderRadius: 10, alignItems: "center" },
  outlineBtnT: { color: "#111827", fontWeight: "700" },
  btn: { backgroundColor: "#111827", padding: 14, borderRadius: 12, alignItems: "center" },
  btnT: { color: "white", fontWeight: "700" },
});
