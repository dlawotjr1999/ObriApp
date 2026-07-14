import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { colors } from "@/constants/theme";
import { ContestSummary } from "@/types/contest";
import { getDday } from "@/utils/datetime";
import Tag from "@/components/common/Tag";

interface ContestCardProps {
  contest: ContestSummary;
  onPress?: () => void;
}

export default function ContestCard({ contest, onPress }: ContestCardProps) {
  const dday = getDday(contest.deadline);

  return (
    <TouchableOpacity style={styles.card} activeOpacity={0.8} onPress={onPress}>
      <View style={styles.topRow}>
        <Text style={styles.title} numberOfLines={1}>
          {contest.title}
        </Text>
        {dday.label ? (
          <Text
            style={[
              styles.dday,
              dday.urgent && styles.ddayUrgent,
              dday.expired && styles.ddayExpired,
            ]}
          >
            {dday.label}
          </Text>
        ) : null}
      </View>

      <View style={styles.badgeRow}>
        <Tag label={contest.category} />
        <Tag label={contest.targetInstrument} />
      </View>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: 14,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    padding: 14,
    gap: 8,
  },
  topRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
  },
  title: {
    flex: 1,
    fontSize: 15,
    fontWeight: "600",
    color: colors.textPrimary,
  },
  dday: {
    fontSize: 11,
    fontWeight: "600",
    color: colors.textMuted,
    flexShrink: 0,
  },
  ddayUrgent: {
    color: "#C0392B",
  },
  ddayExpired: {
    color: colors.placeholder,
  },
  badgeRow: {
    flexDirection: "row",
    gap: 6,
  },
});
