export interface ManagerProfile {
  id: number;
  fullName: string;
  email: string;
  phone: string;
  agencyId?: number;
  agencyName?: string;
  agencyCountry?: string;
}

export interface UpdateProfileRequest {
  fullName: string;
  email: string;
  phone: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}
