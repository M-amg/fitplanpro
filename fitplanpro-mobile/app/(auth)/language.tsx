import { router } from "expo-router";
import React from "react";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { setLanguage, setOnboarded } from "../../lib/lang";
import type { LanguagePreference } from "../../lib/types";

const langs: { code: string; label: string; value: LanguagePreference }[] = [
  { code: "US", label: "English", value: "ENGLISH" },
  { code: "ES", label: "Español", value: "SPANISH" },
  { code: "FR", label: "Français", value: "FRENCH" },
];

export default function LanguageSelect() {
  const [sel, setSel] = React.useState<LanguagePreference>("ENGLISH");

  const continueNext = async () => {
    await setLanguage(sel);
    await setOnboarded();
    router.replace("/(auth)/welcome");
  };

  return (
    <View style={s.c}>
      <Text style={s.title}>Welcome to FitPlan Pro</Text>
      <Text style={s.sub}>Your AI-powered fitness companion</Text>

      <View style={s.langRow}>
        {langs.map((l) => {
          const active = sel === l.value;
          return (
            <TouchableOpacity key={l.code} style={[s.lang, active && s.langA]} onPress={() => setSel(l.value)}>
              <Text style={[s.langCode, active && s.langCodeA]}>{l.code}</Text>
              <Text style={[s.langLabel, active && s.langLabelA]}>{l.label}</Text>
            </TouchableOpacity>
          );
        })}
      </View>

      <TouchableOpacity style={s.btn} onPress={continueNext}>
        <Text style={s.btnT}>Continue</Text>
      </TouchableOpacity>

      <TouchableOpacity onPress={continueNext} style={{ marginTop: 10 }}>
        <Text style={{ textAlign: "center", color: "#6b7280" }}>Skip for now</Text>
      </TouchableOpacity>
    </View>
  );
}

const s = StyleSheet.create({
  c: { flex: 1, padding: 20, gap: 16 },
  title: { fontSize: 18, fontWeight: "800", textAlign: "center", marginTop: 16 },
  sub: { textAlign: "center", color: "#6b7280" },
  langRow: { flexDirection: "row", justifyContent: "space-between", marginTop: 40 },
  lang: { flex: 1, marginHorizontal: 4, backgroundColor: "white", borderWidth: 1, borderColor: "#e5e7eb", borderRadius: 12, paddingVertical: 14, alignItems: "center" },
  langA: { borderColor: "#111827" },
  langCode: { fontSize: 18, fontWeight: "800", color: "#111827" },
  langCodeA: { color: "#111827" },
  langLabel: { color: "#6b7280", marginTop: 4 },
  langLabelA: { color: "#111827" },
  btn: { marginTop: "auto", backgroundColor: "#111827", borderRadius: 12, padding: 14, alignItems: "center" },
  btnT: { color: "white", fontWeight: "700" },
});
