import { ApplicationSummary } from "@/types/application";

export const MOCK_APPLICATIONS: ApplicationSummary[] = [
  {
    id: 1,
    post: {
      id: 2,
      title: "기업 행사 현악 4중주",
      category: "행사",
      eventAt: "2026-08-12T18:30:00",
      location: "서울 중구 OO호텔 그랜드볼룸",
      status: "PARTIALLY_CLOSED",
    },
    status: "PENDING",
    createdAt: "2026-07-02T10:00:00",
  },
  {
    id: 2,
    post: {
      id: 3,
      title: "추모 음악회 플루트·피아노 구인",
      category: "추모",
      eventAt: "2026-09-05T11:00:00",
      location: "경기 수원시 OO성당",
      status: "OPEN",
    },
    status: "ACCEPTED",
    createdAt: "2026-07-03T09:00:00",
  },
  {
    id: 3,
    post: {
      id: 5,
      title: "부산 결혼식 트리오 구인",
      category: "결혼",
      eventAt: "2026-08-22T15:00:00",
      location: "부산 해운대구 OO웨딩",
      status: "PARTIALLY_CLOSED",
    },
    status: "REJECTED",
    createdAt: "2026-06-30T08:00:00",
  },
];
