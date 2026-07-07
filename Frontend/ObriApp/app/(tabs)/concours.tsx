import React from "react";
import { StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { colors } from "@/constants/theme";
import AppHeader from "@/components/common/AppHeader";
import EmptyState from "@/components/common/EmptyState";

export default function ConcoursScreen() {
  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <AppHeader />
      <EmptyState
        icon="trophy-outline"
        title="콩쿠르 확인"
        description="준비 중입니다."
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
});
