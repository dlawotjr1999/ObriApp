export interface CareerEntry {
  organization: string;
  contexts: string;
}

export interface Career extends CareerEntry {
  id: number;
}

export interface UserProfile {
  id: number;
  nickname: string;
  email: string;
  phoneNumber: string;
  instrument: string;
  school: string;
  isGraduate: boolean;
  careers: Career[];
  createdAt: string;
}
