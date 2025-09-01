// lib/profilePayload.ts
import { WizardData } from "./wizard";
import { Gender, FitnessGoal, DietPreference, TrainingExperience, TrainingLocation } from "./types";

export type CreateProfileBody = {
  gender: Gender;                    // "MALE" | "FEMALE" | ...
  age: number;
  height: number;                    // from heightCm
  currentWeight: number;             // from weightKg
  targetWeight: number;              // from targetWeightKg
  goalType: FitnessGoal;             // rename from goal
  trainingExperience: TrainingExperience;
  trainingLocation: TrainingLocation;
  daysPerWeek: number;
  dietPreference: DietPreference;
  mealsPerDay: number;
  snacksPerDay: number;
  locationCulture: string;
  medicalConditions: string;
  foodAllergies: string;             // comma separated
  timePerWorkout: number;            // from timePerWorkoutMin
  equipmentAvailable: string;        // comma separated
  budgetConstraints: "LOW" | "MEDIUM" | "HIGH";
  preferredWorkoutTime: "MORNING" | "AFTERNOON" | "EVENING";
};

export function toCreateProfileBody(d: WizardData): CreateProfileBody {
  return {
    gender: d.gender!,
    age: d.age!,
    height: d.heightCm!,
    currentWeight: d.weightKg!,
    targetWeight: d.targetWeightKg ?? d.weightKg!, // fallback to current
    goalType: d.goal!,
    trainingExperience: d.trainingExperience!,
    trainingLocation: d.trainingLocation!,
    daysPerWeek: d.daysPerWeek!,
    dietPreference: d.dietPreference!,
    mealsPerDay: d.mealsPerDay!,
    snacksPerDay: d.snacksPerDay ?? 0,
    locationCulture: d.locationCulture ?? "",
    medicalConditions: d.medicalConditions ?? "",
    foodAllergies: (d.allergies ?? []).join(", "),
    timePerWorkout: d.timePerWorkoutMin ?? 60,
    equipmentAvailable: (d.equipmentAvailable ?? []).join(", "),
    budgetConstraints: d.budgetConstraints ?? "MEDIUM",
    preferredWorkoutTime: (d.preferredWorkoutTime ?? "MORNING"),
  };
}
