# Obri Frontend (Expo)

음대생 연주 아르바이트 플랫폼 **Obri**의 모바일 앱. Expo Router 기반.

## 시작하기

```bash
npm install
npx expo start
```

## 로컬 환경 설정 (필수)

이 프로젝트는 백엔드 API 주소와 Firebase 설정을 로컬 파일로 주입받는다. 아래 파일들은 전부 `.gitignore` 대상 — 절대 커밋하지 않는다.

### 1. `.env`

```bash
cp .env.example .env
```

`EXPO_PUBLIC_API_URL`을 로컬에서 띄운 백엔드 주소(기본 `http://localhost:8080`)로 맞춘다. `EXPO_PUBLIC_` 접두어가 붙은 값은 빌드 시 클라이언트 번들에 그대로 인라인되므로, 여기엔 공개돼도 되는 값만 넣는다.

### 2. Firebase 네이티브 설정 파일

`@react-native-firebase`가 요구하는 두 파일은 저장소에 포함돼 있지 않다. Firebase 콘솔(프로젝트 설정 → 내 앱)에서 각자 다운로드해 아래 경로에 둔다.

| 플랫폼 | 파일명 | 위치 |
|---|---|---|
| Android | `google-services.json` | `Frontend/ObriApp/google-services.json` |
| iOS | `GoogleService-Info.plist` | `Frontend/ObriApp/GoogleService-Info.plist` |

접근 권한이 없다면 프로젝트 관리자에게 Firebase 콘솔 초대를 요청한다.

여러 명이 EAS로 빌드하는 단계부터는 로컬 파일 대신 `eas secret:create`로 등록하고 `eas.json`의 빌드 프로필에서 참조하는 방식으로 전환한다 — 파일을 각자 로컬에 복사해 다니지 않아도 된다.

## 개발 빌드 (EAS)

Firebase Phone Auth 등 네이티브 모듈이 필요한 기능은 Expo Go에서 동작하지 않는다. Development build가 필요하다.

```bash
npx eas build --profile development --platform android   # 또는 ios
```

## 파일 기반 라우팅

`app` 디렉터리 구조를 그대로 라우트로 사용한다. 자세한 내용은 [Expo Router 문서](https://docs.expo.dev/router/introduction) 참고.
