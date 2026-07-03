import React, { useState, useMemo } from "react";
import { View, FlatList, StyleSheet, TouchableOpacity, Text, Image, useWindowDimensions } from "react-native";
import { SafeAreaView, useSafeAreaInsets } from "react-native-safe-area-context";
import { useRouter } from "expo-router";
import { colors } from "@/constants/theme";
import { MOCK_POSTS } from "@/mocks/posts";
import EmptyState from "@/components/common/EmptyState";
import PostCard from "@/components/post/PostCard";
import FilterBar from "@/components/post/FilterBar";
import FilterSheet from "@/components/post/FilterSheet";
import { PostFilter, DEFAULT_FILTER } from "@/types/filter";

const headerImg = require("@/assets/images/header-img.png");

export default function ObriScreen() {
  const router = useRouter();
  const insets = useSafeAreaInsets();
  const { width: screenWidth } = useWindowDimensions();

  const [filter, setFilter] = useState<PostFilter>(DEFAULT_FILTER);
  const [sheetVisible, setSheetVisible] = useState(false);

  const filteredPosts = useMemo(() => {
    let result = [...MOCK_POSTS];

    if (filter.categories.length > 0) {
      result = result.filter((p) => filter.categories.includes(p.category));
    }
    if (filter.instruments.length > 0) {
      result = result.filter((p) =>
        p.instruments.some((i) => filter.instruments.includes(i.instrument))
      );
    }
    if (filter.regions.length > 0) {
      result = result.filter((p) =>
        filter.regions.some((r) => p.location.includes(r))
      );
    }
    if (filter.status.length > 0) {
      result = result.filter((p) => filter.status.includes(p.status));
    }
    if (filter.sort === "latest") {
      result = [...result].sort(
        (a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
      );
    }

    return result;
  }, [filter]);

  return (
    <SafeAreaView style={styles.container} edges={["top"]}>
      <View style={styles.header}>
        <Image
          source={headerImg}
          style={{ width: screenWidth * 0.65, height: 90 }}
          resizeMode="contain"
        />
      </View>

      <FilterBar
        filter={filter}
        onChange={setFilter}
        onOpenSheet={() => setSheetVisible(true)}
        onReset={() => setFilter(DEFAULT_FILTER)}
      />

      <View style={styles.resultRow}>
        <Text style={styles.resultText}>총 {filteredPosts.length}개</Text>
      </View>

      <FlatList
        data={filteredPosts}
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
            title="조건에 맞는 구인글이 없어요"
            description="필터를 조정하거나 나중에 다시 확인해 주세요."
          />
        }
      />

      <FilterSheet
        visible={sheetVisible}
        filter={filter}
        onApply={setFilter}
        onClose={() => setSheetVisible(false)}
      />

      {/* FAB */}
      <TouchableOpacity
        style={[styles.fab, { bottom: insets.bottom }]}
        onPress={() => router.push("/post/create")}
        activeOpacity={0.7}
      >
        <Text style={styles.fabText}>+</Text>
      </TouchableOpacity>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  header: {
    height: 64,
    overflow: "hidden",
    alignItems: "center",
    justifyContent: "center",
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border,
  },
  resultRow: {
    paddingHorizontal: 16,
    paddingTop: 12,
    paddingBottom: 4,
  },
  resultText: {
    fontSize: 12,
    color: colors.textMuted,
  },
  listContent: {
    flexGrow: 1,
    padding: 16,
  },
  separator: {
    height: 12,
  },
  fab: {
    position: "absolute",
    right: 24,
    width: 52,
    height: 52,
    borderRadius: 26,
    backgroundColor: colors.primary,
    justifyContent: "center",
    alignItems: "center",
    opacity: 0.8,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.2,
    shadowRadius: 4,
    elevation: 4,
  },
  fabText: {
    fontSize: 26,
    color: colors.background,
    lineHeight: 30,
  },
});
