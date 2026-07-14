import React, { useMemo, useState } from "react";
import { View, FlatList, StyleSheet, Text } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { colors } from "@/constants/theme";
import { MOCK_CONTESTS } from "@/mocks/contests";
import { ContestDetail } from "@/types/contest";
import { ContestFilter, DEFAULT_CONTEST_FILTER } from "@/types/contestFilter";
import AppHeader from "@/components/common/AppHeader";
import EmptyState from "@/components/common/EmptyState";
import ContestCard from "@/components/contest/ContestCard";
import ContestDetailModal from "@/components/contest/ContestDetailModal";
import ContestFilterBar from "@/components/contest/ContestFilterBar";
import ContestFilterSheet from "@/components/contest/ContestFilterSheet";

export default function ConcoursScreen() {
  const [selected, setSelected] = useState<ContestDetail | null>(null);
  const [filter, setFilter] = useState<ContestFilter>(DEFAULT_CONTEST_FILTER);
  const [sheetVisible, setSheetVisible] = useState(false);

  // TODO: 콩쿠르 목록 조회 API(GET /api/contests) 연동.
  // 크롤링 기반이라 필터링(categories/instruments)·정렬(deadline)을
  // 프론트에서 계속 처리할지, 서버 쿼리 파라미터로 옮길지는 백엔드 설계 후 결정.
  const filteredContests = useMemo(() => {
    let result = [...MOCK_CONTESTS];

    if (filter.categories.length > 0) {
      result = result.filter((c) => filter.categories.includes(c.category));
    }
    if (filter.instruments.length > 0) {
      result = result.filter((c) => filter.instruments.includes(c.targetInstrument));
    }
    if (filter.sort === "deadline") {
      result = [...result].sort(
        (a, b) => new Date(a.deadline).getTime() - new Date(b.deadline).getTime()
      );
    }

    return result;
  }, [filter]);

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <AppHeader />

      <ContestFilterBar
        filter={filter}
        onChange={setFilter}
        onOpenSheet={() => setSheetVisible(true)}
        onReset={() => setFilter(DEFAULT_CONTEST_FILTER)}
      />

      <View style={styles.resultRow}>
        <Text style={styles.resultText}>총 {filteredContests.length}개</Text>
      </View>

      <FlatList
        data={filteredContests}
        keyExtractor={(item) => String(item.id)}
        renderItem={({ item }) => (
          <ContestCard contest={item} onPress={() => setSelected(item)} />
        )}
        contentContainerStyle={styles.listContent}
        ItemSeparatorComponent={() => <View style={styles.separator} />}
        ListEmptyComponent={
          <EmptyState
            icon="trophy-outline"
            title="조건에 맞는 콩쿠르가 없어요"
            description="필터를 조정하거나 나중에 다시 확인해 주세요."
          />
        }
      />

      <ContestFilterSheet
        visible={sheetVisible}
        filter={filter}
        onApply={setFilter}
        onClose={() => setSheetVisible(false)}
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
