import { zodResolver } from "@hookform/resolvers/zod";
import { router } from "expo-router";
import React from "react";
import { Controller, SubmitHandler, useForm } from "react-hook-form";
import { ScrollView, StyleSheet, Text, TextInput, TouchableOpacity, View } from "react-native";
import { z } from "zod";
import { TrainingExperience, TrainingLocation } from "../../lib/types";
import { useWizard } from "../../lib/wizard";

const schema = z.object({
  trainingExperience: z.nativeEnum(TrainingExperience),
  trainingLocation: z.nativeEnum(TrainingLocation),
  daysPerWeek: z.number().min(1).max(7),
  timePerWorkoutMin: z.number().min(15).max(180).optional(),
});
type FormData = z.infer<typeof schema>;

export default function ExperienceScreen() {
  const { data, setPartial } = useWizard();
  const [exp, setExp] = React.useState<TrainingExperience>(data.trainingExperience ?? TrainingExperience.Beginner);
  const [loc, setLoc] = React.useState<TrainingLocation>(data.trainingLocation ?? TrainingLocation.Gym);

  const { control, handleSubmit } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: {
      trainingExperience: exp,
      trainingLocation: loc,
      daysPerWeek: data.daysPerWeek ?? 5,
      timePerWorkoutMin: data.timePerWorkoutMin ?? 60,
    },
  });

  const onSubmit: SubmitHandler<FormData> = (values) => {
    setPartial({
      trainingExperience: exp,
      trainingLocation: loc,
      daysPerWeek: values.daysPerWeek,
      timePerWorkoutMin: values.timePerWorkoutMin,
    });
    router.push("/(onboarding)/schedule");
  };

  return (
    <ScrollView contentContainerStyle={s.c}>
      <Text style={s.t}>Training Experience</Text>
      <View style={s.row}>
        {Object.values(TrainingExperience).map((e) => {
          const sel = exp === e;
          return (
            <TouchableOpacity key={e} style={[s.pill, sel && s.pillA]} onPress={() => setExp(e)}>
              <Text style={[s.pillT, sel && s.pillTA]}>{e}</Text>
            </TouchableOpacity>
          );
        })}
      </View>

      <Text style={s.t}>Training Location</Text>
      <View style={s.row}>
        {Object.values(TrainingLocation).map((l) => {
          const sel = loc === l;
          return (
            <TouchableOpacity key={l} style={[s.pill, sel && s.pillA]} onPress={() => setLoc(l)}>
              <Text style={[s.pillT, sel && s.pillTA]}>{l}</Text>
            </TouchableOpacity>
          );
        })}
      </View>

      <Text style={s.h}>Days per Week</Text>
      <Controller
        control={control}
        name="daysPerWeek"
        render={({ field: { value, onChange } }) => (
          <TextInput
            style={s.input}
            keyboardType="number-pad"
            value={String(value ?? "")}
            onChangeText={(t) => onChange(Number(t.replace(/[^\d]/g, "")))}
          />
        )}
      />

      <Text style={s.h}>Time per Workout (min) — optional</Text>
      <Controller
        control={control}
        name="timePerWorkoutMin"
        render={({ field: { value, onChange } }) => (
          <TextInput
            style={s.input}
            keyboardType="number-pad"
            value={value === undefined ? "" : String(value)}
            onChangeText={(t) => onChange(t ? Number(t.replace(/[^\d]/g, "")) : undefined)}
          />
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
  t: { fontSize: 18, fontWeight: "700", marginBottom: 8 },
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
