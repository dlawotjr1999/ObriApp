import React, { useState, useRef } from "react";
import {
  View,
  ScrollView,
  Text,
  TouchableOpacity,
  StyleSheet,
  Alert,
  Switch,
  Animated,
  NativeSyntheticEvent,
  NativeScrollEvent,
  useWindowDimensions,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { MOCK_USER, MY_POST_IDS } from "@/mocks/user";
import { MOCK_POSTS } from "@/mocks/posts";
import { MOCK_APPLICATIONS } from "@/mocks/applications";
import AppHeader from "@/components/common/AppHeader";
import PostCard from "@/components/post/PostCard";
import IconText from "@/components/common/IconText";
import { formatEventDateTime } from "@/utils/datetime";
import { ApplicationSummary, ApplicationStatus } from "@/types/application";

const TABS = [
  { key: "posts", label: "내 구인글" },
  { key: "applications", label: "내 지원" },
] as const;

const STATUS_LABEL: Record<ApplicationStatus, string> = {
  PENDING: "검토 중",
  ACCEPTED: "수락됨",
  REJECTED: "거절됨",
  CANCELLED: "취소됨",
  REVOKED: "철회됨",
};

const STATUS_COLOR: Record<ApplicationStatus, string> = {
  PENDING: "#B8860B",
  ACCEPTED: "#2E7D32",
  REJECTED: "#C0392B",
  CANCELLED: colors.textMuted,
  REVOKED: colors.textMuted,
};

function ApplicationCard({ item }: { item: ApplicationSummary }) {
  return (
    <View style={styles.appCard}>
      <View style={styles.appCardHeader}>
        <View style={styles.categoryBadge}>
          <Text style={styles.categoryText}>{item.post.category}</Text>
        </View>
        <View style={[styles.statusBadge, { borderColor: STATUS_COLOR[item.status] }]}>
          <Text style={[styles.statusText, { color: STATUS_COLOR[item.status] }]}>
            {STATUS_LABEL[item.status]}
          </Text>
        </View>
      </View>
      <Text style={styles.appCardTitle} numberOfLines={1}>
        {item.post.title}
      </Text>
      <View style={styles.appCardMeta}>
        <IconText icon="calendar-outline" text={formatEventDateTime(item.post.eventAt)} />
        <IconText icon="location-outline" text={item.post.location} />
      </View>
    </View>
  );
}

export default function MyPageScreen() {
  const router = useRouter();
  const { width: screenWidth } = useWindowDimensions();
  const pageRef = useRef<ScrollView>(null);
  const indicatorAnim = useRef(new Animated.Value(0)).current;
  const [activeIndex, setActiveIndex] = useState(0);
  const [notifEnabled, setNotifEnabled] = useState(true);

  const tabWidth = (screenWidth - 32) / TABS.length;
  const user = MOCK_USER;
  const myPosts = MOCK_POSTS.filter((p) => MY_POST_IDS.includes(p.id));

  const totalApplications = MOCK_APPLICATIONS.length;
  const acceptedApplications = MOCK_APPLICATIONS.filter((a) => a.status === "ACCEPTED").length;
  const myPostCount = myPosts.length;

  function onTabPress(index: number) {
    setActiveIndex(index);
    pageRef.current?.scrollTo({ x: index * screenWidth, animated: true });
    Animated.spring(indicatorAnim, {
      toValue: index * tabWidth,
      useNativeDriver: true,
      tension: 180,
      friction: 20,
    }).start();
  }

  function onPageScroll(e: NativeSyntheticEvent<NativeScrollEvent>) {
    const x = e.nativeEvent.contentOffset.x;
    const index = Math.round(x / screenWidth);
    if (index !== activeIndex && (index === 0 || index === 1)) {
      setActiveIndex(index);
      Animated.spring(indicatorAnim, {
        toValue: index * tabWidth,
        useNativeDriver: true,
        tension: 180,
        friction: 20,
      }).start();
    }
  }

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <AppHeader />
      <ScrollView showsVerticalScrollIndicator={false}>
        {/* 프로필 섹션 */}
        <View style={styles.profileSection}>
          <View style={styles.profileRow}>
            <View style={styles.avatar}>
              <Ionicons name="musical-note" size={28} color={colors.primary} />
            </View>
            <View style={styles.profileInfo}>
                <Text style={styles.nickname}>{user.nickname}</Text>
              <Text style={styles.profileMeta}>
                {user.instrument} · {user.school} ·{" "}
                {user.isGraduate ? "졸업생" : "재학생"}
              </Text>
            </View>
            <TouchableOpacity
              style={styles.editButton}
              onPress={() => router.push("/my-page/edit")}
              activeOpacity={0.7}
            >
              <Ionicons name="pencil-outline" size={16} color={colors.textSecondary} />
            </TouchableOpacity>
          </View>

          {/* 활동 요약 */}
          <View style={styles.statsRow}>
            <View style={styles.statItem}>
              <Text style={styles.statValue}>{myPostCount}</Text>
              <Text style={styles.statLabel}>내 구인글</Text>
            </View>
            <View style={styles.statDivider} />
            <View style={styles.statItem}>
              <Text style={styles.statValue}>{totalApplications}</Text>
              <Text style={styles.statLabel}>총 지원</Text>
            </View>
            <View style={styles.statDivider} />
            <View style={styles.statItem}>
              <Text style={styles.statValue}>{acceptedApplications}</Text>
              <Text style={styles.statLabel}>수락됨</Text>
            </View>
          </View>

          {/* 경력 */}
          {user.careers.length > 0 && (
            <View style={styles.careerSection}>
              <Text style={styles.careerLabel}>경력</Text>
              {user.careers.map((c) => (
                <View key={c.id} style={styles.careerItem}>
                  <View style={styles.careerDot} />
                  <View style={{ flex: 1 }}>
                    <Text style={styles.careerOrg}>{c.organization}</Text>
                    <Text style={styles.careerContext}>{c.contexts}</Text>
                  </View>
                </View>
              ))}
            </View>
          )}
        </View>

        {/* 탭 바 */}
        <View style={[styles.tabBar, { marginHorizontal: 16 }]}>
          {TABS.map((tab, index) => (
            <TouchableOpacity
              key={tab.key}
              style={styles.tab}
              onPress={() => onTabPress(index)}
              activeOpacity={0.7}
            >
              <Text
                style={[
                  styles.tabText,
                  activeIndex === index && styles.tabTextActive,
                ]}
              >
                {tab.label}
              </Text>
            </TouchableOpacity>
          ))}
          <Animated.View
            style={[
              styles.tabIndicator,
              { width: tabWidth, transform: [{ translateX: indicatorAnim }] },
            ]}
          />
        </View>

        {/* 수평 스와이프 페이저 */}
        <ScrollView
          ref={pageRef}
          horizontal
          pagingEnabled
          showsHorizontalScrollIndicator={false}
          scrollEventThrottle={16}
          onScroll={onPageScroll}
          style={styles.pager}
        >
          {/* 내 구인글 */}
          <View style={[styles.page, { width: screenWidth }]}>
            {myPosts.length === 0 ? (
              <Text style={styles.emptyText}>등록한 구인글이 없어요.</Text>
            ) : (
              myPosts.map((post, i) => (
                <View key={post.id} style={i > 0 ? { marginTop: 12 } : undefined}>
                  <PostCard
                    post={post}
                    onPress={(id) =>
                      router.push({ pathname: "/post/[id]", params: { id } })
                    }
                  />
                </View>
              ))
            )}
          </View>

          {/* 내 지원 */}
          <View style={[styles.page, { width: screenWidth }]}>
            {MOCK_APPLICATIONS.length === 0 ? (
              <Text style={styles.emptyText}>지원한 구인글이 없어요.</Text>
            ) : (
              MOCK_APPLICATIONS.map((app, i) => (
                <View key={app.id} style={i > 0 ? { marginTop: 12 } : undefined}>
                  <ApplicationCard item={app} />
                </View>
              ))
            )}
          </View>
        </ScrollView>

        {/* 설정 */}
        <View style={styles.settingsSection}>
          {/* 알림 설정 */}
          <View style={styles.settingsRow}>
            <View style={styles.settingsLeft}>
              <Ionicons name="notifications-outline" size={16} color={colors.textSecondary} />
              <Text style={styles.settingsText}>구인 알림</Text>
            </View>
            <Switch
              value={notifEnabled}
              onValueChange={setNotifEnabled}
              trackColor={{ false: colors.border, true: colors.primaryLight }}
              thumbColor={notifEnabled ? colors.primary : colors.placeholder}
            />
          </View>
          <View style={styles.divider} />

          {/* 이용약관 */}
          <TouchableOpacity style={styles.settingsRow} activeOpacity={0.7}>
            <View style={styles.settingsLeft}>
              <Ionicons name="document-text-outline" size={16} color={colors.textSecondary} />
              <Text style={styles.settingsText}>이용약관</Text>
            </View>
            <Ionicons name="chevron-forward" size={16} color={colors.textMuted} />
          </TouchableOpacity>
          <View style={styles.divider} />

          {/* 개인정보처리방침 */}
          <TouchableOpacity style={styles.settingsRow} activeOpacity={0.7}>
            <View style={styles.settingsLeft}>
              <Ionicons name="lock-closed-outline" size={16} color={colors.textSecondary} />
              <Text style={styles.settingsText}>개인정보처리방침</Text>
            </View>
            <Ionicons name="chevron-forward" size={16} color={colors.textMuted} />
          </TouchableOpacity>
          <View style={styles.divider} />

          {/* 로그아웃 */}
          <TouchableOpacity
            style={styles.settingsRow}
            onPress={() =>
              Alert.alert("로그아웃", "로그아웃하시겠어요?", [
                { text: "취소", style: "cancel" },
                { text: "로그아웃", style: "destructive", onPress: () => {} },
              ])
            }
            activeOpacity={0.7}
          >
            <View style={styles.settingsLeft}>
              <Ionicons name="log-out-outline" size={16} color={colors.textSecondary} />
              <Text style={styles.settingsText}>로그아웃</Text>
            </View>
            <Ionicons name="chevron-forward" size={16} color={colors.textMuted} />
          </TouchableOpacity>
          <View style={styles.divider} />

          {/* 회원탈퇴 */}
          <TouchableOpacity
            style={styles.settingsRow}
            onPress={() =>
              Alert.alert("회원탈퇴", "정말 탈퇴하시겠어요?", [
                { text: "취소", style: "cancel" },
                { text: "탈퇴", style: "destructive", onPress: () => {} },
              ])
            }
            activeOpacity={0.7}
          >
            <View style={styles.settingsLeft}>
              <Ionicons name="person-remove-outline" size={16} color="#C0392B" />
              <Text style={[styles.settingsText, styles.settingsDanger]}>회원탈퇴</Text>
            </View>
            <Ionicons name="chevron-forward" size={16} color={colors.textMuted} />
          </TouchableOpacity>
        </View>

        {/* 앱 버전 */}
        <Text style={styles.versionText}>v0.1.0</Text>

        <View style={{ height: 40 }} />
      </ScrollView>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },

  /* 프로필 */
  profileSection: {
    backgroundColor: colors.surface,
    margin: 16,
    borderRadius: 16,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    padding: 16,
  },
  profileRow: {
    flexDirection: "row",
    alignItems: "center",
    gap: 14,
  },
  avatar: {
    width: 56,
    height: 56,
    borderRadius: 28,
    backgroundColor: colors.background,
    alignItems: "center",
    justifyContent: "center",
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
  },
  profileInfo: {
    flex: 1,
    gap: 4,
  },
  nickname: {
    fontSize: 17,
    fontWeight: "700",
    color: colors.textPrimary,
  },
  profileMeta: {
    fontSize: 13,
    color: colors.textMuted,
  },
  editButton: {
    padding: 6,
  },

  /* 활동 요약 */
  statsRow: {
    flexDirection: "row",
    marginTop: 16,
    paddingTop: 14,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: colors.border,
  },
  statItem: {
    flex: 1,
    alignItems: "center",
    gap: 3,
  },
  statValue: {
    fontSize: 18,
    fontWeight: "700",
    color: colors.textPrimary,
  },
  statLabel: {
    fontSize: 11,
    color: colors.textMuted,
  },
  statDivider: {
    width: StyleSheet.hairlineWidth,
    backgroundColor: colors.border,
    marginVertical: 4,
  },

  /* 경력 */
  careerSection: {
    marginTop: 16,
    paddingTop: 14,
    borderTopWidth: StyleSheet.hairlineWidth,
    borderTopColor: colors.border,
    gap: 10,
  },
  careerLabel: {
    fontSize: 12,
    fontWeight: "600",
    color: colors.textMuted,
    letterSpacing: 0.5,
    marginBottom: 2,
  },
  careerItem: {
    flexDirection: "row",
    alignItems: "flex-start",
    gap: 10,
  },
  careerDot: {
    width: 6,
    height: 6,
    borderRadius: 3,
    backgroundColor: colors.primaryLight,
    marginTop: 5,
  },
  careerOrg: {
    fontSize: 14,
    fontWeight: "600",
    color: colors.textPrimary,
  },
  careerContext: {
    fontSize: 12,
    color: colors.textMuted,
    marginTop: 1,
  },

  /* 탭 바 */
  tabBar: {
    flexDirection: "row",
    backgroundColor: colors.surface,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    borderRadius: 10,
    overflow: "hidden",
    position: "relative",
  },
  tab: {
    flex: 1,
    paddingVertical: 11,
    alignItems: "center",
  },
  tabText: {
    fontSize: 14,
    fontWeight: "500",
    color: colors.textMuted,
  },
  tabTextActive: {
    color: colors.primary,
    fontWeight: "700",
  },
  tabIndicator: {
    position: "absolute",
    bottom: 0,
    left: 0,
    height: 2,
    backgroundColor: colors.primary,
    borderRadius: 1,
  },

  /* 페이저 */
  pager: {
    marginTop: 12,
    minHeight: 400,
  },
  page: {
    padding: 16,
    paddingTop: 4,
  },
  emptyText: {
    textAlign: "center",
    color: colors.textMuted,
    fontSize: 14,
    marginTop: 40,
  },

  /* 지원 카드 */
  appCard: {
    backgroundColor: colors.surface,
    borderRadius: 14,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    padding: 14,
    gap: 8,
  },
  appCardHeader: {
    flexDirection: "row",
    alignItems: "center",
    gap: 8,
  },
  categoryBadge: {
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 6,
    backgroundColor: colors.background,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
  },
  categoryText: {
    fontSize: 11,
    color: colors.textSecondary,
    fontWeight: "600",
  },
  statusBadge: {
    paddingHorizontal: 8,
    paddingVertical: 3,
    borderRadius: 6,
    borderWidth: 1,
  },
  statusText: {
    fontSize: 11,
    fontWeight: "700",
  },
  appCardTitle: {
    fontSize: 15,
    fontWeight: "600",
    color: colors.textPrimary,
  },
  appCardMeta: {
    gap: 3,
  },

  /* 설정 */
  settingsSection: {
    marginHorizontal: 16,
    marginTop: 8,
    backgroundColor: colors.surface,
    borderRadius: 12,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    overflow: "hidden",
  },
  settingsRow: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingHorizontal: 16,
    paddingVertical: 13,
  },
  settingsLeft: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
  },
  settingsText: {
    fontSize: 14,
    color: colors.textSecondary,
  },
  settingsDanger: {
    color: "#C0392B",
  },
  divider: {
    height: StyleSheet.hairlineWidth,
    backgroundColor: colors.border,
    marginHorizontal: 16,
  },

  /* 버전 */
  versionText: {
    textAlign: "center",
    fontSize: 12,
    color: colors.textMuted,
    marginTop: 16,
  },
});
