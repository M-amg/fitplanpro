import { zodResolver } from "@hookform/resolvers/zod";
import { router } from "expo-router";
import React from "react";
import { SubmitHandler, useForm } from "react-hook-form";
import { StyleSheet, Text, TouchableOpacity, View } from "react-native";
import { z } from "zod";
import { FitnessGoal } from "../../lib/types";
import { useWizard } from "../../lib/wizard";

const schema = z.object({
  goal: z.nativeEnum(FitnessGoal),
});
type FormData = z.infer<typeof schema>;

export default function GoalScreen() {
  const { data, setPartial } = useWizard();
  const [selected, setSelected] = React.useState<FitnessGoal>(data.goal ?? FitnessGoal.Recomposition);

  const { handleSubmit } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: { goal: selected },
  });

  const onSubmit: SubmitHandler<FormData> = () => {
    setPartial({ goal: selected });
    router.push("/(onboarding)/diet");
  };

  return (
    <View style={s.c}>
      <Text style={s.t}>Your Goal</Text>
      {Object.values(FitnessGoal).map((g) => {
        const sel = selected === g;
        return (
          <TouchableOpacity key={g} style={[s.item, sel && s.itemA]} onPress={() => setSelected(g)}>
            <Text style={[s.itemText, sel && s.itemTextA]}>{g.replace("_", " ")}</Text>
          </TouchableOpacity>
        );
      })}
      <TouchableOpacity style={s.btn} onPress={handleSubmit(onSubmit)}>
        <Text style={s.btnT}>Continue</Text>
      </TouchableOpacity>
    </View>
  );
}

const s = StyleSheet.create({
  c: { flex: 1, padding: 20 },
  t: { fontSize: 22, fontWeight: "700", textAlign: "center", marginBottom: 16 },
  item: { padding: 14, borderRadius: 12, borderWidth: 1, borderColor: "#d1d5db", marginBottom: 8 },
  itemA: { backgroundColor: "#111827", borderColor: "#111827" },
  itemText: { fontWeight: "600", color: "#111827" },
  itemTextA: { color: "white" },
  btn: { marginTop: 16, backgroundColor: "#111827", borderRadius: 12, padding: 14, alignItems: "center" },
  btnT: { color: "white", fontWeight: "700" },
});
