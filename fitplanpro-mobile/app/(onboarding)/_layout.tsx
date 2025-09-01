// app/(onboarding)/_layout.tsx
import { Stack } from "expo-router";
import React from "react";
import { WizardProvider } from "../../lib/wizard"; // <-- your file

export default function OnboardingLayout() {
  return (
    <WizardProvider>
      <Stack screenOptions={{ headerShown: false }}>
        <Stack.Screen name="personal-info" />
        <Stack.Screen name="goal" />
        <Stack.Screen name="diet" />
        <Stack.Screen name="experience" />
        <Stack.Screen name="schedule" />
        <Stack.Screen name="summary" />
      </Stack>
    </WizardProvider>
  );
}
