import React from "react";
import { View, ScrollView, TouchableOpacity, Text, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { CONTEST_CATEGORIES } from "@/constants/filterOptions";
import { ContestFilter } from "@/types/contestFilter";
import Chip, { chipTextColor } from "@/components/common/Chip";

interface ContestFilterBarProps {
  filter: ContestFilter;
  onChange: (filter: ContestFilter) => void;
  onOpenSheet: () => void;
  onReset: () => void;
}

export default function ContestFilterBar({
  filter,
  onChange,
  onOpenSheet,
  onReset,
}: ContestFilterBarProps) {
  const advancedCount = filter.instruments.length;
  const hasAnyFilter =
    filter.sort !== "default" || filter.categories.length > 0 || advancedCount > 0;

  const sortActive = filter.sort === "deadline";

  function toggleSort() {
    onChange({ ...filter, sort: sortActive ? "default" : "deadline" });
  }

  function toggleCategory(cat: string) {
    const next = filter.categories.includes(cat)
      ? filter.categories.filter((c) => c !== cat)
      : [...filter.categories, cat];
    onChange({ ...filter, categories: next });
  }

  return (
    <View style={styles.wrapper}>
      <ScrollView
        horizontal
        showsHorizontalScrollIndicator={false}
        contentContainerStyle={styles.row}
      >
        <Chip
          label="마감임박순"
          active={sortActive}
          onPress={toggleSort}
          leftIcon={
            <Ionicons name="alarm-outline" size={13} color={chipTextColor(sortActive)} />
          }
        />

        <View style={styles.divider} />

        {CONTEST_CATEGORIES.map((cat) => (
          <Chip
            key={cat}
            label={cat}
            active={filter.categories.includes(cat)}
            onPress={() => toggleCategory(cat)}
          />
        ))}
      </ScrollView>

      {hasAnyFilter && (
        <TouchableOpacity style={styles.resetButton} onPress={onReset} activeOpacity={0.7}>
          <Ionicons name="close-circle" size={15} color={colors.textMuted} />
        </TouchableOpacity>
      )}

      <TouchableOpacity
        style={[styles.filterButton, advancedCount > 0 && styles.filterButtonActive]}
        onPress={onOpenSheet}
        activeOpacity={0.7}
      >
        <Ionicons
          name="options-outline"
          size={16}
          color={advancedCount > 0 ? colors.background : colors.textSecondary}
        />
        {advancedCount > 0 && <Text style={styles.filterCount}>{advancedCount}</Text>}
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    flexDirection: "row",
    alignItems: "center",
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border,
    backgroundColor: colors.background,
  },
  row: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 16,
    paddingVertical: 10,
    gap: 8,
  },
  divider: {
    width: StyleSheet.hairlineWidth,
    height: 16,
    backgroundColor: colors.border,
    marginHorizontal: 4,
  },
  filterButton: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
    paddingHorizontal: 12,
    paddingVertical: 10,
    borderLeftWidth: StyleSheet.hairlineWidth,
    borderLeftColor: colors.border,
  },
  filterButtonActive: {
    backgroundColor: colors.primaryLight,
  },
  filterCount: {
    fontSize: 11,
    fontWeight: "700",
    color: colors.background,
  },
  resetButton: {
    paddingHorizontal: 8,
    paddingVertical: 10,
  },
});
