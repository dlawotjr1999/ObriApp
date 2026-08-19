# Obri

음대생 연주 아르바이트 매칭 플랫폼 **Obri**. Spring Boot 백엔드 + Expo(React Native) 프론트엔드 모노레포.

- 백엔드: [`backend/obri`](backend/obri)
- 프론트엔드: [`Frontend/ObriApp`](Frontend/ObriApp) — 로컬 환경 설정은 해당 README 참고

## 시크릿 파일

DB 접속정보·Firebase 서비스 계정 키·Firebase 앱 설정 파일 등은 저장소에 포함되지 않는다. 각 하위 프로젝트의 `.gitignore`가 이를 막고 있고, 대신 `.example` 템플릿을 커밋해뒀다.

- `backend/obri/src/main/resources/application-local.properties.example` → 복사해 `application-local.properties`로 사용
- `Frontend/ObriApp/.env.example` → 복사해 `.env`로 사용 (API 주소 + Firebase 웹 설정)
- `backend/obri/src/main/resources/firebase-service-account.json` → Firebase 콘솔에서 개별 다운로드, CI는 `FIREBASE_SERVICE_ACCOUNT_KEY` Secret으로 주입

새 시크릿이 필요한 파일을 추가할 때는 실제 값 대신 `.example` 템플릿을 먼저 커밋하고, 파일명을 각 프로젝트의 `.gitignore`에 등록하는 순서를 지킨다.
