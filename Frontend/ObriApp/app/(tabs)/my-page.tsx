import React, { useCallback, useEffect, useState } from "react";
import { View, ScrollView, Text, StyleSheet, ActivityIndicator } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { colors } from "@/constants/theme";
import { MOCK_USER, MY_POST_IDS } from "@/mocks/user";
import { MOCK_POSTS } from "@/mocks/posts";
import { getMyApplications } from "@/api/application";
import { ApiError } from "@/lib/apiClient";
import { ApplicationSummary } from "@/types/application";
import AppHeader from "@/components/common/AppHeader";
import PostCard from "@/components/post/PostCard";
import ProfileSection from "@/components/myPage/ProfileSection";
import TabbedPager from "@/components/myPage/TabbedPager";
import ApplicationCard from "@/components/myPage/ApplicationCard";
import SettingsSection from "@/components/myPage/SettingsSection";

const TABS = [
  { key: "posts", label: "내 모집글" },
  { key: "applications", label: "내 지원" },
];

// "내 모집글" 탭은 아직 mock(MOCK_POSTS·MY_POST_IDS) 그대로다 — post 도메인 getMyPosts() 연결은
// 이 작업(application 도메인) 범위 밖이라 손대지 않았다. "내 지원" 탭만 실 API로 연결한다.
export default function MyPageScreen() {
  const router = useRouter();
  const [notifEnabled, setNotifEnabled] = useState(true);

  const user = MOCK_USER;
  const myPosts = MOCK_POSTS.filter((p) => MY_POST_IDS.includes(p.id));

  const [applications, setApplications] = useState<ApplicationSummary[]>([]);
  const [applicationsLoading, setApplicationsLoading] = useState(true);
  const [applicationsError, setApplicationsError] = useState<string | null>(null);

  // 마이페이지는 이 화면이 무한스크롤 구조가 아니라(바깥이 이미 ScrollView) 첫 페이지만 조회한다.
  // 지원 건수가 페이지 크기(10)를 넘는 경우는 아직 드물다고 보고 후순위로 미룸.
  const loadApplications = useCallback(async () => {
    setApplicationsLoading(true);
    setApplicationsError(null);
    try {
      const page = await getMyApplications(0);
      setApplications(page.content);
    } catch (err) {
      setApplicationsError(err instanceof ApiError ? err.message : "지원 목록을 불러오지 못했어요.");
    } finally {
      setApplicationsLoading(false);
    }
  }, []);

  useEffect(() => {
    loadApplications();
  }, [loadApplications]);

  const acceptedCount = applications.filter((a) => a.status === "ACCEPTED").length;

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <AppHeader />
      <ScrollView showsVerticalScrollIndicator={false}>
        <ProfileSection
          user={user}
          myPostCount={myPosts.length}
          totalApplications={applications.length}
          acceptedApplications={acceptedCount}
          onEditPress={() => router.push("/my-page/edit")}
        />

        <TabbedPager
          tabs={TABS}
          pages={[
            myPosts.length === 0 ? (
              <Text style={styles.emptyText}>등록한 모집글이 없어요.</Text>
            ) : (
              myPosts.map((post, i) => (
                <View key={post.id} style={i > 0 ? { marginTop: 12 } : undefined}>
                  <PostCard
                    post={post}
                    onPress={(id) => router.push({ pathname: "/post/[id]", params: { id } })}
                  />
                </View>
              ))
            ),
            applicationsLoading ? (
              <ActivityIndicator style={styles.tabSpinner} color={colors.primary} />
            ) : applicationsError ? (
              <Text style={styles.emptyText}>{applicationsError}</Text>
            ) : applications.length === 0 ? (
              <Text style={styles.emptyText}>지원한 모집글이 없어요.</Text>
            ) : (
              applications.map((app, i) => (
                <View key={app.id} style={i > 0 ? { marginTop: 12 } : undefined}>
                  <ApplicationCard item={app} />
                </View>
              ))
            ),
          ]}
        />

        <SettingsSection
          notifEnabled={notifEnabled}
          onToggleNotif={setNotifEnabled}
          onLogout={() => {
            // TODO: Firebase 로그아웃 처리 후 (auth)/login으로 이동
          }}
          onWithdraw={() => {
            // TODO: 회원탈퇴 API(DELETE /api/users/me) 연동 후 (auth)/login으로 이동
          }}
        />

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
  emptyText: {
    textAlign: "center",
    color: colors.textMuted,
    fontSize: 14,
    marginTop: 40,
  },
  tabSpinner: {
    marginTop: 40,
  },
  versionText: {
    textAlign: "center",
    fontSize: 12,
    color: colors.textMuted,
    marginTop: 16,
  },
});
