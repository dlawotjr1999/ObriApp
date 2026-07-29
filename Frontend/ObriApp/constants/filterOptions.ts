export const CATEGORIES = ["결혼", "행사", "추모", "레코딩", "기타"];

export const CONTEST_CATEGORIES = ["피아노", "현악", "관악", "성악", "타악", "기타"];

export const INSTRUMENTS = [
  "바이올린", "비올라", "첼로", "더블베이스",
  "플루트", "오보에", "클라리넷", "바순",
  "호른", "트럼펫", "트롬본", "튜바",
  "피아노", "하프", "타악기",
];

// 콩쿠르 카테고리엔 "성악"이 있어 악기 외 보컬 파트도 targetInstrument로 들어옴.
// 구인글(INSTRUMENTS)은 연주자 모집이라 보컬 파트가 필요 없어 별도로 분리.
export const CONTEST_INSTRUMENTS = [
  ...INSTRUMENTS,
  "소프라노", "메조소프라노", "알토", "테너", "바리톤", "베이스",
];

export const REGIONS = ["서울", "경기", "인천", "부산", "대구", "대전", "광주", "기타"];

export const STATUS_LABELS: Record<string, string> = {
  OPEN: "모집중",
  PARTIALLY_CLOSED: "부분마감",
  CLOSED: "마감",
};
