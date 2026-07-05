import React from "react";
import { View, Text, StyleSheet, TouchableOpacity } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";

export default function PracticeLogDetailScreen() {
  const router = useRouter();

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()} activeOpacity={0.7}>
          <Ionicons name="chevron-back" size={24} color={colors.textPrimary} />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>연습일지 상세</Text>
        <View style={{ width: 24 }} />
      </View>

      <View style={styles.body}>
        <Ionicons name="musical-notes-outline" size={40} color={colors.textMuted} />
        <Text style={styles.title}>준비 중입니다</Text>
        <Text style={styles.description}>연습일지 상세 기능은 곧 추가될 예정이에요.</Text>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border,
  },
  headerTitle: {
    fontSize: 16,
    fontWeight: "600",
    color: colors.textPrimary,
  },
  body: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    gap: 12,
    paddingHorizontal: 32,
  },
  title: {
    fontSize: 16,
    fontWeight: "600",
    color: colors.textSecondary,
  },
  description: {
    fontSize: 13,
    color: colors.textMuted,
    textAlign: "center",
  },
});
