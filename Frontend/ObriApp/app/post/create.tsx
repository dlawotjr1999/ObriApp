import React from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
} from "react-native";
import { SafeAreaView, useSafeAreaInsets } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import ThemedButton from "@/components/common/ThemedButton";

export default function PostCreateScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      {/* 헤더 */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()} hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}>
          <Ionicons name="arrow-back" size={22} color={colors.primary} />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>구인글 작성</Text>
        <View style={{ width: 22 }} />
      </View>

      <KeyboardAvoidingView
        style={{ flex: 1 }}
        behavior={Platform.OS === "ios" ? "padding" : undefined}
      >
        <ScrollView
          contentContainerStyle={[styles.content, { paddingBottom: insets.bottom + 80 }]}
          keyboardShouldPersistTaps="handled"
          showsVerticalScrollIndicator={false}
        >
          {/* TODO: 구인글 작성 폼 — 추후 구현 */}
          <View style={styles.placeholderArea}>
            <Ionicons name="create-outline" size={48} color={colors.placeholder} />
            <Text style={styles.placeholderTitle}>구인글 작성</Text>
            <Text style={styles.placeholderDesc}>
              제목, 카테고리, 공연 일시, 장소, 페이, 모집 악기 등{"\n"}세부 항목은 추후 추가될 예정이에요.
            </Text>
          </View>

          {/* 제목 입력 (샘플) */}
          <View style={styles.fieldGroup}>
            <Text style={styles.label}>제목</Text>
            <TextInput
              style={styles.input}
              placeholder="구인글 제목을 입력하세요"
              placeholderTextColor={colors.placeholder}
            />
          </View>

          <View style={styles.fieldGroup}>
            <Text style={styles.label}>내용</Text>
            <TextInput
              style={[styles.input, styles.textArea]}
              placeholder="공연 정보, 조건 등을 자유롭게 작성해주세요"
              placeholderTextColor={colors.placeholder}
              multiline
              numberOfLines={6}
              textAlignVertical="top"
            />
          </View>
        </ScrollView>

        {/* 하단 등록 버튼 */}
        <View style={[styles.footer, { paddingBottom: insets.bottom + 12 }]}>
          <ThemedButton
            title="등록하기"
            onPress={() => {
              // TODO: 구인글 등록 API(POST /api/posts) 연동
              router.back();
            }}
          />
        </View>
      </KeyboardAvoidingView>
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
    paddingHorizontal: 20,
    paddingVertical: 14,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border,
  },
  headerTitle: {
    fontSize: 16,
    fontWeight: "600",
    color: colors.primary,
  },
  content: {
    padding: 24,
    gap: 24,
  },
  placeholderArea: {
    alignItems: "center",
    paddingVertical: 32,
    gap: 12,
  },
  placeholderTitle: {
    fontSize: 18,
    fontWeight: "600",
    color: colors.primary,
  },
  placeholderDesc: {
    fontSize: 13,
    color: colors.textMuted,
    textAlign: "center",
    lineHeight: 20,
  },
  fieldGroup: {
    gap: 8,
  },
  label: {
    fontSize: 12,
    color: colors.textMuted,
    letterSpacing: 1,
  },
  input: {
    backgroundColor: colors.surface,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 12,
    fontSize: 14,
    color: colors.textPrimary,
  },
  textArea: {
    height: 140,
    paddingTop: 12,
  },
  footer: {
    paddingHorizontal: 24,
    paddingTop: 12,
    backgroundColor: colors.background,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: colors.border,
  },
});
