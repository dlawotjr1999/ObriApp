import React from "react";
import { StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { colors } from "@/constants/theme";
import EmptyState from "@/components/common/EmptyState";

export default function MyPageScreen() {
  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <EmptyState
        icon="person-outline"
        title="마이 페이지"
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
