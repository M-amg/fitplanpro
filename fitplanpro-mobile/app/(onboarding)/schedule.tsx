import { zodResolver } from "@hookform/resolvers/zod";
import { router } from "expo-router";
import React from "react";
import { SubmitHandler, useForm } from "react-hook-form";
import { ScrollView, StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { z } from "zod";
import { useWizard } from "../../lib/wizard";

const schema = z.object({
  preferredWorkoutTime: z.enum(["MORNING", "AFTERNOON", "EVENING"]),
  equipmentAvailable: z.array(z.string()).optional(),
});
type FormData = z.infer<typeof schema>;

const EQUIP = ["DUMBBELLS", "BARBELL", "MACHINES", "KETTLEBELLS", "BANDS", "BODYWEIGHT"];

export default function ScheduleScreen() {
  const { data, setPartial } = useWizard();
  const [time, setTime] = React.useState<FormData["preferredWorkoutTime"]>(
    (data.preferredWorkoutTime as any) ?? "MORNING"
  );
  const [equip, setEquip] = React.useState<string[]>(data.equipmentAvailable ?? []);

  const { handleSubmit } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { preferredWorkoutTime: time, equipmentAvailable: equip },
  });

  const toggle = (e: string) =>
    setEquip((arr) => (arr.includes(e) ? arr.filter((x) => x !== e) : [...arr, e]));

  const onSubmit: SubmitHandler<FormData> = () => {
    setPartial({ preferredWorkoutTime: time, equipmentAvailable: equip });
    router.push("/(onboarding)/summary");
  };

  return (
    <ScrollView contentContainerStyle={s.c}>
      <Text style={s.t}>Preferred Workout Time</Text>
      <View style={s.row}>
        {(["MORNING", "AFTERNOON", "EVENING"] as const).map((t) => {
          const sel = time === t;
          return (
            <TouchableOpacity key={t} style={[s.pill, sel && s.pillA]} onPress={() => setTime(t)}>
              <Text style={[s.pillT, sel && s.pillTA]}>{t}</Text>
            </TouchableOpacity>
          );
        })}
      </View>

      <Text style={[s.t, { marginTop: 16 }]}>Equipment Available</Text>
      <View style={s.row}>
        {EQUIP.map((e) => {
          const sel = equip.includes(e);
          return (
            <TouchableOpacity key={e} style={[s.pill, sel && s.pillA]} onPress={() => toggle(e)}>
              <Text style={[s.pillT, sel && s.pillTA]}>{e}</Text>
            </TouchableOpacity>
          );
        })}
      </View>

      <TouchableOpacity style={s.btn} onPress={handleSubmit(onSubmit)}>
        <Text style={s.btnT}>Continue</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const s = StyleSheet.create({
  c: { padding: 20, gap: 10 },
  t: { fontSize: 18, fontWeight: "700", marginBottom: 8 },
  row: { flexDirection: "row", flexWrap: "wrap", gap: 8 },
  pill: { paddingVertical: 8, paddingHorizontal: 12, borderWidth: 1, borderColor: "#d1d5db", borderRadius: 9999 },
  pillA: { backgroundColor: "#111827", borderColor: "#111827" },
  pillT: { color: "#111827", fontWeight: "600" },
  pillTA: { color: "white" },
  btn: { marginTop: 16, backgroundColor: "#111827", borderRadius: 12, padding: 14, alignItems: "center" },
  btnT: { color: "white", fontWeight: "700" },
});
