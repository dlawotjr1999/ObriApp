export interface Career {
  id: number;
  organization: string;
  contexts: string;
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
