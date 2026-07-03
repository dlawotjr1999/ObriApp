import React from "react";
import {
  View,
  ScrollView,
  TouchableOpacity,
  Text,
  StyleSheet,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { CATEGORIES } from "@/constants/filterOptions";
import { PostFilter, DEFAULT_FILTER, activeAdvancedCount } from "@/types/filter";

interface FilterBarProps {
  filter: PostFilter;
  onChange: (filter: PostFilter) => void;
  onOpenSheet: () => void;
  onReset: () => void;
}

export default function FilterBar({ filter, onChange, onOpenSheet, onReset }: FilterBarProps) {
  const advancedCount = activeAdvancedCount(filter);
  const hasAnyFilter =
    filter.sort !== "default" || filter.categories.length > 0 || advancedCount > 0;

  function toggleSort() {
    onChange({ ...filter, sort: filter.sort === "latest" ? "default" : "latest" });
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
        {/* 최신순 */}
        <TouchableOpacity
          style={[styles.chip, filter.sort === "latest" && styles.chipActive]}
          onPress={toggleSort}
          activeOpacity={0.7}
        >
          <Ionicons
            name="time-outline"
            size={13}
            color={filter.sort === "latest" ? colors.background : colors.textMuted}
          />
          <Text style={[styles.chipText, filter.sort === "latest" && styles.chipTextActive]}>
            최신순
          </Text>
        </TouchableOpacity>

        <View style={styles.divider} />

        {/* 카테고리 */}
        {CATEGORIES.map((cat) => {
          const active = filter.categories.includes(cat);
          return (
            <TouchableOpacity
              key={cat}
              style={[styles.chip, active && styles.chipActive]}
              onPress={() => toggleCategory(cat)}
              activeOpacity={0.7}
            >
              <Text style={[styles.chipText, active && styles.chipTextActive]}>
                {cat}
              </Text>
            </TouchableOpacity>
          );
        })}
      </ScrollView>

      {/* 초기화 버튼 — 필터가 하나라도 켜져 있을 때만 노출 */}
      {hasAnyFilter && (
        <TouchableOpacity style={styles.resetButton} onPress={onReset} activeOpacity={0.7}>
          <Ionicons name="close-circle" size={15} color={colors.textMuted} />
        </TouchableOpacity>
      )}

      {/* 고급 필터 버튼 */}
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
  chip: {
    flexDirection: "row",
    alignItems: "center",
    gap: 4,
    paddingHorizontal: 12,
    paddingVertical: 6,
    borderRadius: 20,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    backgroundColor: colors.surface,
  },
  chipActive: {
    backgroundColor: colors.primary,
    borderColor: colors.primary,
  },
  chipText: {
    fontSize: 13,
    color: colors.textSecondary,
  },
  chipTextActive: {
    color: colors.background,
    fontWeight: "600",
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
