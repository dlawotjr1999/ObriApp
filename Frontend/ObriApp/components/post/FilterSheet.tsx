import React, { useState } from "react";
import {
  Modal,
  View,
  Text,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { INSTRUMENTS, REGIONS, STATUS_LABELS } from "@/constants/filterOptions";
import { PostFilter } from "@/types/filter";
import { PostStatus } from "@/types/post";

const STATUSES: PostStatus[] = ["OPEN", "PARTIALLY_CLOSED", "CLOSED"];

interface FilterSheetProps {
  visible: boolean;
  filter: PostFilter;
  onApply: (filter: PostFilter) => void;
  onClose: () => void;
}

export default function FilterSheet({ visible, filter, onApply, onClose }: FilterSheetProps) {
  const insets = useSafeAreaInsets();
  const [draft, setDraft] = useState<PostFilter>(filter);

  function toggle<T>(arr: T[], val: T): T[] {
    return arr.includes(val) ? arr.filter((v) => v !== val) : [...arr, val];
  }

  function handleApply() {
    onApply(draft);
    onClose();
  }

  function handleReset() {
    setDraft({ ...filter, instruments: [], regions: [], status: [] });
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
        {/* 헤더 */}
        <View style={styles.sheetHeader}>
          <Text style={styles.sheetTitle}>필터</Text>
          <TouchableOpacity onPress={onClose} hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}>
            <Ionicons name="close" size={22} color={colors.primary} />
          </TouchableOpacity>
        </View>

        <ScrollView showsVerticalScrollIndicator={false}>
          <Section title="악기">
            <ChipGroup
              options={INSTRUMENTS}
              selected={draft.instruments}
              onToggle={(v) => setDraft({ ...draft, instruments: toggle(draft.instruments, v) })}
            />
          </Section>

          <Section title="지역">
            <ChipGroup
              options={REGIONS}
              selected={draft.regions}
              onToggle={(v) => setDraft({ ...draft, regions: toggle(draft.regions, v) })}
            />
          </Section>

          <Section title="상태">
            <ChipGroup
              options={STATUSES}
              selected={draft.status}
              onToggle={(v) => setDraft({ ...draft, status: toggle(draft.status, v as PostStatus) })}
              labelMap={STATUS_LABELS}
            />
          </Section>
        </ScrollView>

        {/* 하단 버튼 */}
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

function Section({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <View style={sectionStyles.container}>
      <Text style={sectionStyles.title}>{title}</Text>
      {children}
    </View>
  );
}

function ChipGroup({
  options,
  selected,
  onToggle,
  labelMap,
}: {
  options: string[];
  selected: string[];
  onToggle: (val: string) => void;
  labelMap?: Record<string, string>;
}) {
  return (
    <View style={chipStyles.group}>
      {options.map((opt) => {
        const active = selected.includes(opt);
        return (
          <TouchableOpacity
            key={opt}
            style={[chipStyles.chip, active && chipStyles.chipActive]}
            onPress={() => onToggle(opt)}
            activeOpacity={0.7}
          >
            <Text style={[chipStyles.text, active && chipStyles.textActive]}>
              {labelMap ? labelMap[opt] : opt}
            </Text>
          </TouchableOpacity>
        );
      })}
    </View>
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

const sectionStyles = StyleSheet.create({
  container: {
    marginBottom: 24,
  },
  title: {
    fontSize: 12,
    color: colors.textMuted,
    letterSpacing: 1,
    marginBottom: 12,
  },
});

const chipStyles = StyleSheet.create({
  group: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
  },
  chip: {
    paddingHorizontal: 14,
    paddingVertical: 7,
    borderRadius: 20,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    backgroundColor: colors.surface,
  },
  chipActive: {
    backgroundColor: colors.primary,
    borderColor: colors.primary,
  },
  text: {
    fontSize: 13,
    color: colors.textSecondary,
  },
  textActive: {
    color: colors.background,
    fontWeight: "600",
  },
});
