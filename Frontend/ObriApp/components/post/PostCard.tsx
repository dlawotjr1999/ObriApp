import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import { PostSummary } from "@/types/post";
import { formatEventDateTime } from "@/utils/datetime";
import IconText from "@/components/common/IconText";

interface PostCardProps {
  post: PostSummary;
  onPress?: (id: number) => void;
}

// instruments 배열을 "바이올린 2명 · 첼로 1명" 형태의 한 줄로 변환.
function formatInstruments(instruments: PostSummary["instruments"]): string {
  if (instruments.length === 0) return "악기 미정";
  return instruments
    .map(({ instrument, people }) => `${instrument} ${people}명`)
    .join(" · ");
}

export default function PostCard({ post, onPress }: PostCardProps) {
  return (
    <TouchableOpacity
      style={styles.card}
      activeOpacity={0.8}
      onPress={() => onPress?.(post.id)}
    >
      {/* 좌측 썸네일 (추후 이미지로 교체할 자리) */}
      <View style={styles.thumbnail}>
        <Ionicons name="musical-note" size={26} color={colors.primaryLight} />
      </View>

      {/* 우측 정보 */}
      <View style={styles.info}>
        <Text style={styles.title} numberOfLines={1}>
          {post.title}
        </Text>
        <View style={styles.metaList}>
          <IconText icon="calendar-outline" text={formatEventDateTime(post.eventAt)} />
          <IconText icon="location-outline" text={post.location} />
          <IconText icon="musical-notes-outline" text={formatInstruments(post.instruments)} />
        </View>
      </View>
    </TouchableOpacity>
  );
}

const styles = StyleSheet.create({
  card: {
    flexDirection: "row",
    backgroundColor: colors.surface,
    borderRadius: 14,
    borderWidth: StyleSheet.hairlineWidth,
    borderColor: colors.border,
    padding: 14,
    gap: 14,
  },
  thumbnail: {
    width: 56,
    height: 56,
    borderRadius: 10,
    backgroundColor: colors.background,
    alignItems: "center",
    justifyContent: "center",
  },
  info: {
    flex: 1,
    justifyContent: "center",
    gap: 8,
  },
  title: {
    fontSize: 15,
    fontWeight: "600",
    color: colors.textPrimary,
  },
  metaList: {
    gap: 4,
  },
});
