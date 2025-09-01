import React from "react";
import { Alert, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { api } from "../../lib/api";

export default function WeightLogScreen() {
  const [kg, setKg] = React.useState<number | undefined>(undefined);
  const [submitting, setSubmitting] = React.useState(false);

  const today = React.useMemo(() => new Date().toISOString().slice(0, 10), []);

  const submit = async () => {
    if (kg === undefined || isNaN(kg) || kg <= 0) return Alert.alert("Invalid", "Enter your weight (kg)");
    try {
      setSubmitting(true);
      await api.post("/tracking/weight", { date: today, weightKg: kg });
      Alert.alert("Saved", `Logged ${kg} kg`);
    } catch (e: any) {
      Alert.alert("Error", e?.response?.data?.message ?? "Failed to save");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <View style={s.c}>
      <Text style={s.t}>Body Weight</Text>
      <Text style={s.sub}>{today}</Text>

      <Text style={s.h}>Weight (kg)</Text>
      <TextInput
        style={s.input}
        keyboardType="decimal-pad"
        placeholder="e.g. 72.5"
        value={kg === undefined ? "" : String(kg)}
        onChangeText={(t) => setKg(t ? Number(t.replace(/[^\d.]/g, "")) : undefined)}
      />

      <TouchableOpacity style={[s.btn, submitting && { opacity: 0.7 }]} onPress={submit} disabled={submitting}>
        <Text style={s.btnT}>{submitting ? "Saving..." : "Save Weight"}</Text>
      </TouchableOpacity>
    </View>
  );
}

const s = StyleSheet.create({
  c: { flex: 1, padding: 20, gap: 12 },
  t: { fontSize: 22, fontWeight: "700", textAlign: "center" },
  sub: { textAlign: "center", color: "#6b7280" },
  h: { fontWeight: "700", marginTop: 8 },
  input: { borderWidth: 1, borderColor: "#e5e7eb", borderRadius: 12, padding: 12, backgroundColor: "white" },
  btn: { marginTop: 12, backgroundColor: "#111827", borderRadius: 12, padding: 14, alignItems: "center" },
  btnT: { color: "white", fontWeight: "700" },
});
