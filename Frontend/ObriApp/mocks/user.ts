import { UserProfile } from "@/types/user";

export const MOCK_USER: UserProfile = {
  id: 1,
  nickname: "홍길동",
  email: "hong@example.com",
  phoneNumber: "010-1234-5678",
  instrument: "바이올린",
  school: "한국예술종합학교",
  isGraduate: false,
  careers: [
    { id: 1, organization: "서울시립교향악단", contexts: "2023년 객원 연주" },
    { id: 2, organization: "코리안심포니오케스트라", contexts: "2024년 정기 연주회 참여" },
  ],
  createdAt: "2024-01-01T00:00:00",
};

// 마이페이지 "내 구인글"에 표시할 post id 목록
export const MY_POST_IDS = [1, 4];
