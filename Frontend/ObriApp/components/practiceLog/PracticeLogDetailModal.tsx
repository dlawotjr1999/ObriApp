import React from "react";
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  Modal,
  ScrollView,
  Dimensions,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { PracticeLog } from "@/types/practiceLog";
import { formatDate, formatDuration } from "@/utils/datetime";

const { height: SCREEN_HEIGHT } = Dimensions.get("window");

interface PracticeLogDetailModalProps {
  log: PracticeLog | null;
  onClose: () => void;
}

export default function PracticeLogDetailModal({
  log,
  onClose,
}: PracticeLogDetailModalProps) {
  return (
    <Modal visible={!!log} transparent animationType="fade" onRequestClose={onClose}>
      {/* 전체 래퍼: 반투명 배경 + 중앙 정렬 */}
      <View style={styles.wrapper}>
        {/* 바깥 터치 시 닫기 */}
        <TouchableOpacity
          style={StyleSheet.absoluteFillObject}
          activeOpacity={1}
          onPress={onClose}
        />

        {/* X 버튼 절대 위치를 위한 relative 컨테이너 */}
        <View style={styles.container}>
          {/* 원형 X 버튼 — 우측 상단 모서리에 걸침 */}
          <TouchableOpacity style={styles.closeButton} onPress={onClose}>
            <Ionicons name="close" size={14} color={colors.background} />
          </TouchableOpacity>

          {/* 크림 배경 박스 */}
          <View style={styles.sheet}>
            {/* 흰색 카드 프레임 */}
            <View style={styles.card}>
              <Text style={styles.title} numberOfLines={2}>
                {log?.title}
              </Text>

              <View style={styles.metaRow}>
                <View style={styles.metaItem}>
                  <Ionicons name="calendar-outline" size={14} color={colors.textMuted} />
                  <Text style={styles.metaText}>
                    {log ? formatDate(log.logDate) : ""}
                  </Text>
                </View>
                <View style={styles.metaItem}>
                  <Ionicons name="time-outline" size={14} color={colors.textMuted} />
                  <Text style={styles.metaText}>
                    {log ? formatDuration(log.duration) : ""}
                  </Text>
                </View>
              </View>

              <View style={styles.divider} />

              <ScrollView style={styles.scrollArea} showsVerticalScrollIndicator={false}>
                <Text style={styles.content}>{log?.content}</Text>
              </ScrollView>
            </View>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
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
  // 부모가 auto 높이라 flex로는 크기를 못 잡으므로, 직접 상한을 줘서 이 높이를 넘으면 스크롤되게 함
  scrollArea: {
    maxHeight: SCREEN_HEIGHT * 0.55,
  },
  content: {
    fontSize: 14,
    color: colors.textSecondary,
    lineHeight: 22,
  },
});
