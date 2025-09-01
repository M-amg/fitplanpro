import { Stack } from "expo-router";
import { StatusBar } from "expo-status-bar";
import React from "react";
import { ActivityIndicator, View } from "react-native";
import "react-native-gesture-handler";
import "react-native-reanimated";

 // must be first
import { AuthProvider, useAuth } from "../lib/auth";

function Gate() {
  const { loading } = useAuth();

  // Keep a minimal splash while we hydrate token/user from AsyncStorage
  if (loading) {
    return (
      <View style={{ flex: 1, alignItems: "center", justifyContent: "center" }}>
        <ActivityIndicator />
      </View>
    );
  }

  // This Stack is the root navigator.
  // The (auth) and (tabs) folders each have their own _layout.tsx that defines their screens.
  return (
    <Stack screenOptions={{ headerShown: false }}>
      {/* (auth) group: login/register */}
      <Stack.Screen name="(auth)" />
      {/* (tabs) group: dashboard/plans/track/progress/profile */}
      <Stack.Screen name="(tabs)" />
      {/* any other top-level routes (e.g., /progress/details) will auto-register too */}
      <Stack.Screen name="(onboarding)" /> 
    </Stack>
  );
}

export default function RootLayout() {
  return (
    <AuthProvider>
      <StatusBar style="auto" />
      <Gate />
    </AuthProvider>
  );
}
