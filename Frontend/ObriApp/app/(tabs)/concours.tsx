import React, { useCallback, useEffect, useState } from "react";
import { View, FlatList, StyleSheet, Text, ActivityIndicator } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { colors } from "@/constants/theme";
import { getContests } from "@/api/concours";
import { ApiError } from "@/lib/apiClient";
import { ContestDetail } from "@/types/contest";
import { ContestFilter, DEFAULT_CONTEST_FILTER } from "@/types/contestFilter";
import AppHeader from "@/components/common/AppHeader";
import EmptyState from "@/components/common/EmptyState";
import ContestCard from "@/components/contest/ContestCard";
import ContestDetailModal from "@/components/contest/ContestDetailModal";
import ContestFilterBar from "@/components/contest/ContestFilterBar";

export default function ConcoursScreen() {
  const [selected, setSelected] = useState<ContestDetail | null>(null);
  const [filter, setFilter] = useState<ContestFilter>(DEFAULT_CONTEST_FILTER);

  const [contests, setContests] = useState<ContestDetail[]>([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [hasNext, setHasNext] = useState(false);
  const [loading, setLoading] = useState(true);
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 필터가 바뀌면 첫 페이지부터 새로 조회
  const loadFirstPage = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const page = await getContests(filter.categories, 0);
      setContests(page.content);
      setCurrentPage(page.currentPage);
      setHasNext(page.hasNext);
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "콩쿠르 목록을 불러오지 못했어요.");
    } finally {
      setLoading(false);
    }
  }, [filter]);

  useEffect(() => {
    loadFirstPage();
  }, [loadFirstPage]);

  // 무한스크롤 — 다음 페이지를 이어붙임
  const loadNextPage = async () => {
    if (loadingMore || !hasNext) return;
    setLoadingMore(true);
    try {
      const page = await getContests(filter.categories, currentPage + 1);
      setContests((prev) => [...prev, ...page.content]);
      setCurrentPage(page.currentPage);
      setHasNext(page.hasNext);
    } catch {
      // 다음 페이지 실패는 조용히 무시 — 이미 보여준 목록은 그대로 유지, 스크롤하면 재시도됨
    } finally {
      setLoadingMore(false);
    }
  };

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <AppHeader />

      <ContestFilterBar
        filter={filter}
        onChange={setFilter}
        onReset={() => setFilter(DEFAULT_CONTEST_FILTER)}
      />

      {!loading && !error && (
        <View style={styles.resultRow}>
          <Text style={styles.resultText}>총 {contests.length}개</Text>
        </View>
      )}

      {loading ? (
        <View style={styles.centerFill}>
          <ActivityIndicator color={colors.primary} />
        </View>
      ) : error ? (
        <EmptyState icon="cloud-offline-outline" title="목록을 불러오지 못했어요" description={error} />
      ) : (
        <FlatList
          data={contests}
          keyExtractor={(item) => String(item.id)}
          renderItem={({ item }) => (
            <ContestCard contest={item} onPress={() => setSelected(item)} />
          )}
          contentContainerStyle={styles.listContent}
          ItemSeparatorComponent={() => <View style={styles.separator} />}
          onEndReached={loadNextPage}
          onEndReachedThreshold={0.4}
          ListFooterComponent={
            loadingMore ? (
              <ActivityIndicator style={styles.footerSpinner} color={colors.primary} />
            ) : null
          }
          ListEmptyComponent={
            <EmptyState
              icon="trophy-outline"
              title="조건에 맞는 콩쿠르가 없어요"
              description="필터를 조정하거나 나중에 다시 확인해 주세요."
            />
          }
        />
      )}

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
  centerFill: { flex: 1, alignItems: "center", justifyContent: "center" },
  footerSpinner: { marginVertical: 16 },
});
