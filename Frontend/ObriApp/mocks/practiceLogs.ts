import { PracticeLog } from "@/types/practiceLog";

export const MOCK_PRACTICE_LOGS: PracticeLog[] = [
  {
    id: 1,
    title: "Bach 무반주 파르티타 1번 연습",
    practicedAt: "2026-07-04T10:00:00",
    durationMinutes: 90,
    content:
      "알레망드 첫 번째 섹션 느린 템포로 반복. 활 속도 조절에 집중. 오른쪽 어깨 긴장 줄이는 연습.",
    createdAt: "2026-07-04T12:00:00",
  },
  {
    id: 2,
    title: "음계 & 아르페지오 기초 훈련",
    practicedAt: "2026-07-03T09:30:00",
    durationMinutes: 45,
    content:
      "3옥타브 G장조, D장조. 메트로놈 ♩=60부터 시작해서 ♩=100까지 단계적으로 올림.",
    createdAt: "2026-07-03T11:00:00",
  },
  {
    id: 3,
    title: "Brahms 소나타 1번 1악장 합맞춤",
    practicedAt: "2026-07-02T14:00:00",
    durationMinutes: 120,
    content:
      "피아노 반주와 합맞춤. 템포 조율 필요. 발전부 전환 지점 더 연습 필요.",
    createdAt: "2026-07-02T16:30:00",
  },
  {
    id: 4,
    title: "시창·청음 자습",
    practicedAt: "2026-07-01T18:00:00",
    durationMinutes: 30,
    content: "5도권 화음 진행 청음. 단조 선율 시창 연습.",
    createdAt: "2026-07-01T18:45:00",
  },
];
