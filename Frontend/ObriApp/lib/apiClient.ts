// 백엔드 API 호출 공통 레이어. 모든 응답이 { status, message, data } 포맷(CLAUDE.md 4장)이라
// 이 레이어에서 언랩하고, 실패 시 ApiError로 통일해 호출부가 매번 res.ok를 확인하지 않게 한다.
import { auth } from "@/lib/firebase";

const BASE_URL = process.env.EXPO_PUBLIC_API_URL;

export class ApiError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

interface ApiEnvelope<T> {
  status: number;
  message: string;
  data: T;
}

interface RequestOptions {
  method?: "GET" | "POST" | "PUT" | "PATCH" | "DELETE";
  body?: unknown;
  // 회원가입처럼 아직 로그인 상태가 아닌 요청만 false로 지정
  requiresAuth?: boolean;
}

// path 하나를 백엔드에 요청하고 data만 반환. 실패하면 ApiError를 throw
export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = "GET", body, requiresAuth = true } = options;

  const headers: Record<string, string> = { "Content-Type": "application/json" };

  if (requiresAuth) {
    const idToken = await auth.currentUser?.getIdToken();
    if (!idToken) {
      throw new ApiError(401, "로그인이 필요합니다");
    }
    headers.Authorization = `Bearer ${idToken}`;
  }

  const response = await fetch(`${BASE_URL}${path}`, {
    method,
    headers,
    body: body !== undefined ? JSON.stringify(body) : undefined,
  });

  const envelope: ApiEnvelope<T> = await response.json();

  if (!response.ok) {
    throw new ApiError(envelope.status ?? response.status, envelope.message ?? "요청을 처리할 수 없습니다");
  }

  return envelope.data;
}
