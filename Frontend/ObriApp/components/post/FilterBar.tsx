import React from "react";
import { View, ScrollView, TouchableOpacity, Text, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { CATEGORIES } from "@/constants/filterOptions";
import { PostFilter } from "@/types/filter";
import Chip, { chipTextColor } from "@/components/common/Chip";

interface FilterBarProps {
  filter: PostFilter;
  onChange: (filter: PostFilter) => void;
  onOpenSheet: () => void;
  onReset: () => void;
}

export default function FilterBar({ filter, onChange, onOpenSheet, onReset }: FilterBarProps) {
  const advancedCount =
    filter.instruments.length + filter.regions.length + filter.status.length;
  const hasAnyFilter =
    filter.sort !== "default" || filter.categories.length > 0 || advancedCount > 0;

  const sortActive = filter.sort === "latest";

  function toggleSort() {
    onChange({ ...filter, sort: sortActive ? "default" : "latest" });
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
          label="최신순"
          active={sortActive}
          onPress={toggleSort}
          leftIcon={
            <Ionicons name="time-outline" size={13} color={chipTextColor(sortActive)} />
          }
        />

        <View style={styles.divider} />

        {CATEGORIES.map((cat) => (
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
        {advancedCount > 0 && (
          <Text style={styles.filterCount}>{advancedCount}</Text>
        )}
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
