import React from "react";
import { View, FlatList, StyleSheet } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { colors } from "@/constants/theme";
import { MOCK_POSTS } from "@/mocks/posts";
import Logo from "@/components/common/Logo";
import EmptyState from "@/components/common/EmptyState";
import PostCard from "@/components/post/PostCard";

export default function ObriScreen() {
  const router = useRouter();

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <View style={styles.header}>
        <Logo size="small" />
      </View>

      <FlatList
        data={MOCK_POSTS}
        keyExtractor={(item) => String(item.id)}
        renderItem={({ item }) => (
          <PostCard
            post={item}
            onPress={(id) => router.push({ pathname: "/post/[id]", params: { id } })}
          />
        )}
        contentContainerStyle={styles.listContent}
        ItemSeparatorComponent={() => <View style={styles.separator} />}
        ListEmptyComponent={
          <EmptyState
            icon="document-text-outline"
            title="아직 등록된 구인글이 없어요"
            description="새로운 구인글이 등록되면 이곳에 표시됩니다."
          />
        }
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  header: {
    alignItems: "center",
    paddingVertical: 16,
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border,
  },
  listContent: {
    flexGrow: 1,
    padding: 16,
  },
  separator: {
    height: 12,
  },
});
