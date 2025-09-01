// app/(onboarding)/summary.tsx
import React from "react";
import {
  Alert,
  ScrollView,
  StyleSheet,
  Text,
  TouchableOpacity,
  View,
  ActivityIndicator,
} from "react-native";
import { router } from "expo-router";
import { api } from "../../lib/api";
import { useWizard } from "../../lib/wizard";
import { useAuth } from "../../lib/auth";
import {
  DietPreference,
  FitnessGoal,
  Gender,
  TrainingExperience,
  TrainingLocation,
} from "../../lib/types";

// Convert wizard data into the exact shape your backend wants
function buildProfileBody(d: ReturnType<typeof useWizard>["data"]) {
  return {
    gender: d.gender as Gender, // "MALE" | "FEMALE" | ...
    age: d.age as number,
    height: d.heightCm as number,
    currentWeight: d.weightKg as number,
    targetWeight: (d.targetWeightKg ?? d.weightKg) as number,
    goalType: d.goal as FitnessGoal,
    trainingExperience: d.trainingExperience as TrainingExperience,
    trainingLocation: d.trainingLocation as TrainingLocation,
    daysPerWeek: d.daysPerWeek as number,
    dietPreference: d.dietPreference as DietPreference,
    mealsPerDay: d.mealsPerDay as number,
    snacksPerDay: (d.snacksPerDay ?? 0) as number,
    locationCulture: d.locationCulture ?? "",
    medicalConditions: d.medicalConditions ?? "",
    foodAllergies: (d.allergies ?? []).join(", "),
    timePerWorkout: (d.timePerWorkoutMin ?? 60) as number,
    equipmentAvailable: (d.equipmentAvailable ?? []).join(", "),
    budgetConstraints: (d.budgetConstraints ?? "MEDIUM") as "LOW" | "MEDIUM" | "HIGH",
    preferredWorkoutTime: (d.preferredWorkoutTime ?? "MORNING") as
      | "MORNING"
      | "AFTERNOON"
      | "EVENING",
  };
}

export default function SummaryScreen() {
  const { data, reset } = useWizard();
  const { setHasProfile } = useAuth();
  const [submitting, setSubmitting] = React.useState(false);

  const submit = async () => {
    try {
      setSubmitting(true);

      // Build and send the payload
      const body = buildProfileBody(data);
      await api.post("/profiles", body);

      // (Optional) mark profile complete in client state
      if (typeof setHasProfile === "function") setHasProfile(true);

      // Clear local wizard data and go to the app
      reset();
      Alert.alert("Profile created", "Your profile was saved.", [
        { text: "Continue", onPress: () => router.replace("/dashboard") },
      ]);
    } catch (e: any) {
      const msg = e?.response?.data?.message ?? "Failed to create profile";
      Alert.alert("Error", String(msg));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <ScrollView contentContainerStyle={s.c}>
      <Text style={s.t}>Review</Text>

      {Object.entries(data).map(([k, v]) => (
        <View key={k} style={s.row}>
          <Text style={s.k}>{k}</Text>
          <Text style={s.v}>{Array.isArray(v) ? v.join(", ") : String(v ?? "-")}</Text>
        </View>
      ))}

      <TouchableOpacity
        style={[s.btn, submitting && { opacity: 0.7 }]}
        onPress={submit}
        disabled={submitting}
      >
        {submitting ? (
          <ActivityIndicator />
        ) : (
          <Text style={s.btnT}>Finish & Create Profile</Text>
        )}
      </TouchableOpacity>

      <TouchableOpacity style={s.secondary} onPress={() => router.back()}>
        <Text style={s.secondaryT}>Back</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const s = StyleSheet.create({
  c: { padding: 20, gap: 10 },
  t: { fontSize: 22, fontWeight: "700", textAlign: "center", marginBottom: 12 },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderColor: "#eee",
  },
  k: { fontWeight: "600", color: "#374151" },
  v: { color: "#111827", flexShrink: 1, textAlign: "right", marginLeft: 12 },
  btn: {
    marginTop: 16,
    backgroundColor: "#111827",
    borderRadius: 12,
    padding: 14,
    alignItems: "center",
  },
  btnT: { color: "white", fontWeight: "700" },
  secondary: {
    marginTop: 10,
    borderWidth: 1,
    borderColor: "#111827",
    borderRadius: 12,
    padding: 12,
    alignItems: "center",
  },
  secondaryT: { color: "#111827", fontWeight: "700" },
});
