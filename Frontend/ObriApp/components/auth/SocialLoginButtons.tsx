import React from "react";
import { View, Text, TouchableOpacity, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";

interface SocialLoginButtonsProps {
  onGooglePress: () => void;
  onApplePress: () => void;
}

export default function SocialLoginButtons({
  onGooglePress,
  onApplePress,
}: SocialLoginButtonsProps) {
  return (
    <View style={styles.row}>
      <TouchableOpacity style={styles.button} onPress={onGooglePress}>
        <Text style={styles.googleText}>G</Text>
      </TouchableOpacity>
      <TouchableOpacity style={styles.button} onPress={onApplePress}>
        <Ionicons name="logo-apple" size={22} color={colors.primary} />
      </TouchableOpacity>
    </View>
  );
}

const styles = StyleSheet.create({
  row: {
    flexDirection: "row",
    gap: 12,
  },
  button: {
    flex: 1,
    borderWidth: 0.5,
    borderColor: colors.border,
    borderRadius: 12,
    height: 48,
    alignItems: "center",
    justifyContent: "center",
    backgroundColor: colors.surface,
  },
  googleText: {
    fontSize: 18,
    fontWeight: "500",
    color: colors.primary,
  },
});
