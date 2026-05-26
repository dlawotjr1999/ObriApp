import React from "react";
import { View, Text, Switch, StyleSheet } from "react-native";
import { colors } from "@/constants/theme";

interface ToggleFieldProps {
  label: string;
  value: boolean;
  onToggle: (value: boolean) => void;
}

export default function ToggleField({ label, value, onToggle }: ToggleFieldProps) {
  return (
    <View style={styles.container}>
      <Text style={styles.label}>{label}</Text>
      <Switch
        value={value}
        onValueChange={onToggle}
        trackColor={{ false: colors.border, true: colors.primary }}
        thumbColor={colors.surface}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    paddingVertical: 12,
    paddingHorizontal: 14,
    backgroundColor: colors.surface,
    borderWidth: 0.5,
    borderColor: colors.border,
    borderRadius: 10,
    marginBottom: 14,
  },
  label: {
    fontSize: 14,
    color: colors.textPrimary,
  },
});
