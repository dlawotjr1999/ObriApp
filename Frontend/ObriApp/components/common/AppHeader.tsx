import React from "react";
import { View, Image, StyleSheet, useWindowDimensions } from "react-native";
import { colors } from "@/constants/theme";

const headerImg = require("@/assets/images/header-img.png");

export default function AppHeader() {
  const { width: screenWidth } = useWindowDimensions();
  return (
    <View style={styles.header}>
      <Image
        source={headerImg}
        style={{ width: screenWidth * 0.65, height: 90 }}
        resizeMode="contain"
      />
    </View>
  );
}

const styles = StyleSheet.create({
  header: {
    height: 64,
    overflow: "hidden",
    alignItems: "center",
    justifyContent: "center",
    borderBottomWidth: StyleSheet.hairlineWidth,
    borderBottomColor: colors.border,
    backgroundColor: colors.background,
  },
});
