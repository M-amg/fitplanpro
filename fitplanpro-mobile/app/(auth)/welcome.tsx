import { router } from "expo-router";
import React from "react";
import { Image, StyleSheet, Text, TouchableOpacity, View } from "react-native";

export default function Welcome() {
  return (
    <View style={s.c}>
      <View style={{ alignItems: "center", marginTop: 24 }}>
        <Text style={s.title}>Welcome to FitPlan Pro</Text>
        <Text style={s.sub}>Your AI-powered fitness companion</Text>
      </View>

      <View style={s.hero}>
        <Image
          source={require("../../assets/images/react-logo.png")} // replace with your welcome image
          style={{ width: "100%", height: 180, borderRadius: 16 }}
          resizeMode="cover"
        />
      </View>

      <View style={{ gap: 8 }}>
        <View style={s.bullet}><Text>• Personalized workout plans tailored to your goals</Text></View>
        <View style={s.bullet}><Text>• AI-powered form correction and real-time feedback</Text></View>
        <View style={s.bullet}><Text>• Track progress and achieve your fitness milestones</Text></View>
      </View>

      <View style={{ gap: 10, marginTop: "auto" }}>
        <TouchableOpacity style={s.btn} onPress={() => router.push("/(auth)/register")}>
          <Text style={s.btnT}>Sign Up</Text>
        </TouchableOpacity>
        <TouchableOpacity style={s.btnGhost} onPress={() => router.push("/(auth)/login")}>
          <Text style={s.btnGhostT}>Login</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const s = StyleSheet.create({
  c: { flex: 1, padding: 20, gap: 16 },
  title: { fontSize: 20, fontWeight: "800", textAlign: "center" },
  sub: { textAlign: "center", color: "#6b7280", marginTop: 6 },
  hero: { marginTop: 20, backgroundColor: "white", borderRadius: 16, padding: 10, borderWidth: 1, borderColor: "#eee" },
  bullet: { backgroundColor: "white", borderWidth: 1, borderColor: "#eee", borderRadius: 12, padding: 12 },
  btn: { backgroundColor: "#111827", padding: 14, borderRadius: 12, alignItems: "center" },
  btnT: { color: "white", fontWeight: "700" },
  btnGhost: { borderWidth: 1, borderColor: "#111827", padding: 14, borderRadius: 12, alignItems: "center" },
  btnGhostT: { color: "#111827", fontWeight: "700" },
});
