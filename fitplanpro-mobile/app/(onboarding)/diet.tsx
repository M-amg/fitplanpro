import { zodResolver } from "@hookform/resolvers/zod";
import { router } from "expo-router";
import React from "react";
import { Controller, SubmitHandler, useForm } from "react-hook-form";
import { ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { z } from "zod";
import { DietPreference } from "../../lib/types";
import { useWizard } from "../../lib/wizard";

const schema = z.object({
  dietPreference: z.nativeEnum(DietPreference),
  mealsPerDay: z.number().min(1).max(6),
  snacksPerDay: z.number().min(0).max(3).optional(),
  allergiesRaw: z.string().optional(), // comma separated
});
type FormData = z.infer<typeof schema>;

export default function DietScreen() {
  const { data, setPartial } = useWizard();
  const [diet, setDiet] = React.useState<DietPreference>(data.dietPreference ?? DietPreference.Normal);

  const { control, handleSubmit } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: {
      dietPreference: diet,
      mealsPerDay: data.mealsPerDay ?? 3,
      snacksPerDay: data.snacksPerDay ?? 1,
      allergiesRaw: (data.allergies ?? []).join(", "),
    },
  });

  const onSubmit: SubmitHandler<FormData> = (values) => {
    const allergies = (values.allergiesRaw ?? "")
      .split(",")
      .map((s) => s.trim())
      .filter(Boolean);
    setPartial({
      dietPreference: diet,
      mealsPerDay: values.mealsPerDay,
      snacksPerDay: values.snacksPerDay,
      allergies,
    });
    router.push("/(onboarding)/experience");
  };

  return (
    <ScrollView contentContainerStyle={s.c}>
      <Text style={s.t}>Diet & Allergies</Text>

      <Text style={s.h}>Diet Preference</Text>
      <View style={s.row}>
        {Object.values(DietPreference).map((d) => {
          const sel = diet === d;
          return (
            <TouchableOpacity key={d} style={[s.pill, sel && s.pillA]} onPress={() => setDiet(d)}>
              <Text style={[s.pillT, sel && s.pillTA]}>{d.replace("_", " ")}</Text>
            </TouchableOpacity>
          );
        })}
      </View>

      <Text style={s.h}>Meals per Day</Text>
      <Controller
        control={control}
        name="mealsPerDay"
        render={({ field: { value, onChange } }) => (
          <TextInput
            style={s.input}
            keyboardType="number-pad"
            value={String(value ?? "")}
            onChangeText={(t) => onChange(Number(t.replace(/[^\d]/g, "")))}
          />
        )}
      />

      <Text style={s.h}>Snacks per Day (optional)</Text>
      <Controller
        control={control}
        name="snacksPerDay"
        render={({ field: { value, onChange } }) => (
          <TextInput
            style={s.input}
            keyboardType="number-pad"
            value={value === undefined ? "" : String(value)}
            onChangeText={(t) => onChange(t ? Number(t.replace(/[^\d]/g, "")) : undefined)}
          />
        )}
      />

      <Text style={s.h}>Allergies (comma separated)</Text>
      <Controller
        control={control}
        name="allergiesRaw"
        render={({ field: { value, onChange } }) => (
          <TextInput style={s.input} value={value ?? ""} onChangeText={onChange} placeholder="e.g. peanuts, lactose" />
        )}
      />

      <TouchableOpacity style={s.btn} onPress={handleSubmit(onSubmit)}>
        <Text style={s.btnT}>Continue</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const s = StyleSheet.create({
  c: { padding: 20, gap: 10 },
  t: { fontSize: 22, fontWeight: "700", textAlign: "center", marginBottom: 12 },
  h: { fontWeight: "700", marginTop: 8, marginBottom: 6 },
  row: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
  pill: { paddingVertical: 8, paddingHorizontal: 12, borderWidth: 1, borderColor: "#d1d5db", borderRadius: 9999 },
  pillA: { backgroundColor: "#111827", borderColor: "#111827" },
  pillT: { color: "#111827", fontWeight: "600" },
  pillTA: { color: "white" },
  input: { borderWidth: 1, borderColor: "#e5e7eb", borderRadius: 12, padding: 12, backgroundColor: "white" },
  btn: { marginTop: 16, backgroundColor: "#111827", borderRadius: 12, padding: 14, alignItems: "center" },
  btnT: { color: "white", fontWeight: "700" },
});
