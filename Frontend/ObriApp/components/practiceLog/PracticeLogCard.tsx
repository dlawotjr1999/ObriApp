import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { colors } from "@/constants/theme";
import { PracticeLog } from "@/types/practiceLog";
import { formatDate, formatDuration } from "@/utils/datetime";

interface PracticeLogCardProps {
  log: PracticeLog;
  onPress?: (id: number) => void;
}

export default function PracticeLogCard({ log, onPress }: PracticeLogCardProps) {
  return (
    <TouchableOpacity
      style={styles.card}
      activeOpacity={0.8}
      onPress={() => onPress?.(log.id)}
    >
      <View style={styles.topRow}>
        <Text style={styles.date}>{formatDate(log.practicedAt)}</Text>
        <View style={styles.durationBadge}>
          <Text style={styles.durationText}>{formatDuration(log.durationMinutes)}</Text>
        </View>
      </View>

      <Text style={styles.title} numberOfLines={1}>
        {log.title}
      </Text>

      <Text style={styles.content} numberOfLines={2}>
        {log.content}
      </Text>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    backgroundColor: colors.surface,
    borderRadius: 12,
    padding: 16,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
  },
  topRow: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 8,
  },
  date: {
    fontSize: 12,
    color: colors.textMuted,
  },
  durationBadge: {
    backgroundColor: colors.background,
    borderRadius: 12,
    paddingHorizontal: 10,
    paddingVertical: 3,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
  },
  durationText: {
    fontSize: 12,
    color: colors.textSecondary,
    fontWeight: "500",
  },
  title: {
    fontSize: 15,
    fontWeight: "600",
    color: colors.primary,
    marginBottom: 6,
  },
  content: {
    fontSize: 13,
    color: colors.textMuted,
    lineHeight: 19,
  },
});
