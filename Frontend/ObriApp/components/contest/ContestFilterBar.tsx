import React from "react";
import { View, ScrollView, TouchableOpacity, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { CONTEST_CATEGORIES } from "@/constants/filterOptions";
import { ContestFilter } from "@/types/contestFilter";
import Chip from "@/components/common/Chip";

interface ContestFilterBarProps {
  filter: ContestFilter;
  onChange: (filter: ContestFilter) => void;
  onReset: () => void;
}

export default function ContestFilterBar({ filter, onChange, onReset }: ContestFilterBarProps) {
  const hasAnyFilter = filter.categories.length > 0;

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
  resetButton: {
    paddingHorizontal: 12,
    paddingVertical: 10,
  },
});
