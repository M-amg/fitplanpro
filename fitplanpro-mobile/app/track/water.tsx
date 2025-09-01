import React from "react";
import { Alert, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { api } from "../../lib/api";

export default function WaterLogScreen() {
  const [ml, setMl] = React.useState<number>(250);
  const [submitting, setSubmitting] = React.useState(false);

  const today = React.useMemo(() => new Date().toISOString().slice(0, 10), []);

  const submit = async () => {
    if (!ml || ml <= 0) return Alert.alert("Invalid", "Enter amount in ml");
    try {
      setSubmitting(true);
      await api.post("/tracking/water", { date: today, ml });
      Alert.alert("Saved", `Added ${ml} ml water.`);
    } catch (e: any) {
      Alert.alert("Error", e?.response?.data?.message ?? "Failed to save");
    } finally {
      setSubmitting(false);
    }
  };

  const quick = (delta: number) => setMl((v) => Math.max(0, (v || 0) + delta));

  return (
    <View style={s.c}>
      <Text style={s.t}>Water Intake</Text>
      <Text style={s.sub}>{today}</Text>

      <View style={s.row}>
        {[250, 330, 500].map((q) => (
          <TouchableOpacity key={q} style={s.pill} onPress={() => quick(q)}>
            <Text style={s.pillT}>+{q} ml</Text>
          </TouchableOpacity>
        ))}
      </View>

      <Text style={s.h}>Amount (ml)</Text>
      <TextInput
        style={s.input}
        keyboardType="number-pad"
        placeholder="e.g. 250"
        value={ml ? String(ml) : ""}
        onChangeText={(t) => setMl(t ? Number(t.replace(/[^\d]/g, "")) : 0)}
      />

      <TouchableOpacity style={[s.btn, submitting && { opacity: 0.7 }]} onPress={submit} disabled={submitting}>
        <Text style={s.btnT}>{submitting ? "Saving..." : "Save Water"}</Text>
      </TouchableOpacity>
    </View>
  );
}

const s = StyleSheet.create({
  c: { flex: 1, padding: 20, gap: 12 },
  t: { fontSize: 22, fontWeight: "700", textAlign: "center" },
  sub: { textAlign: "center", color: "#6b7280" },
  h: { fontWeight: "700", marginTop: 8 },
  row: { flexDirection: "row", gap: 8, justifyContent: "center", marginTop: 8 },
  pill: { paddingVertical: 8, paddingHorizontal: 12, borderWidth: 1, borderColor: "#d1d5db", borderRadius: 9999 },
  pillT: { fontWeight: "600", color: "#111827" },
  input: { borderWidth: 1, borderColor: "#e5e7eb", borderRadius: 12, padding: 12, backgroundColor: "white" },
  btn: { marginTop: 12, backgroundColor: "#111827", borderRadius: 12, padding: 14, alignItems: "center" },
  btnT: { color: "white", fontWeight: "700" },
});
