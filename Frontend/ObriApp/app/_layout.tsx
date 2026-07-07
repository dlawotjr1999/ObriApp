import { useEffect, useState } from "react";
import { Stack, Redirect } from "expo-router";
import { StatusBar } from "expo-status-bar";

import LoadingScreen from "@/components/common/LoadingScreen";

export default function RootLayout() {
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    // TODO: Firebase 인증 상태 확인 로직으로 교체
    const timer = setTimeout(() => setIsLoading(false), 2000);
    return () => clearTimeout(timer);
  }, []);

  if (isLoading) {
    return (
      <>
        <LoadingScreen />
        <StatusBar style="dark" />
      </>
    );
  }

  // TODO: Firebase 인증 상태에 따라 (auth) 또는 (tabs)로 분기
  // 현재는 테스트용으로 탭 화면으로 바로 이동
  return (
    <>
      <Stack screenOptions={{ headerShown: false }}>
        <Stack.Screen name="(auth)" />
        <Stack.Screen name="(tabs)" />
      </Stack>
      <Redirect href="/(tabs)/obri" />
      <StatusBar style="dark" />
    </>
  );
}
