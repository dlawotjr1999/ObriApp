import React from "react";
import { View, Text, TouchableOpacity, StyleSheet, Modal, Linking, Alert } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { ContestDetail } from "@/types/contest";
import { formatDate } from "@/utils/datetime";
import Tag from "@/components/common/Tag";
import IconText from "@/components/common/IconText";
import ThemedButton from "@/components/common/ThemedButton";

interface ContestDetailModalProps {
  contest: ContestDetail | null;
  onClose: () => void;
}

export default function ContestDetailModal({ contest, onClose }: ContestDetailModalProps) {
  const handleApply = async () => {
    if (!contest) return;
    try {
      await Linking.openURL(contest.sourceUrl);
    } catch {
      Alert.alert("링크를 열 수 없어요", "잠시 후 다시 시도해주세요.");
    }
  };

  return (
    <Modal visible={!!contest} transparent animationType="fade" onRequestClose={onClose}>
      {/* 전체 래퍼: 반투명 배경 + 중앙 정렬 */}
      <View style={styles.wrapper}>
        {/* 바깥 터치 시 닫기 */}
        <TouchableOpacity
          style={StyleSheet.absoluteFill}
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
                {contest?.title}
              </Text>

              <View style={styles.badgeRow}>
                {contest && <Tag label={contest.category} />}
              </View>

              <View style={styles.divider} />

              <View style={styles.metaList}>
                <IconText icon="business-outline" text={contest?.organizer ?? ""} />
                <IconText
                  icon="alarm-outline"
                  text={contest ? `접수 마감 ${formatDate(contest.deadline)}` : ""}
                />
                <IconText
                  icon="calendar-outline"
                  text={
                    contest
                      ? `대회 기간 ${formatDate(contest.startDate)} ~ ${formatDate(contest.endDate)}`
                      : ""
                  }
                />
              </View>

              <View style={styles.divider} />

              <ThemedButton title="지원하기" onPress={handleApply} />
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
    gap: 14,
  },
  title: {
    fontSize: 16,
    fontWeight: "700",
    color: colors.primary,
  },
  badgeRow: {
    flexDirection: "row",
    gap: 6,
  },
  divider: {
    height: StyleSheet.hairlineWidth,
    backgroundColor: colors.border,
  },
  metaList: {
    gap: 8,
  },
});
