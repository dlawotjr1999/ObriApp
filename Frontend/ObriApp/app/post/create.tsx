import React, { useRef, useState } from "react";
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  ScrollView,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  Alert,
} from "react-native";
import { SafeAreaView, useSafeAreaInsets } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { CATEGORIES, REGIONS } from "@/constants/filterOptions";
import { createPost } from "@/api/post";
import { ApiError } from "@/lib/apiClient";
import { PostCreateRequest } from "@/types/post";
import ThemedButton from "@/components/common/ThemedButton";
import ChipSelect from "@/components/common/ChipSelect";
import PostInstrumentFormItem, {
  PostInstrumentDraft,
} from "@/components/post/PostInstrumentFormItem";

export default function PostCreateScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();

  const [category, setCategory] = useState("");
  const [title, setTitle] = useState("");
  const [eventDate, setEventDate] = useState("");
  const [eventTime, setEventTime] = useState("");
  const [location, setLocation] = useState("");
  const [region, setRegion] = useState("");
  const [timetable, setTimetable] = useState("");
  const [submitting, setSubmitting] = useState(false);

  // 배열 index는 항목 삭제 시 뒤 요소가 앞으로 당겨져 재사용되므로,
  // React key로 쓰기 위한 항목별 안정적인 로컬 id를 별도로 관리한다.
  const keyCounter = useRef(0);
  const [instrumentKeys, setInstrumentKeys] = useState<number[]>(() => [keyCounter.current++]);
  const [instruments, setInstruments] = useState<PostInstrumentDraft[]>([
    { instrument: "", people: "" },
  ]);

  const handleInstrumentChange = (index: number, entry: PostInstrumentDraft) => {
    const updated = [...instruments];
    updated[index] = entry;
    setInstruments(updated);
  };

  const handleInstrumentRemove = (index: number) => {
    setInstruments((prev) => prev.filter((_, i) => i !== index));
    setInstrumentKeys((prev) => prev.filter((_, i) => i !== index));
  };

  const handleInstrumentAdd = () => {
    setInstruments((prev) => [...prev, { instrument: "", people: "" }]);
    setInstrumentKeys((prev) => [...prev, keyCounter.current++]);
  };

  // 모집글 등록. POST는 멱등이 아니라(같은 요청을 두 번 보내면 글이 두 개 생김) submitting 플래그로
  // 버튼을 잠가 연속 탭에 의한 중복 등록을 막는다. 실패해도 이 화면에 그대로 남아 재시도할 수 있게
  // router.back()은 성공 시에만 호출한다.
  const handleSubmit = async () => {
    if (submitting) return;
    setSubmitting(true);
    try {
      const payload: PostCreateRequest = {
        category,
        title,
        eventAt: `${eventDate}T${eventTime}:00`,
        location,
        region,
        timetable,
        instruments: instruments
          .filter((it) => it.instrument && it.people)
          .map(({ instrument, people }) => ({ instrument, people: Number(people) })),
      };
      await createPost(payload);
      router.back();
    } catch (err) {
      Alert.alert(
        "등록 실패",
        err instanceof ApiError ? err.message : "잠시 후 다시 시도해주세요."
      );
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      {/* 헤더 */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => router.back()} hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}>
          <Ionicons name="arrow-back" size={22} color={colors.primary} />
        </TouchableOpacity>
        <Text style={styles.headerTitle}>모집글 작성</Text>
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
          <ChipSelect
            label="카테고리"
            options={CATEGORIES}
            selected={category}
            onSelect={setCategory}
          />

          <View style={styles.fieldGroup}>
            <Text style={styles.label}>제목</Text>
            <TextInput
              style={styles.input}
              placeholder="모집글 제목을 입력하세요"
              placeholderTextColor={colors.placeholder}
              value={title}
              onChangeText={setTitle}
            />
          </View>

          <View style={styles.row}>
            <View style={[styles.fieldGroup, styles.rowField]}>
              <Text style={styles.label}>공연 날짜</Text>
              <TextInput
                style={styles.input}
                placeholder="2026-08-01"
                placeholderTextColor={colors.placeholder}
                value={eventDate}
                onChangeText={setEventDate}
              />
            </View>
            <View style={[styles.fieldGroup, styles.rowField]}>
              <Text style={styles.label}>공연 시간</Text>
              <TextInput
                style={styles.input}
                placeholder="14:00"
                placeholderTextColor={colors.placeholder}
                value={eventTime}
                onChangeText={setEventTime}
              />
            </View>
          </View>

          <View style={styles.fieldGroup}>
            <Text style={styles.label}>장소</Text>
            <TextInput
              style={styles.input}
              placeholder="예: 서울 강남구 OO스튜디오"
              placeholderTextColor={colors.placeholder}
              value={location}
              onChangeText={setLocation}
            />
          </View>

          <ChipSelect
            label="지역"
            options={REGIONS}
            selected={region}
            onSelect={setRegion}
          />

          <View style={styles.fieldGroup}>
            <Text style={styles.label}>시간표</Text>
            <TextInput
              style={[styles.input, styles.textArea]}
              placeholder="예: 매주 토요일 오후 2시 합주"
              placeholderTextColor={colors.placeholder}
              value={timetable}
              onChangeText={setTimetable}
              multiline
              numberOfLines={4}
              textAlignVertical="top"
            />
          </View>

          <View style={styles.fieldGroup}>
            <Text style={styles.label}>모집 악기</Text>
            {instruments.map((it, index) => (
              <PostInstrumentFormItem
                key={instrumentKeys[index]}
                value={it}
                index={index}
                onChange={handleInstrumentChange}
                onRemove={handleInstrumentRemove}
                removable={instruments.length > 1}
              />
            ))}
            <TouchableOpacity style={styles.addButton} onPress={handleInstrumentAdd}>
              <Ionicons name="add" size={18} color={colors.textMuted} />
              <Text style={styles.addButtonText}>악기 추가</Text>
            </TouchableOpacity>
          </View>
        </ScrollView>

        {/* 하단 등록 버튼 */}
        <View style={[styles.footer, { paddingBottom: insets.bottom + 12 }]}>
          <ThemedButton
            title={submitting ? "등록 중..." : "등록하기"}
            onPress={handleSubmit}
            disabled={submitting}
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
    gap: 8,
  },
  row: {
    flexDirection: "row",
    gap: 12,
  },
  rowField: {
    flex: 1,
  },
  fieldGroup: {
    gap: 8,
    marginBottom: 16,
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
    height: 90,
    paddingTop: 12,
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
  },
  addButtonText: {
    fontSize: 13,
    color: colors.textMuted,
  },
  footer: {
    paddingHorizontal: 24,
    paddingTop: 12,
    backgroundColor: colors.background,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: colors.border,
  },
});
