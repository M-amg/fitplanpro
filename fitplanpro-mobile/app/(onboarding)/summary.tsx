import { router } from "expo-router";
import React from "react";
import {
  Alert,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
} from "react-native";
import { api } from "../../lib/api"; // your axios instance
import { useWizard } from "../../lib/wizard";

import { useAuth } from "../../lib/auth";

export default function SummaryScreen() {
  const { data } = useWizard();
  const { setHasProfile } = useAuth();

  const submit = async () => {
    try {
      // Send profile to backend
      await api.put("/profile", {
        gender: data.gender,
        age: data.age,
        heightCm: data.heightCm,
        weightKg: data.weightKg,
        targetWeightKg: data.targetWeightKg,
        goal: data.goal,
        dietPreference: data.dietPreference,
        mealsPerDay: data.mealsPerDay,
        snacksPerDay: data.snacksPerDay,
        allergies: data.allergies,
        trainingExperience: data.trainingExperience,
        trainingLocation: data.trainingLocation,
        daysPerWeek: data.daysPerWeek,
        timePerWorkoutMin: data.timePerWorkoutMin,
        preferredWorkoutTime: data.preferredWorkoutTime,
        equipmentAvailable: data.equipmentAvailable,
      });

      // Generate (or fetch) plan
      await api.post("/plans:generate", { useCache: true, force: false });
      setHasProfile(true);
      router.replace("/dashboard");
      Alert.alert("Done", "Profile saved and plan generated.", [
        {
          text: "Go to app",
          onPress: () => router.replace("/(tabs)/dashboard"),
        },
      ]);
    } catch (e: any) {
      Alert.alert(
        "Error",
        e?.response?.data?.message ?? "Failed to submit profile"
      );
    }
  };

  return (
    <ScrollView contentContainerStyle={s.c}>
      <Text style={s.t}>Review</Text>

      {Object.entries(data).map(([k, v]) => (
        <View key={k} style={s.row}>
          <Text style={s.k}>{k}</Text>
          <Text style={s.v}>
            {Array.isArray(v) ? v.join(", ") : String(v ?? "-")}
          </Text>
        </View>
      ))}

      <TouchableOpacity style={s.btn} onPress={submit}>
        <Text style={s.btnT}>Finish & Generate Plan</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const s = StyleSheet.create({
  c: { padding: 20, gap: 8 },
  t: { fontSize: 22, fontWeight: "700", textAlign: "center", marginBottom: 12 },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    paddingVertical: 8,
    borderBottomWidth: 1,
    borderColor: "#eee",
  },
  k: { fontWeight: "600", color: "#374151" },
  v: { color: "#111827", flexShrink: 1, textAlign: "right" },
  btn: {
    marginTop: 16,
    backgroundColor: "#111827",
    borderRadius: 12,
    padding: 14,
    alignItems: "center",
  },
  btnT: { color: "white", fontWeight: "700" },
});
