import React from "react";
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  Alert,
} from "react-native";
import { useRouter } from "expo-router";
import { colors } from "@/constants/theme";
import { useRegisterForm } from "@/contexts/RegisterContext";
import ScreenHeader from "@/components/common/ScreenHeader";
import StepIndicator from "@/components/common/StepIndicator";
import ThemedInput from "@/components/common/ThemedInput";
import ThemedButton from "@/components/common/ThemedButton";
import ChipSelect from "@/components/common/ChipSelect";
import ToggleField from "@/components/common/ToggleField";

const INSTRUMENTS = ["피아노", "바이올린", "첼로", "플루트", "성악", "기타"];

export default function RegisterStep2() {
  const router = useRouter();
  const { form, updateForm } = useRegisterForm();

  const handleCheckNickname = () => {
    // TODO: GET /api/users/check/{nickname}
    Alert.alert("닉네임 확인", "사용 가능한 닉네임입니다.");
  };

  const handleNext = () => {
    // TODO: 유효성 검사
    router.push("/(auth)/register/career");
  };

  return (
    <View style={styles.container}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled"
        keyboardDismissMode="on-drag"
      >
        <ScreenHeader
          title="프로필 설정"
          subtitle="나를 소개할 기본 정보를 입력해주세요"
        />
        <StepIndicator total={3} current={2} />

        <View style={styles.nicknameRow}>
          <View style={styles.nicknameInput}>
            <ThemedInput
              label="닉네임"
              icon="person-outline"
              placeholder="닉네임 입력"
              value={form.nickname}
              onChangeText={(v) => updateForm({ nickname: v })}
            />
          </View>
          <TouchableOpacity
            style={styles.checkButton}
            onPress={handleCheckNickname}
          >
            <Text style={styles.checkButtonText}>중복 확인</Text>
          </TouchableOpacity>
        </View>

        <ThemedInput
          label="전화번호"
          icon="call-outline"
          placeholder="010-0000-0000"
          value={form.phoneNumber}
          onChangeText={(v) => updateForm({ phoneNumber: v })}
          keyboardType="phone-pad"
        />

        <ChipSelect
          label="악기"
          options={INSTRUMENTS}
          selected={form.instrument}
          onSelect={(v) => updateForm({ instrument: v })}
        />

        <ThemedInput
          label="학교"
          icon="school-outline"
          placeholder="학교명 입력"
          value={form.school}
          onChangeText={(v) => updateForm({ school: v })}
        />

        <ToggleField
          label="졸업 여부"
          value={form.isGraduate}
          onToggle={(v) => updateForm({ isGraduate: v })}
        />

        <View style={styles.bottom}>
          <ThemedButton title="다음" onPress={handleNext} />
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  scrollContent: {
    // flexGrow: 1,
    paddingHorizontal: 28,
    paddingTop: 16,
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
  bottom: {
    marginTop: "auto",
    marginBottom: 32,
  },
});
