import React, { useState } from "react";
import {
  View,
  FlatList,
  StyleSheet,
  TouchableOpacity,
  Text,
  Modal,
  ScrollView,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { MOCK_PRACTICE_LOGS } from "@/mocks/practiceLogs";
import { PracticeLog } from "@/types/practiceLog";
import AppHeader from "@/components/common/AppHeader";
import EmptyState from "@/components/common/EmptyState";
import PracticeLogCard from "@/components/practiceLog/PracticeLogCard";
import { formatDate, formatDuration } from "@/utils/datetime";

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

const modalStyles = StyleSheet.create({
  // 반투명 배경 + 중앙 정렬. padding이 크림 박스의 상하좌우 여백을 결정
  wrapper: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(0,0,0,0.4)",
    padding: 28,
  },
  // X 버튼을 절대 위치로 걸치기 위한 relative 컨테이너
  container: {
    width: "100%",
  },
  // 원형 X 버튼 — 크림 박스 우측 상단 모서리에 걸침
  closeButton: {
    position: "absolute",
    top: -8,
    right: -8,
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: colors.primary,
    justifyContent: "center",
    alignItems: "center",
    zIndex: 1,
  },
  // 크림 배경 박스 — 사방 동일 패딩
  sheet: {
    backgroundColor: colors.background,
    borderRadius: 20,
    padding: 20,
  },
  // 흰색 카드 프레임
  card: {
    backgroundColor: colors.surface,
    borderRadius: 14,
    padding: 16,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    maxHeight: 360,
  },
  title: {
    fontSize: 16,
    fontWeight: "700",
    color: colors.primary,
    marginBottom: 12,
  },
  metaRow: {
    flexDirection: "row",
    gap: 16,
    marginBottom: 14,
  },
  metaItem: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
  },
  metaText: {
    fontSize: 13,
    color: colors.textMuted,
  },
  divider: {
    height: StyleSheet.hairlineWidth,
    backgroundColor: colors.border,
    marginBottom: 14,
  },
  content: {
    fontSize: 14,
    color: colors.textSecondary,
    lineHeight: 22,
  },
});

export default function PracticeLogScreen() {
  const router = useRouter();
  const [selected, setSelected] = useState<PracticeLog | null>(null);

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
        data={MOCK_PRACTICE_LOGS}
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

      <PracticeLogDetailModal
        log={selected}
        onClose={() => setSelected(null)}
      />
    </SafeAreaView>
  );
}

function PracticeLogDetailModal({
  log,
  onClose,
}: {
  log: PracticeLog | null;
  onClose: () => void;
}) {
  return (
    <Modal
      visible={!!log}
      transparent
      animationType="fade"
      onRequestClose={onClose}
    >
      {/* 전체 래퍼: 반투명 배경 + 중앙 정렬 */}
      <View style={modalStyles.wrapper}>
        {/* 바깥 터치 시 닫기 */}
        <TouchableOpacity
          style={StyleSheet.absoluteFillObject}
          activeOpacity={1}
          onPress={onClose}
        />

        {/* X 버튼 절대 위치를 위한 relative 컨테이너 */}
        <View style={modalStyles.container}>
          {/* 원형 X 버튼 — 우측 상단 모서리에 걸침 */}
          <TouchableOpacity style={modalStyles.closeButton} onPress={onClose}>
            <Ionicons name="close" size={14} color={colors.background} />
          </TouchableOpacity>

          {/* 크림 배경 박스 */}
          <View style={modalStyles.sheet}>
            {/* 흰색 카드 프레임 */}
            <View style={modalStyles.card}>
              <Text style={modalStyles.title} numberOfLines={2}>
                {log?.title}
              </Text>

              <View style={modalStyles.metaRow}>
                <View style={modalStyles.metaItem}>
                  <Ionicons name="calendar-outline" size={14} color={colors.textMuted} />
                  <Text style={modalStyles.metaText}>
                    {log ? formatDate(log.practicedAt) : ""}
                  </Text>
                </View>
                <View style={modalStyles.metaItem}>
                  <Ionicons name="time-outline" size={14} color={colors.textMuted} />
                  <Text style={modalStyles.metaText}>
                    {log ? formatDuration(log.durationMinutes) : ""}
                  </Text>
                </View>
              </View>

              <View style={modalStyles.divider} />

              <ScrollView showsVerticalScrollIndicator={false}>
                <Text style={modalStyles.content}>{log?.content}</Text>
              </ScrollView>
            </View>
          </View>
        </View>
      </View>
    </Modal>
  );
}
