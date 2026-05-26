import React from "react";
import {
  View,
  Text,
  TextInput,
  StyleSheet,
  TextInputProps,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";

interface ThemedInputProps extends TextInputProps {
  label: string;
  icon: keyof typeof Ionicons.glyphMap;
  rightElement?: React.ReactNode;
}

export default function ThemedInput({
  label,
  icon,
  rightElement,
  ...inputProps
}: ThemedInputProps) {
  return (
    <View style={styles.container}>
      <Text style={styles.label}>{label}</Text>
      <View style={styles.inputWrapper}>
        <Ionicons
          name={icon}
          size={18}
          color={colors.placeholder}
          style={styles.icon}
        />
        <TextInput
          style={styles.input}
          placeholderTextColor={colors.placeholder}
          {...inputProps}
        />
        {rightElement}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    marginBottom: 16,
  },
  label: {
    fontSize: 12,
    color: colors.textSecondary,
    letterSpacing: 1,
    marginBottom: 6,
  },
  inputWrapper: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: colors.surface,
    borderWidth: 0.5,
    borderColor: colors.border,
    borderRadius: 10,
    paddingHorizontal: 14,
    height: 46,
  },
  icon: {
    marginRight: 10,
  },
  input: {
    flex: 1,
    fontSize: 14,
    color: colors.textPrimary,
  },
});
