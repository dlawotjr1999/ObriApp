import React, { useState } from "react";
import { Modal, View, Text, TouchableOpacity, ScrollView, StyleSheet } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { CONTEST_INSTRUMENTS } from "@/constants/filterOptions";
import { ContestFilter } from "@/types/contestFilter";
import Chip from "@/components/common/Chip";

interface ContestFilterSheetProps {
  visible: boolean;
  filter: ContestFilter;
  onApply: (filter: ContestFilter) => void;
  onClose: () => void;
}

export default function ContestFilterSheet({
  visible,
  filter,
  onApply,
  onClose,
}: ContestFilterSheetProps) {
  const insets = useSafeAreaInsets();
  const [draft, setDraft] = useState<ContestFilter>(filter);

  function toggleInstrument(val: string) {
    const next = draft.instruments.includes(val)
      ? draft.instruments.filter((v) => v !== val)
      : [...draft.instruments, val];
    setDraft({ ...draft, instruments: next });
  }

  function handleApply() {
    onApply(draft);
    onClose();
  }

  function handleReset() {
    setDraft({ ...filter, instruments: [] });
  }

  // 모달이 열릴 때마다 draft를 현재 filter로 동기화
  function handleShow() {
    setDraft(filter);
  }

  return (
    <Modal
      visible={visible}
      transparent
      animationType="slide"
      onShow={handleShow}
      onRequestClose={onClose}
    >
      <TouchableOpacity style={styles.backdrop} activeOpacity={1} onPress={onClose} />

      <View style={[styles.sheet, { paddingBottom: insets.bottom + 16 }]}>
        <View style={styles.sheetHeader}>
          <Text style={styles.sheetTitle}>필터</Text>
          <TouchableOpacity onPress={onClose} hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}>
            <Ionicons name="close" size={22} color={colors.primary} />
          </TouchableOpacity>
        </View>

        <ScrollView showsVerticalScrollIndicator={false}>
          <Text style={styles.sectionTitle}>악기</Text>
          <View style={styles.chipGroup}>
            {CONTEST_INSTRUMENTS.map((inst) => (
              <Chip
                key={inst}
                label={inst}
                active={draft.instruments.includes(inst)}
                onPress={() => toggleInstrument(inst)}
              />
            ))}
          </View>
        </ScrollView>

        <View style={styles.footer}>
          <TouchableOpacity style={styles.resetButton} onPress={handleReset} activeOpacity={0.7}>
            <Text style={styles.resetText}>초기화</Text>
          </TouchableOpacity>
          <TouchableOpacity style={styles.applyButton} onPress={handleApply} activeOpacity={0.85}>
            <Text style={styles.applyText}>적용하기</Text>
          </TouchableOpacity>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.3)",
  },
  sheet: {
    backgroundColor: colors.background,
    borderTopLeftRadius: 20,
    borderTopRightRadius: 20,
    maxHeight: "75%",
    paddingTop: 20,
    paddingHorizontal: 20,
  },
  sheetHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: 20,
  },
  sheetTitle: {
    fontSize: 17,
    fontWeight: "700",
    color: colors.primary,
  },
  sectionTitle: {
    fontSize: 12,
    color: colors.textMuted,
    letterSpacing: 1,
    marginBottom: 12,
  },
  chipGroup: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
    marginBottom: 24,
  },
  footer: {
    flexDirection: "row",
    gap: 12,
    marginTop: 16,
  },
  resetButton: {
    flex: 1,
    height: 48,
    borderRadius: 24,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    alignItems: "center",
    justifyContent: "center",
  },
  resetText: {
    fontSize: 14,
    color: colors.textSecondary,
  },
  applyButton: {
    flex: 2,
    height: 48,
    borderRadius: 24,
    backgroundColor: colors.primary,
    alignItems: "center",
    justifyContent: "center",
  },
  applyText: {
    fontSize: 15,
    fontWeight: "600",
    color: colors.background,
    letterSpacing: 0.5,
  },
});
