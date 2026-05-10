import React from "react";
import { View, Text, StyleSheet } from "react-native";
import { colors } from "@/constants/theme";


interface DividerWithTextProps {
  text: string;
}

export default function DividerWithText({ text }: DividerWithTextProps) {
  return (
    <View style={styles.container}>
      <View style={styles.line} />
      <Text style={styles.text}>{text}</Text>
      <View style={styles.line} />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    alignItems: "center",
    marginVertical: 20,
  },
  line: {
    flex: 1,
    height: 0.5,
    backgroundColor: colors.border,
  },
  text: {
    fontSize: 11,
    color: colors.textMuted,
    letterSpacing: 1,
    marginHorizontal: 12,
  },
});
