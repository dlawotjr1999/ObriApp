import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { TouchableOpacity } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";

export default function PracticeLogCreateScreen() {
  const router = useRouter();

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()} hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}>
          <Ionicons name="arrow-back" size={22} color={colors.primary} />
        </TouchableOpacity>
        <Text style={styles.title}>연습일지 작성</Text>
        <View style={{ width: 22 }} />
      </View>

      <View style={styles.body}>
        <Ionicons name="create-outline" size={48} color={colors.textMuted} />
        <Text style={styles.message}>준비 중입니다.</Text>
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border,
  },
  title: {
    fontSize: 16,
    fontWeight: "600",
    color: colors.primary,
  },
  body: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    gap: 12,
  },
  message: {
    fontSize: 14,
    color: colors.textMuted,
  },
});
