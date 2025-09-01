// lib/wizard.ts (enhanced)
import AsyncStorage from "@react-native-async-storage/async-storage";
import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import { DietPreference, FitnessGoal, Gender, TrainingExperience, TrainingLocation } from "./types";

export type WizardData = {
  gender?: Gender;
  age?: number;
  heightCm?: number;
  weightKg?: number;
  targetWeightKg?: number;
  goal?: FitnessGoal;
  dietPreference?: DietPreference;
  mealsPerDay?: number;
  snacksPerDay?: number;
  allergies?: string[];
  trainingExperience?: TrainingExperience;
  trainingLocation?: TrainingLocation;
  daysPerWeek?: number;
  timePerWorkoutMin?: number;
  preferredWorkoutTime?: "MORNING" | "AFTERNOON" | "EVENING";
  equipmentAvailable?: string[];
};

type Ctx = {
  data: WizardData;
  setPartial: (patch: Partial<WizardData>) => void;
  reset: () => void;
};

const WizardContext = createContext<Ctx | null>(null);
const KEY = "fitplanpro.wizard";

export function WizardProvider({ children }: { children: React.ReactNode }) {
  const [data, setData] = useState<WizardData>({});

  // hydrate
  useEffect(() => {
    (async () => {
      try {
        const raw = await AsyncStorage.getItem(KEY);
        if (raw) setData(JSON.parse(raw));
      } catch {}
    })();
  }, []);

  // persist
  useEffect(() => {
    AsyncStorage.setItem(KEY, JSON.stringify(data)).catch(() => {});
  }, [data]);

  const value = useMemo<Ctx>(
    () => ({
      data,
      setPartial: (patch) => setData((d) => ({ ...d, ...patch })),
      reset: () => setData({}),
    }),
    [data]
  );

  return <WizardContext.Provider value={value}>{children}</WizardContext.Provider>;
}

export function useWizard() {
  const ctx = useContext(WizardContext);
  if (!ctx) throw new Error("useWizard must be used inside WizardProvider");
  return ctx;
}
