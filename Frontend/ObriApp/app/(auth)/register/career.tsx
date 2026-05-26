import React from "react";
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
} from "react-native";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { useRegisterForm } from "@/contexts/RegisterContext";
import ScreenHeader from "@/components/common/ScreenHeader";
import StepIndicator from "@/components/common/StepIndicator";
import ThemedButton from "@/components/common/ThemedButton";
import CareerFormItem, { CareerEntry } from "@/components/auth/CareerFormItem";

export default function RegisterStep3() {
  const router = useRouter();
  const { form, updateForm } = useRegisterForm();

  const handleChange = (index: number, entry: CareerEntry) => {
    const updated = [...form.careers];
    updated[index] = entry;
    updateForm({ careers: updated });
  };

  const handleRemove = (index: number) => {
    const updated = form.careers.filter((_, i) => i !== index);
    updateForm({ careers: updated });
  };

  const handleAdd = () => {
    updateForm({
      careers: [...form.careers, { organization: "", contexts: "" }],
    });
  };

  const handleSubmit = () => {
    // TODO: Firebase 회원가입 → POST /api/auth/register
  };

  const handleSkip = () => {
    updateForm({ careers: [] });
    handleSubmit();
  };

  return (
    <View style={styles.container}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled"
        keyboardDismissMode="on-drag"
      >
        <ScreenHeader
          title="경력 사항"
          subtitle="연주 경력이 있다면 추가해주세요"
        />
        <StepIndicator total={3} current={3} />

        {form.careers.map((career, index) => (
          <CareerFormItem
            key={index}
            value={career}
            index={index}
            onChange={handleChange}
            onRemove={handleRemove}
            removable={form.careers.length > 1}
          />
        ))}

        <TouchableOpacity style={styles.addButton} onPress={handleAdd}>
          <Ionicons name="add" size={18} color={colors.textMuted} />
          <Text style={styles.addButtonText}>경력 추가</Text>
        </TouchableOpacity>

        <View style={styles.bottom}>
          <ThemedButton title="가입 완료" onPress={handleSubmit} />
          <TouchableOpacity style={styles.skipButton} onPress={handleSkip}>
            <Text style={styles.skipText}>건너뛰기</Text>
          </TouchableOpacity>
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
    marginTop: "auto",
    marginBottom: 32,
  },
  skipButton: {
    alignItems: "center",
    marginTop: 12,
  },
  skipText: {
    fontSize: 13,
    color: colors.textMuted,
  },
});
