import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { colors } from "@/constants/theme";

interface LogoProps {
  size?: "large" | "small";
}

export default function Logo({ size = "large" }: LogoProps) {
  const isLarge = size === "large";

  return (
    <View style={styles.container}>
      <Text style={[styles.main, !isLarge && styles.mainSmall]}>poco</Text>
      <Text style={[styles.sub, !isLarge && styles.subSmall]}>a</Text>
      <Text style={[styles.main, !isLarge && styles.mainSmall]}>poco</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    alignItems: "center",
  },
  main: {
    fontFamily: "Georgia",
    fontSize: 42,
    fontStyle: "italic",
    color: colors.primary,
    letterSpacing: 2,
    lineHeight: 46,
  },
  mainSmall: {
    fontSize: 28,
    lineHeight: 32,
  },
  sub: {
    fontFamily: "Georgia",
    fontSize: 18,
    fontStyle: "italic",
    color: colors.primaryLight,
    letterSpacing: 6,
    marginVertical: 2,
  },
  subSmall: {
    fontSize: 14,
    letterSpacing: 4,
  },
});
