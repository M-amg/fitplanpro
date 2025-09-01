import { Redirect } from "expo-router";
import React from "react";
import { useAuth } from "../lib/auth";
import { getOnboarded } from "../lib/lang";

export default function Index() {
  const { token, loading, hasProfile } = useAuth();
  const [onboarded, setOnboarded] = React.useState<boolean | null>(null);

  React.useEffect(() => {
    (async () => setOnboarded(await getOnboarded()))();
  }, []);

  if (loading || onboarded === null) return null;

  if (!token) {
    // If not onboarded yet, go to language first; else go to welcome/login
    return <Redirect href={onboarded ? "/welcome" : "/language"} />;
  }
  return <Redirect href={hasProfile ? "/dashboard" : "/personal-info"} />;
}
