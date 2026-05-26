import React from "react";
import { View, Text, Image, StyleSheet } from "react-native";
import { colors } from "@/constants/theme";
import Logo from "@/components/common/Logo";

const musicBg = require("@/assets/images/music-bg.png");

export default function LoadingScreen() {
  return (
    <View style={styles.container}>
      <Image source={musicBg} style={styles.backgroundImage} />

      <View style={styles.content}>
        <Logo />

        <View style={styles.divider} />

        <Text style={styles.tagline}>음대생을 위한 연주 플랫폼</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  backgroundImage: {
    ...StyleSheet.absoluteFillObject,
    width: "100%",
    height: "100%",
    resizeMode: "cover",
  },
  content: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
  },
  divider: {
    width: 72,
    height: StyleSheet.hairlineWidth,
    backgroundColor: colors.primary,
    opacity: 0.3,
    marginTop: 28,
    marginBottom: 16,
  },
  tagline: {
    fontSize: 12,
    color: colors.textSecondary,
    letterSpacing: 4,
  },
});
