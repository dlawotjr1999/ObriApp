import React from "react";
import {
  View,
  Text,
  ScrollView,
  StyleSheet,
} from "react-native";
import { SafeAreaView, useSafeAreaInsets } from "react-native-safe-area-context";
import { useLocalSearchParams } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { getMockPostById } from "@/mocks/posts";
import { MOCK_USER, MY_POST_IDS } from "@/mocks/user";
import { MOCK_APPLICATIONS } from "@/mocks/applications";
import { formatEventDateTime } from "@/utils/datetime";
import { formatKRW } from "@/utils/number";
import ScreenHeader from "@/components/common/ScreenHeader";
import EmptyState from "@/components/common/EmptyState";
import IconText from "@/components/common/IconText";
import Tag from "@/components/common/Tag";
import ThemedButton from "@/components/common/ThemedButton";

function Section({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <View style={styles.section}>
      <Text style={styles.sectionTitle}>{title}</Text>
      {children}
    </View>
  );
}

export default function PostDetailScreen() {
  const { id } = useLocalSearchParams<{ id: string }>();
  const insets = useSafeAreaInsets();
  // TODO: 구인글 단건 조회 API(GET /api/posts/{id}) 연동 (임시 더미 조회)
  const post = getMockPostById(Number(id));

  if (!post) {
    return (
      <SafeAreaView style={styles.container} edges={["top"]}>
        <View style={styles.headerArea}>
          <ScreenHeader />
        </View>
        <EmptyState
          icon="alert-circle-outline"
          title="구인글을 찾을 수 없어요"
          description="삭제되었거나 존재하지 않는 구인글입니다."
        />
      </SafeAreaView>
    );
  }

  // 지원 대상 악기는 선택형이 아니라 내 프로필 악기(user.getInstrument())로 서버가 자동 판정한다
  // (AppRequestDTO엔 postId/additionalInfo뿐, 악기 필드 없음 — 별도 선택 UI 불필요)
  const myInstrumentSlot = post.instruments.find((it) => it.instrument === MOCK_USER.instrument);
  const instrumentClosed = !!myInstrumentSlot && myInstrumentSlot.currentPeople >= myInstrumentSlot.people;

  const isMyPost = MY_POST_IDS.includes(post.id);
  const hasApplied = MOCK_APPLICATIONS.some((a) => a.post.id === post.id);
  const eventPassed = new Date(post.eventAt) < new Date();
  const isClosed = post.status === "CLOSED";

  // 버튼 비활성 우선순위는 ApplicationService.submitApplication의 검증 순서와 동일하게 맞춘다:
  // 마감글 → 공연종료 → 내 악기 정원마감 → 본인 글 → 중복 지원
  let applyLabel = "지원하기";
  let applyDisabled = false;
  if (isClosed) {
    applyLabel = "마감된 구인글";
    applyDisabled = true;
  } else if (eventPassed) {
    applyLabel = "종료된 공연";
    applyDisabled = true;
  } else if (instrumentClosed) {
    applyLabel = "정원이 마감된 악기";
    applyDisabled = true;
  } else if (isMyPost) {
    applyLabel = "내가 등록한 구인글";
    applyDisabled = true;
  } else if (hasApplied) {
    applyLabel = "이미 지원한 구인글";
    applyDisabled = true;
  }

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <View style={styles.headerArea}>
        <ScreenHeader />
      </View>

      <ScrollView
        contentContainerStyle={[styles.scrollContent, { paddingBottom: 120 }]}
        showsVerticalScrollIndicator={false}
      >
        {/* 히어로: 썸네일 + 카테고리 + 제목 */}
        <View style={styles.hero}>
          <View style={styles.thumbnail}>
            <Ionicons name="musical-note" size={36} color={colors.primaryLight} />
          </View>
          <View style={styles.categoryRow}>
            <Tag label={post.category} variant="filled" />
            {isClosed && <Tag label="마감" />}
          </View>
          <Text style={styles.title}>{post.title}</Text>

          {/* 작성자 */}
          <View style={styles.writerRow}>
            <Ionicons name="person-circle-outline" size={18} color={colors.textMuted} />
            <Text style={styles.writerText}>
              {post.writer.nickname} · {post.writer.instrument}
            </Text>
            <View style={styles.mannerBadge}>
              <Ionicons name="star" size={12} color={colors.primaryLight} />
              <Text style={styles.mannerText}>{post.writer.mannerScore.toFixed(1)}</Text>
            </View>
          </View>
        </View>

        <View style={styles.divider} />

        {/* 기본 정보 */}
        <Section title="기본 정보">
          <View style={styles.infoList}>
            <IconText icon="calendar-outline" text={formatEventDateTime(post.eventAt)} />
            <IconText icon="location-outline" text={post.location} />
            <IconText icon="cash-outline" text={formatKRW(post.pay)} />
          </View>
        </Section>

        {/* 모집 악기 — 내 프로필 악기와 일치하는 항목을 강조 표시 */}
        <Section title="모집 악기">
          <View style={styles.tagRow}>
            {post.instruments.map((it) => (
              <Tag
                key={it.instrument}
                label={`${it.instrument} ${it.currentPeople}/${it.people}`}
                variant={it.instrument === MOCK_USER.instrument ? "filled" : "outline"}
              />
            ))}
          </View>
        </Section>

        {/* 시간표 */}
        <Section title="시간표">
          <Text style={styles.bodyText}>{post.timetable}</Text>
        </Section>

        {/* 설명 */}
        {post.description ? (
          <Section title="설명">
            <Text style={styles.bodyText}>{post.description}</Text>
          </Section>
        ) : null}
      </ScrollView>

      {/* 하단 고정: 지원하기 (우측) */}
      <View style={[styles.footer, { paddingBottom: insets.bottom + 12 }]}>
        <ThemedButton
          title={applyLabel}
          disabled={applyDisabled}
          style={styles.applyButton}
          onPress={() => {
            // TODO: 오브리 지원 API(POST /api/posts/{id}/applications) 연동
          }}
        />
      </View>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  headerArea: {
    paddingHorizontal: 24,
    paddingTop: 8,
  },
  scrollContent: {
    paddingHorizontal: 24,
  },
  hero: {
    alignItems: "flex-start",
  },
  thumbnail: {
    width: 72,
    height: 72,
    borderRadius: 16,
    backgroundColor: colors.surface,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    alignItems: "center",
    justifyContent: "center",
    marginBottom: 16,
  },
  categoryRow: {
    flexDirection: "row",
    gap: 6,
    marginBottom: 10,
  },
  title: {
    fontSize: 22,
    fontWeight: "700",
    color: colors.textPrimary,
    lineHeight: 30,
  },
  writerRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 6,
    marginTop: 12,
  },
  writerText: {
    fontSize: 13,
    color: colors.textSecondary,
  },
  mannerBadge: {
    flexDirection: "row",
    alignItems: "center",
    gap: 2,
    marginLeft: 2,
  },
  mannerText: {
    fontSize: 12,
    color: colors.textSecondary,
    fontWeight: "500",
  },
  divider: {
    height: StyleSheet.hairlineWidth,
    backgroundColor: colors.border,
    marginVertical: 24,
  },
  section: {
    marginBottom: 24,
  },
  sectionTitle: {
    fontSize: 12,
    color: colors.textMuted,
    letterSpacing: 1,
    marginBottom: 12,
  },
  infoList: {
    gap: 10,
  },
  tagRow: {
    flexDirection: "row",
    flexWrap: "wrap",
    gap: 8,
  },
  bodyText: {
    fontSize: 14,
    color: colors.textSecondary,
    lineHeight: 22,
  },
  footer: {
    position: "absolute",
    left: 0,
    right: 0,
    bottom: 0,
    paddingHorizontal: 24,
    paddingTop: 12,
    backgroundColor: colors.background,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: colors.border,
    alignItems: "flex-end",
  },
  applyButton: {
    height: 48,
    paddingHorizontal: 40,
    borderRadius: 24,
  },
});
