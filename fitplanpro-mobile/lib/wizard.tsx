// lib/wizard.tsx
import React, { createContext, useContext, useEffect, useMemo, useState } from "react";
import AsyncStorage from "@react-native-async-storage/async-storage";
import {
  DietPreference,
  FitnessGoal,
  Gender,
  TrainingExperience,
  TrainingLocation,
} from "./types";

/** All data we collect during onboarding */
export type WizardData = {
  // Personal info
  gender?: Gender;
  age?: number;
  heightCm?: number;
  weightKg?: number;
  targetWeightKg?: number;

  // Goal & diet
  goal?: FitnessGoal;
  dietPreference?: DietPreference;
  mealsPerDay?: number;
  snacksPerDay?: number;
  allergies?: string[];

  // Training
  trainingExperience?: TrainingExperience;
  trainingLocation?: TrainingLocation;
  daysPerWeek?: number;
  timePerWorkoutMin?: number;
  preferredWorkoutTime?: "MORNING" | "AFTERNOON" | "EVENING";
  equipmentAvailable?: string[];

  // ✅ Extra fields required by POST /profiles
  locationCulture?: string;
  medicalConditions?: string;
  budgetConstraints?: "LOW" | "MEDIUM" | "HIGH";
};

type Ctx = {
  /** Current accumulated onboarding data */
  data: WizardData;
  /** Merge a partial patch into the wizard state */
  setPartial: (patch: Partial<WizardData>) => void;
  /** Clear all wizard data (e.g., after successful submit) */
  reset: () => void;
};

const WizardContext = createContext<Ctx | null>(null);
const STORAGE_KEY = "fitplanpro.wizard";

/** Provides onboarding state + persistence to its children */
export function WizardProvider({ children }: { children: React.ReactNode }) {
  const [data, setData] = useState<WizardData>({});

  // Hydrate from storage once
  useEffect(() => {
    (async () => {
      try {
        const raw = await AsyncStorage.getItem(STORAGE_KEY);
        if (raw) setData(JSON.parse(raw));
      } catch {
        // ignore hydration errors
      }
    })();
  }, []);

  // Persist on every change (fire-and-forget)
  useEffect(() => {
    AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(data)).catch(() => {});
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

/** Hook to read/update onboarding data inside the provider */
export function useWizard() {
  const ctx = useContext(WizardContext);
  if (!ctx) throw new Error("useWizard must be used inside WizardProvider");
  return ctx;
}
