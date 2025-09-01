import AsyncStorage from "@react-native-async-storage/async-storage";
import type { LanguagePreference } from "./types";

const LANG_KEY = "fitplanpro.lang";
const ONBOARD_KEY = "fitplanpro.onboarded";

export async function setLanguage(lang: LanguagePreference) {
  await AsyncStorage.setItem(LANG_KEY, lang);
}
export async function getLanguage(): Promise<LanguagePreference | null> {
  const v = await AsyncStorage.getItem(LANG_KEY);
  return (v as LanguagePreference) ?? null;
}

export async function setOnboarded() {
  await AsyncStorage.setItem(ONBOARD_KEY, "1");
}
export async function getOnboarded(): Promise<boolean> {
  return (await AsyncStorage.getItem(ONBOARD_KEY)) === "1";
}
