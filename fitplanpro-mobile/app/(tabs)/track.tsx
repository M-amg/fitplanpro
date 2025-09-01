import { router } from "expo-router";
import React from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";

export default function TrackHub() {
  const go = (path: string) => router.push(path);

  return (
    <View style={s.c}>
      <Text style={s.t}>Track</Text>
      <TouchableOpacity style={s.btn} onPress={() => go("/track/meal/1")}>
        <Text style={s.btnT}>Log Meal (Day 1)</Text>
      </TouchableOpacity>
      <TouchableOpacity style={s.btn} onPress={() => go("/track/workout/1")}>
        <Text style={s.btnT}>Log Workout (Day 1)</Text>
      </TouchableOpacity>
      <TouchableOpacity style={s.btn} onPress={() => go("/track/water")}>
        <Text style={s.btnT}>Log Water</Text>
      </TouchableOpacity>
      <TouchableOpacity style={s.btn} onPress={() => go("/track/weight")}>
        <Text style={s.btnT}>Log Weight</Text>
      </TouchableOpacity>
      <Text style={s.tip}>Tip: change the “1” in path to log for another day.</Text>
    </View>
  );
}

const s = StyleSheet.create({
  c: { flex: 1, justifyContent: "center", padding: 20, gap: 12 },
  t: { fontSize: 24, fontWeight: "700", textAlign: "center", marginBottom: 4 },
  btn: { backgroundColor: "#111827", borderRadius: 12, padding: 14, alignItems: "center" },
  btnT: { color: "white", fontWeight: "700" },
  tip: { textAlign: "center", color: "#6b7280", marginTop: 8 },
});
