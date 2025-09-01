import * as FileSystem from "expo-file-system";
import * as Sharing from "expo-sharing";
import React from "react";
import {
    ActivityIndicator,
    Alert,
    Dimensions,
    RefreshControl,
    ScrollView,
    StyleSheet,
    Text,
    TouchableOpacity,
    View
} from "react-native";
import { BarChart, LineChart } from "react-native-chart-kit";
import { api } from "../../lib/api";
import type { AnalyticsOverview } from "../../lib/types";

const screenW = Dimensions.get("window").width;
const chartW = screenW - 24;
const chartH = 200;

type Range = "7d" | "30d" | "90d";

export default function ProgressDetails() {
  const [range, setRange] = React.useState<Range>("30d");
  const [loading, setLoading] = React.useState(true);
  const [refreshing, setRefreshing] = React.useState(false);
  const [error, setError] = React.useState<string | null>(null);
  const [data, setData] = React.useState<AnalyticsOverview | null>(null);

  const load = async (r: Range = range) => {
    try {
      setError(null);
      setLoading(true);
      const res = await api.get<AnalyticsOverview>(`/analytics/overview?range=${r}`);
      setData(res.data);
    } catch (e: any) {
      setError(e?.response?.data?.message ?? "Failed to load analytics");
    } finally {
      setLoading(false);
      setRefreshing(false);
    }
  };

  React.useEffect(() => { load("30d"); }, []);
  const onRefresh = () => { setRefreshing(true); load(range); };

  const weightLabels = data?.weightTrend?.map(d => shortDate(d.date)) ?? [];
  const weightValues = data?.weightTrend?.map(d => d.weightKg) ?? [];

  const kcalLabels  = data?.kcalVsGoal?.map(d => shortDate(d.date)) ?? [];
  const kcalActual  = data?.kcalVsGoal?.map(d => d.intake) ?? [];
  const kcalTarget  = data?.kcalVsGoal?.map(d => d.target) ?? [];

  const adherencePct = data?.workoutAdherence?.planned
    ? Math.round((data!.workoutAdherence.completed / data!.workoutAdherence.planned) * 100)
    : 0;

  const chartConfig = {
    decimalPlaces: 0,
    color: (opacity = 1) => `rgba(17,24,39,${opacity})`,
    labelColor: (opacity = 1) => `rgba(107,114,128,${opacity})`,
    propsForDots: { r: "3" },
    propsForBackgroundLines: { stroke: "#f3f4f6" },
    backgroundGradientFrom: "#fff",
    backgroundGradientTo: "#fff",
  } as const;

  const exportCsv = async () => {
    try {
      if (!data) return;
      const csv = buildCsv(data);
      const fileUri = FileSystem.cacheDirectory + `progress_${range}.csv`;
      await FileSystem.writeAsStringAsync(fileUri, csv, { encoding: FileSystem.EncodingType.UTF8 });

      const canShare = await Sharing.isAvailableAsync();
      if (canShare) {
        await Sharing.shareAsync(fileUri, { UTI: "public.comma-separated-values-text", mimeType: "text/csv" });
      } else {
        Alert.alert("Exported", `Saved CSV to: ${fileUri}`);
      }
    } catch (e: any) {
      Alert.alert("Error", e?.message ?? "Failed to export CSV");
    }
  };

  const changeRange = (r: Range) => {
    setRange(r);
    load(r);
  };

  if (loading) return <View style={s.center}><ActivityIndicator /></View>;
  if (error)   return <View style={s.center}><Text style={s.err}>{error}</Text></View>;
  if (!data)   return <View style={s.center}><Text>No analytics yet.</Text></View>;

  return (
    <ScrollView
      style={{ flex: 1 }}
      contentContainerStyle={{ paddingBottom: 20 }}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
    >
      {/* Range toggle + export */}
      <View style={[s.row, { paddingHorizontal: 12, marginTop: 12 }]}>
        <View style={s.rangeRow}>
          {(["7d","30d","90d"] as Range[]).map(r => {
            const sel = r === range;
            return (
              <TouchableOpacity key={r} style={[s.pill, sel && s.pillA]} onPress={() => changeRange(r)}>
                <Text style={[s.pillT, sel && s.pillTA]}>{r}</Text>
              </TouchableOpacity>
            );
          })}
        </View>

        <TouchableOpacity onPress={exportCsv} style={s.outlineBtn}>
          <Text style={s.outlineBtnT}>Export CSV</Text>
        </TouchableOpacity>
      </View>

      {/* Charts */}
      <View style={s.card}>
        <Text style={s.h}>Weight Trend (kg)</Text>
        <LineChart
          data={{ labels: weightLabels, datasets: [{ data: weightValues }] }}
          width={chartW}
          height={chartH}
          chartConfig={chartConfig}
          bezier
          style={s.chart}
          yAxisLabel=""
          yAxisSuffix=""
          formatYLabel={(v) => `${Math.round(Number(v))}`}
        />
      </View>

      <View style={s.card}>
        <Text style={s.h}>Calories vs Goal</Text>
        <BarChart
          data={{ labels: kcalLabels, datasets: [{ data: kcalActual }] }}
          width={chartW}
          height={chartH}
          chartConfig={chartConfig}
          style={s.chart}
          fromZero
          yAxisLabel=""
          yAxisSuffix=""
        />
        <View style={{ marginTop: 8 }}>
          <LineChart
            data={{ labels: kcalLabels, datasets: [{ data: kcalTarget }] }}
            width={chartW}
            height={Math.round(chartH * 0.8)}
            chartConfig={chartConfig}
            style={s.chart}
            yAxisLabel=""
            yAxisSuffix=""
          />
        </View>
      </View>

      {/* KPI */}
      <View style={s.cardRow}>
        <View style={s.kpi}>
          <Text style={s.kpiLabel}>Workouts Completed</Text>
          <Text style={s.kpiValue}>
            {data.workoutAdherence.completed}/{data.workoutAdherence.planned}
          </Text>
        </View>
        <View style={s.kpi}>
          <Text style={s.kpiLabel}>Adherence</Text>
          <Text style={s.kpiValue}>{adherencePct}%</Text>
        </View>
      </View>

      {/* Tables */}
      <View style={s.card}>
        <Text style={s.h}>Table — Weight</Text>
        <Table
          headers={["Date", "Weight (kg)"]}
          rows={data.weightTrend.map(w => [shortDate(w.date), String(w.weightKg)])}
        />
      </View>

      <View style={s.card}>
        <Text style={s.h}>Table — Calories</Text>
        <Table
          headers={["Date", "Intake", "Target"]}
          rows={data.kcalVsGoal.map(k => [shortDate(k.date), String(k.intake), String(k.target)])}
        />
      </View>
    </ScrollView>
  );
}

function buildCsv(d: AnalyticsOverview): string {
  const lines: string[] = [];
  lines.push("Section,Date,Metric,Value");
  // weight
  for (const w of d.weightTrend) {
    lines.push(`Weight,${w.date},weightKg,${cleanNumber(w.weightKg)}`);
  }
  // kcal
  for (const k of d.kcalVsGoal) {
    lines.push(`Calories,${k.date},intake,${cleanNumber(k.intake)}`);
    lines.push(`Calories,${k.date},target,${cleanNumber(k.target)}`);
  }
  // adherence
  lines.push(`Adherence,,completed,${cleanNumber(d.workoutAdherence.completed)}`);
  lines.push(`Adherence,,planned,${cleanNumber(d.workoutAdherence.planned)}`);
  return lines.join("\n");
}

const cleanNumber = (n: number) => Number.isFinite(n) ? n : 0;

const shortDate = (iso: string) => {
  const d = new Date(iso);
  const m = (d.getMonth() + 1).toString().padStart(2, "0");
  const day = d.getDate().toString().padStart(2, "0");
  return `${m}/${day}`;
};

/** Tiny table **/
function Table({ headers, rows }: { headers: string[]; rows: string[][] }) {
  return (
    <View style={{ borderWidth: 1, borderColor: "#eee", borderRadius: 12 }}>
      <View style={[t.row, { backgroundColor: "#f9fafb" }]}>
        {headers.map((h, i) => (
          <Text key={i} style={[t.cell, t.head]}>{h}</Text>
        ))}
      </View>
      {rows.map((r, i) => (
        <View key={i} style={t.row}>
          {r.map((c, j) => (
            <Text key={j} style={t.cell}>{c}</Text>
          ))}
        </View>
      ))}
    </View>
  );
}

const t = StyleSheet.create({
  row: { flexDirection: "row", borderTopWidth: 1, borderColor: "#f3f4f6" },
  cell: { flex: 1, paddingVertical: 10, paddingHorizontal: 8, color: "#111827" },
  head: { fontWeight: "700", color: "#374151" },
});

const s = StyleSheet.create({
  center: { flex: 1, alignItems: "center", justifyContent: "center", padding: 20 },
  err: { color: "#dc2626" },
  row: { flexDirection: "row", alignItems: "center", justifyContent: "space-between" },
  rangeRow: { flexDirection: "row", gap: 8 },
  pill: { paddingVertical: 8, paddingHorizontal: 12, borderWidth: 1, borderColor: "#d1d5db", borderRadius: 9999 },
  pillA: { backgroundColor: "#111827", borderColor: "#111827" },
  pillT: { color: "#111827", fontWeight: "600" },
  pillTA: { color: "white" },
  outlineBtn: { borderWidth: 1, borderColor: "#111827", paddingVertical: 8, paddingHorizontal: 12, borderRadius: 10 },
  outlineBtnT: { color: "#111827", fontWeight: "700" },

  card: { backgroundColor: "white", margin: 12, padding: 16, borderRadius: 12, borderWidth: 1, borderColor: "#eee" },
  cardRow: { flexDirection: "row", gap: 12, marginHorizontal: 12, marginTop: 4 },
  kpi: { flex: 1, backgroundColor: "white", padding: 16, borderRadius: 12, borderWidth: 1, borderColor: "#eee" },
  kpiLabel: { color: "#6b7280", marginBottom: 6 },
  kpiValue: { fontSize: 22, fontWeight: "800", color: "#111827" },
  h: { fontSize: 18, fontWeight: "700", marginBottom: 6 },
  chart: { marginVertical: 8, borderRadius: 12 },
});
