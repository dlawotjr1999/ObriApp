import React, { useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
} from "react-native";
import { Link } from "expo-router";
import { Ionicons } from "@expo/vector-icons";
import { colors } from "@/constants/theme";
import Logo from "@/components/common/Logo";
import ThemedInput from "@/components/common/ThemedInput";
import ThemedButton from "@/components/common/ThemedButton";
import DividerWithText from "@/components/common/DividerWithText";
import SocialLoginButtons from "@/components/auth/SocialLoginButtons";

export default function LoginScreen() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);

  const handleLogin = () => {
    // TODO: Firebase 인증
  };

  return (
    <View style={styles.container}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        keyboardShouldPersistTaps="handled"
        keyboardDismissMode="on-drag"
      >
        <View style={styles.logoArea}>
          <Logo />
        </View>

        <View style={styles.form}>
          <ThemedInput
            label="이메일"
            icon="mail-outline"
            placeholder="example@email.com"
            value={email}
            onChangeText={setEmail}
            keyboardType="email-address"
            autoCapitalize="none"
            autoCorrect={false}
          />

          <ThemedInput
            label="비밀번호"
            icon="lock-closed-outline"
            placeholder="비밀번호를 입력하세요"
            value={password}
            onChangeText={setPassword}
            secureTextEntry={!showPassword}
            rightElement={
              <TouchableOpacity
                onPress={() => setShowPassword(!showPassword)}
                hitSlop={{ top: 10, bottom: 10, left: 10, right: 10 }}
              >
                <Ionicons
                  name={showPassword ? "eye-off-outline" : "eye-outline"}
                  size={18}
                  color={colors.placeholder}
                />
              </TouchableOpacity>
            }
          />

          <TouchableOpacity style={styles.forgotPassword}>
            <Text style={styles.forgotPasswordText}>비밀번호를 잊으셨나요?</Text>
          </TouchableOpacity>

          <ThemedButton title="로그인" onPress={handleLogin} />

          <DividerWithText text="또는" />

          <SocialLoginButtons
            onGooglePress={() => {}}
            onApplePress={() => {}}
          />
        </View>

        <View style={styles.signupRow}>
          <Text style={styles.signupText}>계정이 없으신가요? </Text>
          <Link href="/(auth)/register" asChild>
            <TouchableOpacity>
              <Text style={styles.signupLink}>회원가입</Text>
            </TouchableOpacity>
          </Link>
        </View>
      </ScrollView>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  scrollContent: {
    flexGrow: 1,
    paddingHorizontal: 32,
  },
  logoArea: {
    marginTop: 120,
    marginBottom: 48,
  },
  form: {
    width: "100%",
  },
  forgotPassword: {
    alignSelf: "flex-end",
    marginBottom: 32,
    marginTop: 2,
  },
  forgotPasswordText: {
    fontSize: 12,
    color: colors.textMuted,
  },
  signupRow: {
    flexDirection: "row",
    justifyContent: "center",
    marginTop: "auto",
    marginBottom: 40,
  },
  signupText: {
    fontSize: 13,
    color: colors.textMuted,
  },
  signupLink: {
    fontSize: 13,
    color: colors.primary,
    fontWeight: "500",
  },
});
