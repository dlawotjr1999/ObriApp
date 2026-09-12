import React, { useState } from "react";
import { View, Text, TextInput, TouchableOpacity, StyleSheet, Modal } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import ThemedButton from "@/components/common/ThemedButton";

interface ApplicationSubmitModalProps {
  visible: boolean;
  postTitle: string;
  // 제출 API(POST /api/applications/submit) 호출은 부모(post/[id].tsx)가 담당한다 — 이 모달은
  // 입력값만 모아 onSubmit으로 넘기고, 성공/실패에 따른 화면 갱신은 부모 책임.
  submitting: boolean;
  onSubmit: (additionalInfo: string) => void;
  onClose: () => void;
}

// 지원 제출 전 어필 문구(additionalInfo)를 입력받는 모달. AppRequestDTO엔 postId·additionalInfo뿐이라
// 악기 선택 같은 다른 입력은 없다(서버가 프로필 악기로 자동 판정).
export default function ApplicationSubmitModal({
  visible,
  postTitle,
  submitting,
  onSubmit,
  onClose,
}: ApplicationSubmitModalProps) {
  const [additionalInfo, setAdditionalInfo] = useState("");

  // 모달이 열릴 때마다 이전 입력을 지운다 (다른 글에 다시 열었을 때 이전 문구가 남아있지 않도록)
  function handleShow() {
    setAdditionalInfo("");
  }

  return (
    <Modal visible={visible} transparent animationType="fade" onShow={handleShow} onRequestClose={onClose}>
      <View style={styles.wrapper}>
        <TouchableOpacity style={StyleSheet.absoluteFill} activeOpacity={1} onPress={onClose} />

        <View style={styles.container}>
          <TouchableOpacity style={styles.closeButton} onPress={onClose}>
            <Ionicons name="close" size={14} color={colors.background} />
          </TouchableOpacity>

          <View style={styles.sheet}>
            <View style={styles.card}>
              <Text style={styles.title} numberOfLines={2}>
                지원하기
              </Text>
              <Text style={styles.subtitle} numberOfLines={1}>
                {postTitle}
              </Text>

              <View style={styles.divider} />

              <Text style={styles.label}>어필 문구 (선택)</Text>
              <TextInput
                style={styles.input}
                placeholder="모집자에게 전달할 소개나 어필 문구를 남겨보세요"
                placeholderTextColor={colors.placeholder}
                value={additionalInfo}
                onChangeText={setAdditionalInfo}
                multiline
                numberOfLines={4}
                textAlignVertical="top"
              />

              <ThemedButton
                title={submitting ? "제출 중..." : "제출하기"}
                onPress={() => onSubmit(additionalInfo)}
                disabled={submitting}
                style={styles.submitButton}
              />
            </View>
          </View>
        </View>
      </View>
    </Modal>
  );
}

const styles = StyleSheet.create({
  wrapper: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: "rgba(0,0,0,0.4)",
    padding: 28,
  },
  container: {
    width: "100%",
  },
  closeButton: {
    position: "absolute",
    top: -8,
    right: -8,
    width: 28,
    height: 28,
    borderRadius: 14,
    backgroundColor: colors.primary,
    justifyContent: "center",
    alignItems: "center",
    zIndex: 1,
  },
  sheet: {
    backgroundColor: colors.background,
    borderRadius: 20,
    padding: 20,
  },
  card: {
    backgroundColor: colors.surface,
    borderRadius: 14,
    padding: 16,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
  },
  title: {
    fontSize: 16,
    fontWeight: "700",
    color: colors.primary,
  },
  subtitle: {
    fontSize: 13,
    color: colors.textSecondary,
    marginTop: 4,
  },
  divider: {
    height: StyleSheet.hairlineWidth,
    backgroundColor: colors.border,
    marginVertical: 14,
  },
  label: {
    fontSize: 12,
    color: colors.textMuted,
    letterSpacing: 1,
    marginBottom: 8,
  },
  input: {
    backgroundColor: colors.background,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    borderRadius: 10,
    paddingHorizontal: 14,
    paddingVertical: 12,
    height: 100,
    fontSize: 14,
    color: colors.textPrimary,
  },
  submitButton: {
    marginTop: 16,
  },
});
