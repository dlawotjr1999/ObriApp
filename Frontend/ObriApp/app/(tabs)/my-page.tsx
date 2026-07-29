import React, { useState } from "react";
import { View, ScrollView, Text, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { colors } from "@/constants/theme";
import { MOCK_USER, MY_POST_IDS } from "@/mocks/user";
import { MOCK_POSTS } from "@/mocks/posts";
import { MOCK_APPLICATIONS } from "@/mocks/applications";
import AppHeader from "@/components/common/AppHeader";
import PostCard from "@/components/post/PostCard";
import ProfileSection from "@/components/myPage/ProfileSection";
import TabbedPager from "@/components/myPage/TabbedPager";
import ApplicationCard from "@/components/myPage/ApplicationCard";
import SettingsSection from "@/components/myPage/SettingsSection";

const TABS = [
  { key: "posts", label: "내 구인글" },
  { key: "applications", label: "내 지원" },
];

export default function MyPageScreen() {
  const router = useRouter();
  const [notifEnabled, setNotifEnabled] = useState(true);

  const user = MOCK_USER;
  const myPosts = MOCK_POSTS.filter((p) => MY_POST_IDS.includes(p.id));

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <AppHeader />
      <ScrollView showsVerticalScrollIndicator={false}>
        <ProfileSection
          user={user}
          myPostCount={myPosts.length}
          totalApplications={MOCK_APPLICATIONS.length}
          acceptedApplications={MOCK_APPLICATIONS.filter((a) => a.status === "ACCEPTED").length}
          onEditPress={() => router.push("/my-page/edit")}
        />

        <TabbedPager
          tabs={TABS}
          pages={[
            myPosts.length === 0 ? (
              <Text style={styles.emptyText}>등록한 구인글이 없어요.</Text>
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
            MOCK_APPLICATIONS.length === 0 ? (
              <Text style={styles.emptyText}>지원한 구인글이 없어요.</Text>
            ) : (
              MOCK_APPLICATIONS.map((app, i) => (
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
  versionText: {
    textAlign: "center",
    fontSize: 12,
    color: colors.textMuted,
    marginTop: 16,
  },
});
