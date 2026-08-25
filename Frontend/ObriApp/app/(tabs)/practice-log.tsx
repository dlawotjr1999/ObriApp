import React, { useMemo, useState } from "react";
import { View, FlatList, StyleSheet, TouchableOpacity, Text } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { MOCK_PRACTICE_LOGS } from "@/mocks/practiceLogs";
import { PracticeLog } from "@/types/practiceLog";
import AppHeader from "@/components/common/AppHeader";
import EmptyState from "@/components/common/EmptyState";
import PracticeLogCard from "@/components/practiceLog/PracticeLogCard";
import PracticeLogDetailModal from "@/components/practiceLog/PracticeLogDetailModal";

export default function PracticeLogScreen() {
  const router = useRouter();
  const [selected, setSelected] = useState<PracticeLog | null>(null);

  // 연습 날짜 기준 최신순. 목데이터 배열 순서에 의존하지 않도록 명시적으로 정렬한다
  const sortedLogs = useMemo(
    () =>
      [...MOCK_PRACTICE_LOGS].sort(
        (a, b) => new Date(b.logDate).getTime() - new Date(a.logDate).getTime()
      ),
    []
  );

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <AppHeader />

      <View style={styles.pageHeader}>
        <Text style={styles.pageTitle}>연습일지</Text>
        <TouchableOpacity
          style={styles.writeButton}
          onPress={() => router.push("/practice-log/create")}
          activeOpacity={0.8}
        >
          <Ionicons name="add" size={16} color={colors.background} />
          <Text style={styles.writeButtonText}>작성하기</Text>
        </TouchableOpacity>
      </View>

      <FlatList
        data={sortedLogs}
        keyExtractor={(item) => String(item.id)}
        renderItem={({ item }) => (
          <PracticeLogCard log={item} onPress={() => setSelected(item)} />
        )}
        contentContainerStyle={styles.listContent}
        ItemSeparatorComponent={() => <View style={styles.separator} />}
        ListEmptyComponent={
          <EmptyState
            icon="musical-notes-outline"
            title="아직 연습 기록이 없어요"
            description="오늘의 연습을 기록해 보세요."
          />
        }
      />

      <PracticeLogDetailModal log={selected} onClose={() => setSelected(null)} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  pageHeader: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 16,
    paddingTop: 16,
    paddingBottom: 12,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border,
  },
  pageTitle: {
    fontSize: 17,
    fontWeight: "700",
    color: colors.primary,
  },
  writeButton: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
    backgroundColor: colors.primary,
    paddingHorizontal: 14,
    paddingVertical: 8,
    borderRadius: 20,
  },
  writeButtonText: {
    fontSize: 13,
    fontWeight: "600",
    color: colors.background,
  },
  listContent: { flexGrow: 1, padding: 16 },
  separator: { height: 12 },
});
