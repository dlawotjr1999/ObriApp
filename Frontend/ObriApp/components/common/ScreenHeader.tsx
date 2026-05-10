import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { useRouter } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";

interface ScreenHeaderProps {
  title: string;
  subtitle?: string;
  showBack?: boolean;
}

export default function ScreenHeader({
  title,
  subtitle,
  showBack = true,
}: ScreenHeaderProps) {
  const router = useRouter();

  return (
    <View style={styles.container}>
      {showBack && (
        <TouchableOpacity
          style={styles.backButton}
          onPress={() => router.back()}
          hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
        >
          <Ionicons name="arrow-back" size={22} color={colors.primary} />
        </TouchableOpacity>
      )}
      <Text style={styles.title}>{title}</Text>
      {subtitle && <Text style={styles.subtitle}>{subtitle}</Text>}
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginBottom: 28,
  },
  backButton: {
    marginBottom: 20,
  },
  title: {
    fontSize: 20,
    fontWeight: "500",
    color: colors.primary,
    marginBottom: 6,
  },
  subtitle: {
    fontSize: 13,
    color: colors.textMuted,
  },
});
