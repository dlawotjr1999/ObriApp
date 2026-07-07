import React, { useState } from "react";
import { View, FlatList, StyleSheet, Text } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { colors } from "@/constants/theme";
import { MOCK_CONTESTS } from "@/mocks/contests";
import { ContestDetail } from "@/types/contest";
import AppHeader from "@/components/common/AppHeader";
import EmptyState from "@/components/common/EmptyState";
import ContestCard from "@/components/contest/ContestCard";
import ContestDetailModal from "@/components/contest/ContestDetailModal";

export default function ConcoursScreen() {
  const [selected, setSelected] = useState<ContestDetail | null>(null);

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <AppHeader />

      <View style={styles.resultRow}>
        <Text style={styles.resultText}>총 {MOCK_CONTESTS.length}개</Text>
      </View>

      <FlatList
        data={MOCK_CONTESTS}
        keyExtractor={(item) => String(item.id)}
        renderItem={({ item }) => (
          <ContestCard contest={item} onPress={() => setSelected(item)} />
        )}
        contentContainerStyle={styles.listContent}
        ItemSeparatorComponent={() => <View style={styles.separator} />}
        ListEmptyComponent={
          <EmptyState
            icon="trophy-outline"
            title="등록된 콩쿠르 정보가 없어요"
            description="새로운 콩쿠르가 업데이트되면 알려드릴게요."
          />
        }
      />

      <ContestDetailModal contest={selected} onClose={() => setSelected(null)} />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: colors.background },
  resultRow: {
    paddingHorizontal: 16,
    paddingTop: 12,
    paddingBottom: 4,
  },
  resultText: {
    fontSize: 12,
    color: colors.textMuted,
  },
  listContent: { flexGrow: 1, padding: 16 },
  separator: { height: 12 },
});
