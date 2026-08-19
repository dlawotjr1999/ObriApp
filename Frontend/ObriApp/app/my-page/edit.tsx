import React, { useRef, useState } from "react";
import {
  View,
  Text,
  ScrollView,
  TouchableOpacity,
  StyleSheet,
  Alert,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { MOCK_USER } from "@/mocks/user";
import { CareerEntry } from "@/types/user";
import ThemedInput from "@/components/common/ThemedInput";
import ThemedButton from "@/components/common/ThemedButton";
import ChipSelect from "@/components/common/ChipSelect";
import ToggleField from "@/components/common/ToggleField";
import CareerFormItem from "@/components/auth/CareerFormItem";

const INSTRUMENTS = ["피아노", "바이올린", "첼로", "플루트", "성악", "기타"];

export default function EditProfileScreen() {
  const router = useRouter();

  const [nickname, setNickname] = useState(MOCK_USER.nickname);
  const [instrument, setInstrument] = useState(MOCK_USER.instrument);
  const [school, setSchool] = useState(MOCK_USER.school);
  const [isGraduate, setIsGraduate] = useState(MOCK_USER.isGraduate);
  const [careers, setCareers] = useState<CareerEntry[]>(
    MOCK_USER.careers.map(({ organization, contexts }) => ({ organization, contexts }))
  );

  // 배열 index는 항목 삭제 시 뒤 요소가 앞으로 당겨져 재사용되므로,
  // React key로 쓰기 위한 항목별 안정적인 로컬 id를 별도로 관리한다.
  const keyCounter = useRef(0);
  const [careerKeys, setCareerKeys] = useState<number[]>(() =>
    careers.map(() => keyCounter.current++)
  );

  const handleCheckNickname = () => {
    // TODO: GET /api/users/check/{nickname}
    Alert.alert("닉네임 확인", "사용 가능한 닉네임입니다.");
  };

  const handleCareerChange = (index: number, entry: CareerEntry) => {
    const updated = [...careers];
    updated[index] = entry;
    setCareers(updated);
  };

  const handleCareerRemove = (index: number) => {
    setCareers((prev) => prev.filter((_, i) => i !== index));
    setCareerKeys((prev) => prev.filter((_, i) => i !== index));
  };

  const handleCareerAdd = () => {
    setCareers((prev) => [...prev, { organization: "", contexts: "" }]);
    setCareerKeys((prev) => [...prev, keyCounter.current++]);
  };

  const handleSave = () => {
    // TODO: PATCH /api/users/me 연동 후 router.back()
    router.back();
  };

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()} activeOpacity={0.7}>
          <Ionicons name="chevron-back" size={24} color={colors.textPrimary} />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>프로필 수정</Text>
        <View style={{ width: 24 }} />
      </View>

      <ScrollView
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled"
        keyboardDismissMode="on-drag"
        showsVerticalScrollIndicator={false}
      >
        <View style={styles.nicknameRow}>
          <View style={styles.nicknameInput}>
            <ThemedInput
              label="닉네임"
              icon="person-outline"
              placeholder="닉네임 입력"
              value={nickname}
              onChangeText={setNickname}
            />
          </View>
          <TouchableOpacity style={styles.checkButton} onPress={handleCheckNickname}>
            <Text style={styles.checkButtonText}>중복 확인</Text>
          </TouchableOpacity>
        </View>

        <ChipSelect
          label="악기"
          options={INSTRUMENTS}
          selected={instrument}
          onSelect={setInstrument}
        />

        <ThemedInput
          label="학교"
          icon="school-outline"
          placeholder="학교명 입력"
          value={school}
          onChangeText={setSchool}
        />

        <ToggleField label="졸업 여부" value={isGraduate} onToggle={setIsGraduate} />

        <Text style={styles.sectionLabel}>경력</Text>
        {careers.map((career, index) => (
          <CareerFormItem
            key={careerKeys[index]}
            value={career}
            index={index}
            onChange={handleCareerChange}
            onRemove={handleCareerRemove}
            removable
          />
        ))}
        <TouchableOpacity style={styles.addButton} onPress={handleCareerAdd}>
          <Ionicons name="add" size={18} color={colors.textMuted} />
          <Text style={styles.addButtonText}>경력 추가</Text>
        </TouchableOpacity>

        <View style={styles.bottom}>
          <ThemedButton title="저장" onPress={handleSave} />
        </View>
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  header: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 16,
    paddingVertical: 12,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border,
  },
  headerTitle: {
    fontSize: 16,
    fontWeight: "600",
    color: colors.textPrimary,
  },
  scrollContent: {
    paddingHorizontal: 20,
    paddingTop: 20,
  },
  nicknameRow: {
    flexDirection: "row",
    alignItems: "flex-end",
    gap: 8,
  },
  nicknameInput: {
    flex: 1,
  },
  checkButton: {
    backgroundColor: colors.surface,
    borderWidth: 0.5,
    borderColor: colors.border,
    borderRadius: 10,
    paddingHorizontal: 14,
    height: 46,
    justifyContent: "center",
    marginBottom: 16,
  },
  checkButtonText: {
    fontSize: 13,
    color: colors.textSecondary,
  },
  sectionLabel: {
    fontSize: 12,
    color: colors.textSecondary,
    letterSpacing: 1,
    marginBottom: 10,
  },
  addButton: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: 6,
    borderWidth: 1,
    borderStyle: "dashed",
    borderColor: colors.border,
    borderRadius: 10,
    height: 44,
    marginBottom: 24,
  },
  addButtonText: {
    fontSize: 13,
    color: colors.textMuted,
  },
  bottom: {
    marginBottom: 32,
  },
});
