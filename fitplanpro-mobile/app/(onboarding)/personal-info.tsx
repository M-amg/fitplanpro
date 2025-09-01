import { useWizard } from "@/lib/wizard";
import { zodResolver } from "@hookform/resolvers/zod";
import { router } from "expo-router";
import React from "react";
import { Controller, SubmitHandler, useForm } from "react-hook-form";
import {
  ScrollView,
  StyleSheet,
  Text,
  TextInput,
  TouchableOpacity,
  View
} from "react-native";
import { z } from "zod";
import { Gender } from "../../lib/types";


const schema = z.object({
  gender: z.nativeEnum(Gender),
  age: z.number().min(10, "Too young").max(100, "Too old"),
  heightCm: z.number().min(80).max(250),
  weightKg: z.number().min(20).max(300),
  targetWeightKg: z.number().optional(),
});
type FormData = z.infer<typeof schema>;

export default function PersonalInfoScreen() {
  const { setPartial } = useWizard();
  const {
    control,
    handleSubmit,
    setValue,
    watch,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(schema),
    defaultValues: {
      gender: Gender.Male,
      age: 25,
      heightCm: 170,
      weightKg: 70,
      targetWeightKg: 65,
    },
  });

  const onSubmit: SubmitHandler<FormData> = (values) => {
    setPartial(values); // save to wizard
    router.push("/(onboarding)/goal");
  };

  return (
    <ScrollView contentContainerStyle={s.container}>
      <Text style={s.title}>Personal Info</Text>

      <Text style={s.label}>Gender</Text>
      <View style={s.row}>
        {Object.values(Gender).map((g) => {
          const selected = watch("gender") === g;
          return (
            <TouchableOpacity
              key={g}
              style={[s.pill, selected && s.pillActive]}
              onPress={() => setValue("gender", g as Gender)}
            >
              <Text style={[s.pillText, selected && s.pillTextActive]}>
                {g}
              </Text>
            </TouchableOpacity>
          );
        })}
      </View>
      {errors.gender && <Text style={s.err}>{errors.gender.message}</Text>}

      <Text style={s.label}>Age</Text>
      <Controller
        control={control}
        name="age"
        render={({ field: { value, onChange } }) => (
          <TextInput
            style={s.input}
            keyboardType="number-pad"
            value={String(value ?? "")}
            onChangeText={(t) => onChange(Number(t.replace(/[^\d]/g, "")))}
            placeholder="Age in years"
          />
        )}
      />
      {errors.age && <Text style={s.err}>{errors.age.message}</Text>}

      <Text style={s.label}>Height (cm)</Text>
      <Controller
        control={control}
        name="heightCm"
        render={({ field: { value, onChange } }) => (
          <TextInput
            style={s.input}
            keyboardType="number-pad"
            value={String(value ?? "")}
            onChangeText={(t) => onChange(Number(t.replace(/[^\d]/g, "")))}
            placeholder="e.g. 170"
          />
        )}
      />
      {errors.heightCm && <Text style={s.err}>{errors.heightCm.message}</Text>}

      <Text style={s.label}>Weight (kg)</Text>
      <Controller
        control={control}
        name="weightKg"
        render={({ field: { value, onChange } }) => (
          <TextInput
            style={s.input}
            keyboardType="number-pad"
            value={String(value ?? "")}
            onChangeText={(t) => onChange(Number(t.replace(/[^\d]/g, "")))}
            placeholder="e.g. 70"
          />
        )}
      />
      {errors.weightKg && <Text style={s.err}>{errors.weightKg.message}</Text>}

      <Text style={s.label}>Target Weight (kg) — optional</Text>
      <Controller
        control={control}
        name="targetWeightKg"
        render={({ field: { value, onChange } }) => (
          <TextInput
            style={s.input}
            keyboardType="number-pad"
            value={value ? String(value) : ""}
            onChangeText={(t) =>
              onChange(t ? Number(t.replace(/[^\d]/g, "")) : undefined)
            }
            placeholder="e.g. 65"
          />
        )}
      />
      {errors.targetWeightKg && (
        <Text style={s.err}>{errors.targetWeightKg.message}</Text>
      )}

      <TouchableOpacity
        style={[s.button, isSubmitting && { opacity: 0.7 }]}
        onPress={handleSubmit(onSubmit)}
        disabled={isSubmitting}
      >
        <Text style={s.buttonText}>Continue</Text>
      </TouchableOpacity>
    </ScrollView>
  );
}

const s = StyleSheet.create({
  container: { padding: 20, gap: 10 },
  title: {
    fontSize: 24,
    fontWeight: "700",
    marginBottom: 10,
    textAlign: "center",
  },
  label: { fontSize: 14, fontWeight: "600", marginTop: 8, marginBottom: 4 },
  input: {
    borderWidth: 1,
    borderColor: "#e5e7eb",
    borderRadius: 12,
    padding: 12,
    backgroundColor: "white",
  },
  row: { flexDirection: "row", gap: 8 },
  pill: {
    paddingVertical: 10,
    paddingHorizontal: 14,
    borderWidth: 1,
    borderColor: "#d1d5db",
    borderRadius: 9999,
  },
  pillActive: { backgroundColor: "#111827", borderColor: "#111827" },
  pillText: { color: "#111827", fontWeight: "600" },
  pillTextActive: { color: "white" },
  button: {
    marginTop: 16,
    backgroundColor: "#111827",
    padding: 14,
    borderRadius: 12,
    alignItems: "center",
  },
  buttonText: { color: "white", fontWeight: "700" },
  err: { color: "#dc2626", marginTop: -4 },
});
