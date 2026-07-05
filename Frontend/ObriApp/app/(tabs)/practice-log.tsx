import React from "react";
import { StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { colors } from "@/constants/theme";
import AppHeader from "@/components/common/AppHeader";
import EmptyState from "@/components/common/EmptyState";

export default function PracticeLogScreen() {
  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <AppHeader />
      <EmptyState
        icon="musical-notes-outline"
        title="연습 일지"
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
