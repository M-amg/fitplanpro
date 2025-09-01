// app/(tabs)/progress.tsx
import AsyncStorage from "@react-native-async-storage/async-storage";
import React from "react";
import {
    ActivityIndicator,
    Dimensions,
    RefreshControl,
    ScrollView,
    StyleSheet,
    Text,
    TouchableOpacity,
    View,
} from "react-native";
import { BarChart, LineChart } from "react-native-chart-kit";
import { api } from "../../lib/api";
import type { AnalyticsOverview } from "../../lib/types";

type Range = "7d" | "30d" | "90d";

const screenW = Dimensions.get("window").width;
const chartW = screenW - 24;
const chartH = 220;

// storage
const STORAGE_KEY = "fitplanpro.analytics.cache.v1"; // bump v1 -> v2 if schema changes
const TTL_MS = 10 * 60 * 1000; // 10 minutes

type CachedEntry = { updatedAt: number; data: AnalyticsOverview };
type CacheShape = Partial<Record<Range, CachedEntry>>;

export default function ProgressScreen() {
  const [range, setRange] = React.useState<Range>("30d");

  // in-memory view (what we render)
  const [data, setData] = React.useState<AnalyticsOverview | null>(null);

  // local cache (in-memory mirror of AsyncStorage)
  const [cache, setCache] = React.useState<CacheShape>({});

  // ui flags
  const [bootLoading, setBootLoading] = React.useState(true);
  const [refreshing, setRefreshing] = React.useState(false);
  const [bgRefreshing, setBgRefreshing] = React.useState(false);
  const [error, setError] = React.useState<string | null>(null);

  // ---- Storage helpers ----
  const loadCacheFromStorage = React.useCallback(async (): Promise<CacheShape> => {
    try {
      const raw = await AsyncStorage.getItem(STORAGE_KEY);
      if (!raw) return {};
      const parsed = JSON.parse(raw) as CacheShape;
      return parsed ?? {};
    } catch {
      return {};
    }
  }, []);

  const saveCacheToStorage = React.useCallback(async (next: CacheShape) => {
    try {
      await AsyncStorage.setItem(STORAGE_KEY, JSON.stringify(next));
    } catch {
      // ignore write errors (render still uses in-memory cache)
    }
  }, []);

  // ---- Data fetch with SWR semantics ----
  const fetchRange = React.useCallback(
    async (r: Range, { silent }: { silent: boolean }) => {
      try {
        if (silent) setBgRefreshing(true);
        else setRefreshing(true);
        setError(null);

        const res = await api.get<AnalyticsOverview>(`/analytics/overview?range=${r}`);

        // update both caches
        const next: CacheShape = {
          ...cache,
          [r]: { data: res.data, updatedAt: Date.now() },
        };
        setCache(next);
        saveCacheToStorage(next);

        // update view if still on same range
        if (r === range) setData(res.data);
      } catch (e: any) {
        const msg = e?.response?.data?.message ?? "Failed to load analytics";
        if (!cache[r]) setError(msg); // only surface if no fallback
      } finally {
        setBootLoading(false);
        setRefreshing(false);
        setBgRefreshing(false);
      }
    },
    [range, cache, saveCacheToStorage]
  );

  // ---- Boot: hydrate cache, show cached instantly, then refresh if stale ----
  React.useEffect(() => {
    (async () => {
      const stored = await loadCacheFromStorage();
      setCache(stored);

      // prefer 30d on first mount
      const initialRange: Range = "30d";
      setRange(initialRange);

      const entry = stored[initialRange];
      if (entry?.data) setData(entry.data);

      const isStale = !entry || Date.now() - entry.updatedAt > TTL_MS;
      await fetchRange(initialRange, { silent: !!entry && !isStale ? true : false });
    })();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // pull-to-refresh for current range
  const onRefresh = () => fetchRange(range, { silent: false });

  // user toggles range
  const changeRange = (r: Range) => {
    setRange(r);
    const entry = cache[r];

    // show cache immediately if present
    if (entry?.data) setData(entry.data);
    else {
      setData(null);
      setBootLoading(true);
    }

    // decide silent vs full refresh based on staleness
    const isStale = !entry || Date.now() - entry.updatedAt > TTL_MS;
    fetchRange(r, { silent: !!entry && !isStale });
  };

  // choose what to render
  const current = data ?? cache[range]?.data ?? null;

  if (bootLoading && !current) {
    return (
      <View style={s.center}>
        <ActivityIndicator />
      </View>
    );
  }

  if (error && !current) {
    return (
      <View style={s.center}>
        <Text style={s.err}>{error}</Text>
        <TouchableOpacity style={s.btn} onPress={() => fetchRange(range, { silent: false })}>
          <Text style={s.btnT}>Retry</Text>
        </TouchableOpacity>
      </View>
    );
  }

  if (!current) {
    return (
      <View style={s.center}>
        <Text>No analytics yet.</Text>
      </View>
    );
  }

  // Prepare series from current view data
  const weightLabels = current.weightTrend.map((d) => shortDate(d.date));
  const weightValues = current.weightTrend.map((d) => d.weightKg);

  const kcalLabels = current.kcalVsGoal.map((d) => shortDate(d.date));
  const kcalActual = current.kcalVsGoal.map((d) => d.intake);
  const kcalTarget = current.kcalVsGoal.map((d) => d.target);

  const adherencePct = current.workoutAdherence.planned
    ? Math.round((current.workoutAdherence.completed / current.workoutAdherence.planned) * 100)
    : 0;

  // Chart config
  const chartConfig = {
    decimalPlaces: 0,
    color: (opacity = 1) => `rgba(17,24,39,${opacity})`,
    labelColor: (opacity = 1) => `rgba(107,114,128,${opacity})`,
    propsForDots: { r: "3" },
    propsForBackgroundLines: { stroke: "#f3f4f6" },
    backgroundGradientFrom: "#fff",
    backgroundGradientTo: "#fff",
  } as const;

  return (
    <ScrollView
      style={{ flex: 1 }}
      contentContainerStyle={{ paddingBottom: 16 }}
      refreshControl={<RefreshControl refreshing={refreshing} onRefresh={onRefresh} />}
    >
      {/* Header row with range toggle */}
      <View style={s.headerRow}>
        <Text style={s.title}>Progress</Text>
        <View style={s.rangeRow}>
          {(["7d", "30d", "90d"] as Range[]).map((r) => {
            const sel = r === range;
            const stale = cache[r] ? Date.now() - (cache[r]!.updatedAt) > TTL_MS : true;
            return (
              <TouchableOpacity key={r} style={[s.pill, sel && s.pillA]} onPress={() => changeRange(r)}>
                <Text style={[s.pillT, sel && s.pillTA]}>
                  {r}{/* show • if stale */}
                  {sel && stale ? " •" : ""}
                </Text>
              </TouchableOpacity>
            );
          })}
        </View>
      </View>

      {/* Weight Trend */}
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
        {bgRefreshing && <Text style={s.loadingText}>Refreshing…</Text>}
        <Timestamp entry={cache[range]} />
      </View>

      {/* Calories vs Goal */}
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
          showValuesOnTopOfBars={false}
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
        {bgRefreshing && <Text style={s.loadingText}>Refreshing…</Text>}
        <Timestamp entry={cache[range]} />
      </View>

      {/* Adherence KPIs */}
      <View style={s.cardRow}>
        <View style={s.kpi}>
          <Text style={s.kpiLabel}>Workouts Completed</Text>
          <Text style={s.kpiValue}>
            {current.workoutAdherence.completed}/{current.workoutAdherence.planned}
          </Text>
        </View>
        <View style={s.kpi}>
          <Text style={s.kpiLabel}>Adherence</Text>
          <Text style={s.kpiValue}>{adherencePct}%</Text>
        </View>
      </View>
    </ScrollView>
  );
}

function Timestamp({ entry }: { entry?: CachedEntry }) {
  if (!entry) return null;
  const d = new Date(entry.updatedAt);
  const hh = String(d.getHours()).padStart(2, "0");
  const mm = String(d.getMinutes()).padStart(2, "0");
  return <Text style={s.stamp}>Last updated {hh}:{mm}</Text>;
}

const shortDate = (iso: string) => {
  const d = new Date(iso);
  const m = (d.getMonth() + 1).toString().padStart(2, "0");
  const day = d.getDate().toString().padStart(2, "0");
  return `${m}/${day}`;
};

const s = StyleSheet.create({
  center: { flex: 1, alignItems: "center", justifyContent: "center", padding: 20 },
  err: { color: "#dc2626", marginBottom: 10 },
  btn: { backgroundColor: "#111827", paddingVertical: 10, paddingHorizontal: 14, borderRadius: 10 },
  btnT: { color: "white", fontWeight: "700" },

  headerRow: { marginTop: 12, marginHorizontal: 12, flexDirection: "row", justifyContent: "space-between", alignItems: "center" },
  title: { fontSize: 22, fontWeight: "800", color: "#111827" },
  rangeRow: { flexDirection: "row", gap: 8 },
  pill: { paddingVertical: 6, paddingHorizontal: 10, borderWidth: 1, borderColor: "#d1d5db", borderRadius: 9999 },
  pillA: { backgroundColor: "#111827", borderColor: "#111827" },
  pillT: { color: "#111827", fontWeight: "700" },
  pillTA: { color: "white" },

  card: { backgroundColor: "white", margin: 12, padding: 16, borderRadius: 12, borderWidth: 1, borderColor: "#eee" },
  h: { fontSize: 18, fontWeight: "700", marginBottom: 6 },
  chart: { marginVertical: 8, borderRadius: 12 },
  loadingText: { textAlign: "right", color: "#6b7280", fontSize: 12 },
  stamp: { textAlign: "right", color: "#9ca3af", fontSize: 11, marginTop: 4 },

  cardRow: { flexDirection: "row", gap: 12, marginHorizontal: 12, marginTop: 4 },
  kpi: { flex: 1, backgroundColor: "white", padding: 16, borderRadius: 12, borderWidth: 1, borderColor: "#eee" },
  kpiLabel: { color: "#6b7280", marginBottom: 6 },
  kpiValue: { fontSize: 22, fontWeight: "800", color: "#111827" },
});
