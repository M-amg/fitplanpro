// src/types.ts

/** =======================
 *  COMMON ENUMS / TYPES
 *  ======================= */

export type ISODate = string; // e.g. "2025-08-30"
export type ISODateTime = string; // e.g. "2025-08-30T12:34:56Z"

export enum Gender {
  Male = "MALE",
  Female = "FEMALE",
}

export enum FitnessGoal {
  FatLoss = "FAT_LOSS",
  MuscleGain = "MUSCLE_GAIN",
  Recomposition = "RECOMPOSITION",
  Maintenance = "MAINTENANCE",
}

export enum ActivityLevel {
  Sedentary = "SEDENTARY",
  Light = "LIGHT",
  Moderate = "MODERATE",
  Active = "ACTIVE",
  VeryActive = "VERY_ACTIVE",
}

export enum TrainingExperience {
  Beginner = "BEGINNER",
  Intermediate = "INTERMEDIATE",
  Advanced = "ADVANCED",
}

export enum TrainingLocation {
  Gym = "GYM",
  Home = "HOME",
  Outdoor = "OUTDOOR",
}

export enum DietPreference {
  Normal = "NORMAL",
  LowCarb = "LOW_CARB",
  LowFat = "LOW_FAT",
  Keto = "KETO",
  Vegetarian = "VEGETARIAN",
  Vegan = "VEGAN",
}

export type Equipment =
  | "DUMBBELLS"
  | "BARBELL"
  | "MACHINES"
  | "KETTLEBELLS"
  | "BANDS"
  | "BODYWEIGHT";

export interface Pagination {
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

/** API error (standardized) */
export interface ApiError {
  error: string; // e.g. "VALIDATION_ERROR"
  message: string;
  fields?: Array<{ field: string; issue: string }>;
}

/** Optional wrapper if your backend uses a standard envelope */
export interface ApiResponse<T> {
  success: boolean;
  data: T;
  message?: string;
  timestamp?: ISODateTime;
  errors?: Record<string, string>;
}

/** =======================
 *  AUTH
 *  ======================= */

export type User = {
  id: number;
  email: string;
  phone: string;
  languagePreference: LanguagePreference;
  createdAt: string;
  lastLogin: string;
};

export type LoginData = {
  token: string;
  user: User;
  hasProfile: boolean;
};

export interface RegisterRequest {
  email: string;
  password: string;
  phone?: string;
  lang?: string; // "ar" | "en" | "fr"
  tz?: string; // IANA TZ, e.g. "Africa/Casablanca"
}

export interface UserSummary {
  id: number;
  email: string;
  createdAt?: ISODateTime;
}

export interface RegisterResponse {
  token: string;
  user: UserSummary;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: UserSummary;
}

/** =======================
 *  PROFILE
 *  ======================= */

export interface Profile {
  id: number;
  userId: number;
  gender: Gender;
  age: number; // years
  heightCm: number;
  weightKg: number;
  targetWeightKg?: number;
  goal: FitnessGoal;
  activityLevel?: ActivityLevel;

  trainingExperience: TrainingExperience;
  trainingLocation: TrainingLocation;
  daysPerWeek: number; // 3..7
  timePerWorkoutMin?: number;
  preferredWorkoutTime?: "MORNING" | "AFTERNOON" | "EVENING";

  dietPreference: DietPreference;
  mealsPerDay: number;
  snacksPerDay?: number;
  locationCulture?: string; // e.g. "MOROCCO"

  allergies?: string[];
  equipmentAvailable?: Equipment[];
  budget?: "LOW" | "MEDIUM" | "HIGH";

  createdAt?: ISODateTime;
  updatedAt?: ISODateTime;
}

export type UpdateProfileRequest = Omit<
  Profile,
  "id" | "userId" | "createdAt" | "updatedAt"
>;

/** =======================
 *  PLAN (WORKOUT + MEAL)
 *  ======================= */

export interface MacroTargets {
  calories: number; // kcal
  protein: number; // g
  carbs: number; // g
  fat: number; // g
}

export interface ExerciseSet {
  set: number;
  reps: number;
  weightKg?: number;
  rir?: number; // reps in reserve (optional)
  restSec?: number;
}

export interface Exercise {
  id?: number;
  name: string;
  notes?: string;
  sets: ExerciseSet[];
}

export interface WorkoutBlock {
  title: string; // e.g. "Upper Push"
  durationMin?: number;
  exercises: Exercise[];
}

export interface MealItem {
  foodId?: number;
  name: string;
  grams?: number;
  kcal?: number;
  protein?: number;
  carbs?: number;
  fat?: number;
}

export type MealSlot = "BREAKFAST" | "SNACK1" | "LUNCH" | "SNACK2" | "DINNER";

export interface Meal {
  slot: MealSlot;
  items: MealItem[];
  kcal?: number; // pre-summed if server provides it
}

export interface DayPlan {
  day: 1 | 2 | 3 | 4 | 5 | 6 | 7;
  workout?: WorkoutBlock;
  meals: Meal[];
}

export interface Plan {
  planId: string;
  macros: MacroTargets;
  week: { days: DayPlan[] };
  explanations?: {
    workoutRationale?: string;
    nutritionScience?: string;
  };
  createdAt?: ISODateTime;
}

export interface GeneratePlanRequest {
  useCache?: boolean; // default true on server
  force?: boolean; // bypass cache/similar-profile
}

export interface GeneratePlanResponse extends Plan {}

/** =======================
 *  TRACKING
 *  ======================= */

export interface TrackedMeal {
  id?: number;
  date: ISODate; // yyyy-mm-dd
  planId?: string;
  day?: number; // 1..7
  mealSlot: MealSlot;
  photoUrl?: string;
  items: MealItem[];
}

export interface TrackedWorkoutSet {
  exerciseName: string;
  set: number;
  reps: number;
  weightKg?: number;
}

export interface TrackedWorkout {
  id?: number;
  date: ISODate;
  planId?: string;
  day?: number; // 1..7
  completed: boolean;
  sets?: TrackedWorkoutSet[];
}

export interface WaterLog {
  id?: number;
  date: ISODate;
  ml: number;
}

export interface WeightLog {
  id?: number;
  date: ISODate;
  weightKg: number;
}

/** =======================
 *  ANALYTICS
 *  ======================= */

export interface WeightPoint {
  date: ISODate;
  weightKg: number;
}
export interface KcalPoint {
  date: ISODate;
  intake: number;
  target: number;
}

export interface AnalyticsOverview {
  weightTrend: WeightPoint[];
  kcalVsGoal: KcalPoint[];
  workoutAdherence: { completed: number; planned: number };
}

export type LanguagePreference = "ENGLISH" | "SPANISH" | "FRENCH";
